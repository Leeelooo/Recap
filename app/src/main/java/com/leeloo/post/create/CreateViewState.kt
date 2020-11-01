package com.leeloo.post.create

import com.leeloo.post.base.MviViewState
import com.leeloo.post.vo.VKAttachment

data class CreateViewState(
    val isPublicPost: Boolean = true,
    val isDelayPickerOpened: Boolean = false,
    val isMentionListOpened: Boolean = false,
    val isSettingsOpened: Boolean = false,
    val isPostSent: Boolean = false,
    val isCommentsDisabled: Boolean = false,
    val isNotificationDisabled: Boolean = false,
    val postSendingError: Throwable? = null,
    val attachmentMode: Int = 0,
    val timeDelay: Long = 0,
    val mentionsList: List<String> = emptyList(),
    val attachments: List<VKAttachment> = emptyList()
) : MviViewState