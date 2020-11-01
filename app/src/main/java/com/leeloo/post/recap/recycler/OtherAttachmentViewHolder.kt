package com.leeloo.post.recap.recycler

import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView
import com.leeloo.post.R
import com.leeloo.post.base.recycler.DataViewHolder
import com.leeloo.post.vo.VKAttachment

class OtherAttachmentViewHolder(
    view: View
) : DataViewHolder<VKAttachment.VKOtherAttachment>(view) {
    private val attachmentIcon =
        view.findViewById<ShapeableImageView>(R.id.item_other_attachment_image)
    private val attachmentName = view.findViewById<TextView>(R.id.item_other_attachment_name)
    private val attachmentAdditional =
        view.findViewById<TextView>(R.id.item_other_attachment_additional)

    override fun bindData(data: VKAttachment.VKOtherAttachment) {
        Glide.with(attachmentIcon)
            .applyDefaultRequestOptions(
                RequestOptions().placeholder(R.color.colorGray)
            )
            .load(data.attachmentIcon)
            .into(attachmentIcon)
        attachmentName.text = data.name
        attachmentAdditional.text = data.additional
    }

    fun clear() {
        Glide.with(attachmentIcon).clear(attachmentIcon)
    }

}