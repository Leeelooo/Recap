package com.leeloo.post.vo

data class VKLocation(
    val type: String,
    val coordinates: String,
    val place: VKPlace
)

data class VKPlace(
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val address: String
)