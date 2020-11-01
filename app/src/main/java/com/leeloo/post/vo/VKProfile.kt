package com.leeloo.post.vo

import com.squareup.moshi.Json

data class VKProfile(
    val id: Long,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String,
    @Json(name = "screen_name") val screenName: String?,
    @Json(name = "photo_50") val previewPhotoUrl: String,
    @Json(name = "photo_100") val fullPhotoUrl: String
)