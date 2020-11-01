package com.leeloo.post.base

import androidx.lifecycle.LiveData

interface MviView<
        VS : MviViewState,
        I : MviIntent> {
    fun render(viewState: VS)
    fun intents(): LiveData<I>
}