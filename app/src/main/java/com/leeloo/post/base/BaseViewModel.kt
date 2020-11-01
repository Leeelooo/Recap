package com.leeloo.post.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leeloo.post.utils.distinctUntilChanged
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseViewModel<
        VS : MviViewState,
        E : MviEffect,
        I : MviIntent,
        A : MviAction> : ViewModel(), MviViewModel<VS, I> {
    protected abstract fun initialState(): VS
    protected abstract fun intentInterpreter(intent: I): A
    protected abstract suspend fun performAction(action: A): E
    protected abstract fun stateReducer(oldState: VS, effect: E): VS

    protected val viewStateLiveData = MediatorLiveData<VS>().also {
        it.value = initialState()
    }

    private val _effectLiveData = MediatorLiveData<E>().also { effectLiveData ->
        viewStateLiveData.addSource(effectLiveData) {
            viewStateLiveData.value = stateReducer(viewStateLiveData.value!!, it)
        }
    }

    private var clearLastIntentSource: (() -> Unit)? = null

    protected fun addIntermediateEffect(effect: E) {
        _effectLiveData.postValue(effect)
    }

    override fun states(): LiveData<VS> = viewStateLiveData.distinctUntilChanged()

    override fun processIntents(intents: LiveData<I>) {
        clearLastIntentSource?.let { it() }

        _effectLiveData.addSource(intents) {
            viewModelScope.launch(Dispatchers.IO) {
                _effectLiveData.postValue(performAction(intentInterpreter(it)))
            }
        }

        clearLastIntentSource = { _effectLiveData.removeSource(intents) }
    }

    override fun onCleared() {
        super.onCleared()
        clearLastIntentSource?.let { it() }
    }
}