package com.leeloo.post.vo

import com.squareup.moshi.Json

data class VKLikes(
    val count: Int,
    @Json(name = "user_likes") val userLikes: Int,
    @Json(name = "can_like") val canLike: Int,
    @Json(name = "can_publish") val canPublish: Int
)