package com.leeloo.post.vo

import com.squareup.moshi.Json

data class VKComment(
    val count: Int,
    @Json(name = "can_post") val canPost: Int
)
