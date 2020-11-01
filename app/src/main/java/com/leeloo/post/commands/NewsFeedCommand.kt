package com.leeloo.post.commands

import com.leeloo.post.vo.VKFeed
import com.squareup.moshi.JsonAdapter
import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject

class NewsFeedCommand(
    filters: List<String>,
    returnBanned: Int,
    startTime: Long,
    endTime: Long,
    maxPhotos: Int,
    sourceIds: List<String>,
    startFrom: String,
    count: Int,
    fields: List<String>,
    section: String,
    private val feedConverter: JsonAdapter<VKFeed>
) : VKRequest<VKFeed>("newsfeed.get") {

    init {
        if (filters.isNotEmpty()) {
            addParam("filters", filters.joinToString())
        }
        addParam("return_banned", returnBanned)
        addParam("start_time", startTime)
        addParam("end_time", endTime)
        addParam("max_photos", maxPhotos)
        if (sourceIds.isNotEmpty()) {
            addParam("source_ids", sourceIds.joinToString())
        }
        if (startFrom.isNotEmpty()) {
            addParam("start_from", startFrom)
        }
        addParam("count", count)
        if (fields.isNotEmpty()) {
            addParam("fields", fields.joinToString())
        }
        if (section.isNotEmpty()) {
            addParam("section", section)
        }
    }

    override fun parse(r: JSONObject): VKFeed =
        feedConverter.fromJson(r.optString("response")) ?: throw IllegalStateException("Json not well formed")

}