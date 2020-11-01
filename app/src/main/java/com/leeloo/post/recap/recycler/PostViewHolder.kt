package com.leeloo.post.recap.recycler

import android.view.View
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.leeloo.post.R
import com.leeloo.post.base.recycler.DataViewHolder
import com.leeloo.post.vo.VKPost
import com.leeloo.post.vo.VKProfile

class PostViewHolder(
    view: View,
    onClick: (VKPost) -> Unit
) : DataViewHolder<VKPost>(view) {
    private val imageView = view.findViewById<ShapeableImageView>(R.id.item_post_user_image)
    private lateinit var post: VKPost

    init {
        view.setOnClickListener { onClick(post) }
    }

    override fun bindData(data: VKPost) {
        throw UnsupportedOperationException("Wrong bind function")
    }

    fun bindData(data: VKPost, user: VKProfile?, isCurrent: Boolean) {
        this.post = data
        if (user != null) {
            Glide.with(imageView)
                .load(user.fullPhotoUrl)
                .into(imageView)
        }
        if (isCurrent) {
            imageView.setStrokeWidthResource(R.dimen.current_item_stroke_width)
        } else {
            imageView.setStrokeWidthResource(R.dimen.post_item_stroke_width)
        }
    }

    fun clear() {
        Glide.with(imageView).clear(imageView)
    }

}