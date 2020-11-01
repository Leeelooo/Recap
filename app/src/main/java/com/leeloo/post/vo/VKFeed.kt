package com.leeloo.post.vo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VKFeed(
    val items: List<VKPost>,
    val profiles: List<VKProfile>,
    val groups: List<VKGroup>,
    @Json(name = "next_from") val nextFrom: String?,
)