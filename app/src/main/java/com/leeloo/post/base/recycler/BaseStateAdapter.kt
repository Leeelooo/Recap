package com.leeloo.post.base.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.leeloo.post.R

enum class RecyclerState(val viewType: Int) {
    LOADING(100000),
    ERROR(200000),
    EMPTY(300000),
    ITEM(1)
}

abstract class BaseStateAdapter<T, VH : DataViewHolder<T>>(
    private val onRetry: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    protected val dataList: MutableList<T> = mutableListOf()
    private var state = RecyclerState.LOADING

    protected abstract fun getDataViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): VH

    protected open fun createLoadingViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder = EmptyViewHolder(
        inflater.inflate(
            R.layout.item_loading,
            parent,
            false
        )
    )

    protected open fun createEmptyViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder = EmptyViewHolder(
        inflater.inflate(
            R.layout.item_empty,
            parent,
            false
        )
    )

    protected open fun createErrorViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder = ErrorViewHolder(
        inflater.inflate(
            R.layout.item_error,
            parent,
            false
        ),
        onRetry
    )

    fun updateData(newData: List<T>, state: RecyclerState) {
        dataList.clear()
        dataList.addAll(newData)
        this.state = state
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        when (state) {
            RecyclerState.ITEM -> position
            else -> state.viewType
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (state) {
            RecyclerState.LOADING -> createLoadingViewHolder(layoutInflater, parent)
            RecyclerState.ERROR -> createErrorViewHolder(layoutInflater, parent)
            RecyclerState.EMPTY -> createEmptyViewHolder(layoutInflater, parent)
            RecyclerState.ITEM -> getDataViewHolder(layoutInflater, parent)
        }
    }

    override fun getItemCount() = if (dataList.isNotEmpty()) dataList.size else 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (dataList.isNotEmpty()) {
            (holder as VH).bindData(dataList[position])
        } else {
            if (holder.itemView.layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                (holder.itemView.layoutParams
                        as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
            }
        }
    }

}