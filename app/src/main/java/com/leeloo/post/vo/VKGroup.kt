package com.leeloo.post.vo

import com.squareup.moshi.Json

data class VKGroup(
    val id: Long,
    val name: String,
    val type: String,
    @Json(name = "screen_name") val screenName: String?,
    @Json(name = "photo_100") val previewPhotoUrl: String,
    @Json(name = "photo_200") val fullPhotoUrl: String
)