package com.leeloo.post.recap.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.leeloo.post.R
import com.leeloo.post.base.recycler.BaseAdapter
import com.leeloo.post.vo.VKAttachment

class OtherAttachmentAdapter :
    BaseAdapter<VKAttachment.VKOtherAttachment, OtherAttachmentViewHolder>() {

    override fun getDataViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): OtherAttachmentViewHolder = OtherAttachmentViewHolder(
        inflater.inflate(
            R.layout.item_other_attachment,
            parent,
            false
        )
    )

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is OtherAttachmentViewHolder) {
            holder.clear()
        }
    }
}