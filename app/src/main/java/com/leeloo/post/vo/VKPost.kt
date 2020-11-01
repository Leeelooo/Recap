package com.leeloo.post.vo

import com.squareup.moshi.Json

data class VKPost(
    @Json(name = "post_id") val postId: Long,
    @Json(name = "source_id") val sourceId: Long,
    val date: Long,
    val text: String?,
    val comments: VKComment?,
    val likes: VKLikes?,
    val reposts: VKReposts?,
    val geo: VKLocation?,
    val attachments: List<Any>?,
    @Json(name = "copy_history") val copyPost: List<VKPostCopy>?
)

data class VKPostCopy(
    val date: Long,
    val text: String?,
    @Json(name = "owner_id") val source: Long,
    val attachments: List<Any>?
)