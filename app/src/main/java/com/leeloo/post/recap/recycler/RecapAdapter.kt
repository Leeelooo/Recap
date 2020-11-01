package com.leeloo.post.recap.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.leeloo.post.R
import com.leeloo.post.base.recycler.BaseStateAdapter
import com.leeloo.post.base.recycler.ErrorViewHolder
import com.leeloo.post.base.recycler.RecyclerState
import com.leeloo.post.vo.VKPost
import com.leeloo.post.vo.VKProfile

class RecapAdapter(
    private val onPostClick: (VKPost) -> Unit,
    private val onRetry: () -> Unit
) : BaseStateAdapter<VKPost, PostViewHolder>(onRetry) {
    private val users: MutableList<VKProfile> = mutableListOf()
    private lateinit var currentPost: VKPost

    fun updateData(
        items: List<VKPost>,
        users: List<VKProfile>,
        currentPost: VKPost?,
        state: RecyclerState
    ) {
        this.users.clear()
        this.users.addAll(users)
        if (currentPost != null) {
            this.currentPost = currentPost
        }
        super.updateData(items, state)
    }

    override fun createErrorViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder = ErrorViewHolder(
        inflater.inflate(
            R.layout.item_error_horizontal,
            parent,
            false
        ),
        onRetry
    )

    override fun getDataViewHolder(inflater: LayoutInflater, parent: ViewGroup): PostViewHolder =
        PostViewHolder(
            inflater.inflate(R.layout.item_post, parent, false),
            onPostClick
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostViewHolder && dataList.isNotEmpty()) {
            holder.bindData(
                dataList[position],
                users.firstOrNull { it.id == dataList[position].sourceId },
                currentPost == dataList[position]
            )
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is PostViewHolder) {
            holder.clear()
        }
    }
}