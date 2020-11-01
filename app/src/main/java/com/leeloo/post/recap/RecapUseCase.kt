package com.leeloo.post.recap

import com.leeloo.post.commands.NewsFeedCommand
import com.leeloo.post.utils.Result
import com.leeloo.post.vo.VKFeed
import com.squareup.moshi.JsonAdapter
import com.vk.api.sdk.internal.await

interface RecapUseCase {
    suspend fun getFriendsPosts(): Result<VKFeed>
}

class RecapUseCaseImpl(
    private val feedAdapter: JsonAdapter<VKFeed>
) : RecapUseCase {
    override suspend fun getFriendsPosts(): Result<VKFeed> {
        val currentTimeStamp = System.currentTimeMillis() / 1000L
        return try {
            Result.Success(
                NewsFeedCommand(
                    filters = listOf("post"),
                    returnBanned = 0,
                    startTime = currentTimeStamp - WEEK_IN_MILLIS, //looks like vk is showing only 1-2 weeks old posts for friends, but we still do to double check
                    endTime = currentTimeStamp,
                    maxPhotos = 5,
                    sourceIds = listOf("friends"),
                    startFrom = "",
                    count = 100,
                    fields = emptyList(),
                    section = "",
                    feedConverter = feedAdapter
                ).await()
            )
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    companion object {
        private const val WEEK_IN_MILLIS = 604800L
    }

}
