package com.leeloo.post.base.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.leeloo.post.R

abstract class BaseAdapter<T, VH : DataViewHolder<T>> :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val dataList: MutableList<T> = mutableListOf()

    protected abstract fun getDataViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): VH

    protected open fun getEmptyViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder = EmptyViewHolder(
        inflater.inflate(
            R.layout.item_empty,
            parent,
            false
        )
    )

    fun updateData(newData: List<T>) {
        dataList.clear()
        dataList.addAll(newData)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        when (dataList.size) {
            0 -> 3
            else -> 4 + position
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (dataList.size) {
            0 -> getEmptyViewHolder(layoutInflater, parent)
            else -> getDataViewHolder(layoutInflater, parent)
        }
    }

    override fun getItemCount(): Int =
        if (dataList.isEmpty()) 1 else dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (dataList.isNotEmpty()) {
            (holder as VH).bindData(dataList[position])
        }
    }

}