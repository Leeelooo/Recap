package com.leeloo.post.recap.recycler

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.leeloo.post.R
import com.leeloo.post.base.recycler.DataViewHolder
import com.leeloo.post.vo.VKAttachment
import com.leeloo.post.widget.AspectImageView

class PhotoAttachmentViewHolder(
    view: View
) : DataViewHolder<VKAttachment.VKPhotoAttachment>(view) {
    private val image = view as AspectImageView

    override fun bindData(data: VKAttachment.VKPhotoAttachment) {
        image.aspectRatio = data.height.toFloat().div(data.width)
        Glide.with(image)
            .applyDefaultRequestOptions(
                RequestOptions().placeholder(R.color.colorGray)
            )
            .load(data.photoUrl)
            .into(image)
    }

    fun clear() {
        Glide.with(image).clear(image)
    }

}