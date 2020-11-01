package com.leeloo.post.create

import com.leeloo.post.base.MviEffect

sealed class CreateEffect : MviEffect {

    object ChangeCommentsEffect : CreateEffect()

    object ChangeNotificationsEffect : CreateEffect()

    object ChangePostPublicityEffect : CreateEffect()

    object OpenDelayPickerDialogEffect : CreateEffect()

    object CloseDialogsEffect : CreateEffect()

    data class SetDelayTimeEffect(
        val timeDelay: Long
    ) : CreateEffect()

    data class SetAttachDialogModeEffect(
        val mode: Int
    ) : CreateEffect()

    object OpenMentionsDialogEffect : CreateEffect()

    data class LoadedMentionListEffect(
        val mentionList: List<String>
    ) : CreateEffect()

    object OpenSettingsDialogEffect : CreateEffect()

    object PostSentEffect : CreateEffect()

    class PostSendingErrorEffect(
        val throwable: Throwable
    ) : CreateEffect()

}