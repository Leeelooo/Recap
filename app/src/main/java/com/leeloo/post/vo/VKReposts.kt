package com.leeloo.post.vo

import com.squareup.moshi.Json

data class VKReposts(
    val count: Int,
    @Json(name = "user_reposted") val userReposted: Int
)