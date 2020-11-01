package com.leeloo.post.commands

import android.net.Uri
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKHttpPostCall
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class VKServerUploadInfo(val uploadUrl: String, val albumId: Int, val userId: Int)

class VKSaveInfo(
    val id: Int,
    val albumId: Int,
    val ownerId: Int
) {
    fun getAttachment() = "photo${ownerId}_$id"
}

class VKFileUploadInfo(val server: String, val photo: String, val hash: String)

class VKWallPostCommand(
    private val message: String,
    private val ownerId: Int = 0,
    private val friendsOnly: Boolean = false,
    private val fromGroup: Boolean = false,
    private val closeComments: Boolean = false,
    private val muteNotifications: Boolean = false,
    private val publishDate: Long = 0L
) : ApiCommand<Int>() {
    override fun onExecute(manager: VKApiManager): Int {
        val callBuilder = VKMethodCall.Builder()
            .method("wall.post")
            .args("friends_only", if (friendsOnly) 1 else 0)
            .args("from_group", if (fromGroup) 1 else 0)
            .args("publish_date", publishDate)
            .args("close_comments", if (closeComments) 1 else 0)
            .args("mute_notifications", if (muteNotifications) 1 else 0)
            .version(manager.config.version)
        if (message.isNotEmpty())
            callBuilder.args("message", message)


        if (ownerId != 0)
            callBuilder.args("owner_id", ownerId)

//        val uploadInfo = getServerUploadInfo(manager)
//        val attachments = uploadPhoto(photo, uploadInfo, manager)

//        callBuilder.args("attachments", attachments)

        return manager.execute(callBuilder.build(), ResponseApiParser())
    }

    private fun getServerUploadInfo(manager: VKApiManager): VKServerUploadInfo {
        val uploadInfoCall = VKMethodCall.Builder()
            .method("photos.getWallUploadServer")
            .version(manager.config.version)
            .build()
        return manager.execute(uploadInfoCall, ServerUploadInfoParser())
    }

    private fun uploadPhoto(
        uri: Uri,
        serverUploadInfo: VKServerUploadInfo,
        manager: VKApiManager
    ): String {
        val fileUploadCall = VKHttpPostCall.Builder()
            .url(serverUploadInfo.uploadUrl)
            .args("photo", uri, "image.jpg")
            .timeout(TimeUnit.MINUTES.toMillis(5))
            .retryCount(RETRY_COUNT)
            .build()
        val fileUploadInfo = manager.execute(fileUploadCall, null, FileUploadInfoParser())

        val saveCall = VKMethodCall.Builder()
            .method("photos.saveWallPhoto")
            .args("server", fileUploadInfo.server)
            .args("photo", fileUploadInfo.photo)
            .args("hash", fileUploadInfo.hash)
            .version(manager.config.version)
            .build()

        val saveInfo = manager.execute(saveCall, SaveInfoParser())

        return saveInfo.getAttachment()
    }

    companion object {
        const val RETRY_COUNT = 3
    }

    private class ResponseApiParser : VKApiResponseParser<Int> {
        override fun parse(response: String): Int {
            try {
                return JSONObject(response).getJSONObject("response").getInt("post_id")
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }

    private class ServerUploadInfoParser : VKApiResponseParser<VKServerUploadInfo> {
        override fun parse(response: String): VKServerUploadInfo {
            try {
                val joResponse = JSONObject(response).getJSONObject("response")
                return VKServerUploadInfo(
                    uploadUrl = joResponse.getString("upload_url"),
                    albumId = joResponse.getInt("album_id"),
                    userId = joResponse.getInt("user_id")
                )
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }

    private class FileUploadInfoParser : VKApiResponseParser<VKFileUploadInfo> {
        override fun parse(response: String): VKFileUploadInfo {
            try {
                val joResponse = JSONObject(response)
                return VKFileUploadInfo(
                    server = joResponse.getString("server"),
                    photo = joResponse.getString("photo"),
                    hash = joResponse.getString("hash")
                )
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }

    private class SaveInfoParser : VKApiResponseParser<VKSaveInfo> {
        override fun parse(response: String): VKSaveInfo {
            try {
                val joResponse = JSONObject(response).getJSONArray("response").getJSONObject(0)
                return VKSaveInfo(
                    id = joResponse.getInt("id"),
                    albumId = joResponse.getInt("album_id"),
                    ownerId = joResponse.getInt("owner_id")
                )
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}