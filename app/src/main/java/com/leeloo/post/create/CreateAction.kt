package com.leeloo.post.create

import com.leeloo.post.base.MviAction
import com.leeloo.post.vo.VKAttachment

sealed class CreateAction : MviAction {

    object ChangeCommentsAction : CreateAction()

    object ChangeNotificationsAction : CreateAction()

    object ChangePostPublicityAction : CreateAction()

    object CloseDialogsAction : CreateAction()

    object OpenMentionsListAction : CreateAction()

    object OpenDelaySelectionAction : CreateAction()

    data class SetDelayTimeAction(
        val delayTime: Long
    ) : CreateAction()

    data class ChangeAttachmentDialogModeAction(
        val mode: Int
    ) : CreateAction()

    data class AttachFileAction(
        val attachment: VKAttachment
    ) : CreateAction()

    object OpenSettingsListAction : CreateAction()

    data class SendPostAction(
        val message: String
    ) : CreateAction()

}