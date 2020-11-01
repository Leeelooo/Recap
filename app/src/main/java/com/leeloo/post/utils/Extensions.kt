package com.leeloo.post.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.leeloo.post.vo.VKGroup
import com.leeloo.post.vo.VKProfile

fun <T, R> LiveData<T>.map(transformation: (T) -> R): LiveData<R> =
    Transformations.map(this, transformation)

fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> =
    Transformations.distinctUntilChanged(this)

fun getPostSource(users: List<VKProfile>?, groups: List<VKGroup>?, id: Long): VKPostSource? {
    return if (id < 0) {
        val group = groups?.firstOrNull { it.id == -id }
        if (group == null) {
            null
        } else {
            VKPostSource(
                iconUrl = group.fullPhotoUrl,
                name = group.name
            )
        }
    } else {
        val user = users?.firstOrNull { it.id == id }
        if (user == null) {
            null
        } else {
            VKPostSource(
                iconUrl = user.fullPhotoUrl,
                name = "${user.firstName} ${user.lastName}"
            )
        }
    }
}

data class VKPostSource(
    val iconUrl: String,
    val name: String
)