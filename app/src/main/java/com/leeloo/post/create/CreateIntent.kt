package com.leeloo.post.create

import com.leeloo.post.base.MviIntent
import com.leeloo.post.vo.VKAttachment

sealed class CreateIntent : MviIntent {

    object ToggleCommentsIntent : CreateIntent()

    object ToggleNotificationsIntent : CreateIntent()

    object TogglePostPublicityIntent : CreateIntent()

    object OpenMentionsOptionsIntent : CreateIntent()

    object ChoosedMentionIntent : CreateIntent()

    object DismissMentionsDialogIntent : CreateIntent()

    object OpenDelayPickerIntent : CreateIntent()

    data class ChoosedDelayTimePicker(
        val delayTime: Long
    ) : CreateIntent()

    object DismissDelayPickerIntent : CreateIntent()

    object ClearDelayTimeIntent : CreateIntent()

    object OpenFilesToAttachIntent : CreateIntent()

    data class SelectFileTypeToAttachIntent(
        val mode: Int
    ) : CreateIntent()

    data class ChoosedFileToAttachIntent(
        val attachment: VKAttachment
    ) : CreateIntent()

    object DismissAttachDialogIntent : CreateIntent()

    object OpenPostSettingsIntent : CreateIntent()

    object DismissSettingsDialogIntent : CreateIntent()

    data class SendPostIntent(
        val message: String
    ) : CreateIntent()

}