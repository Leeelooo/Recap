package com.leeloo.post.vo

import org.json.JSONObject

//nice structure awesome documentation
sealed class VKAttachment {

    data class VKPhotoAttachment(
        val photoUrl: String,
        val width: Int,
        val height: Int
    )

    data class VKOtherAttachment(
        val attachmentIcon: String,
        val name: String,
        val additional: String
    )

}

fun List<Any>?.getPhotoAttachments(): List<VKAttachment.VKPhotoAttachment> {
    if (this == null) {
        return emptyList()
    }
    return this.map { JSONObject(it as Map<String, Any>) }
        .filter { listOf("photo", "posted_photo").contains(it.optString("type")) }
        .map {
            val attachment = it.getJSONObject(it.optString("type"))
            when (it.optString("type")) {
                "photo" -> {
                    val sizes = attachment.getJSONArray("sizes")
                    val photoSize = sizes.getJSONObject(minOf(sizes.length() - 1, 3))
                    VKAttachment.VKPhotoAttachment(
                        photoSize.optString("url"),
                        photoSize.optInt("width"),
                        photoSize.optInt("height")
                    )
                }
                "posted_photo" -> {
                    VKAttachment.VKPhotoAttachment(
                        attachment.optString("photo_604"),
                        604,
                        604
                    )
                }
                else -> throw IllegalStateException("we filtered it")
            }
        }
}

fun List<Any>?.getOtherAttachments(): List<VKAttachment.VKOtherAttachment> {
    if (this == null) {
        return emptyList()
    }
    val list = mutableListOf<VKAttachment.VKOtherAttachment>()

    return list
}

