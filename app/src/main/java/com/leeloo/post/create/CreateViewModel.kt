package com.leeloo.post.create

import com.leeloo.post.base.BaseViewModel
import com.leeloo.post.commands.VKWallPostCommand
import com.vk.api.sdk.VkResult
import com.vk.api.sdk.internal.awaitResult

class CreateViewModel : BaseViewModel<CreateViewState, CreateEffect, CreateIntent, CreateAction>() {
    override fun initialState(): CreateViewState = CreateViewState()

    override fun intentInterpreter(intent: CreateIntent): CreateAction =
        when (intent) {
            is CreateIntent.TogglePostPublicityIntent -> CreateAction.ChangePostPublicityAction
            is CreateIntent.OpenMentionsOptionsIntent -> CreateAction.OpenMentionsListAction
            is CreateIntent.ChoosedMentionIntent -> CreateAction.CloseDialogsAction
            is CreateIntent.DismissMentionsDialogIntent -> CreateAction.CloseDialogsAction
            is CreateIntent.OpenDelayPickerIntent -> CreateAction.OpenDelaySelectionAction
            is CreateIntent.ChoosedDelayTimePicker -> CreateAction.SetDelayTimeAction(intent.delayTime)
            is CreateIntent.ClearDelayTimeIntent -> CreateAction.SetDelayTimeAction(0L)
            is CreateIntent.DismissDelayPickerIntent -> CreateAction.CloseDialogsAction
            is CreateIntent.OpenFilesToAttachIntent -> CreateAction.ChangeAttachmentDialogModeAction(
                1
            )
            is CreateIntent.SelectFileTypeToAttachIntent -> CreateAction.ChangeAttachmentDialogModeAction(
                intent.mode
            )
            is CreateIntent.ChoosedFileToAttachIntent -> CreateAction.AttachFileAction(intent.attachment)
            is CreateIntent.DismissAttachDialogIntent -> CreateAction.CloseDialogsAction
            is CreateIntent.OpenPostSettingsIntent -> CreateAction.OpenSettingsListAction
            is CreateIntent.DismissSettingsDialogIntent -> CreateAction.CloseDialogsAction
            is CreateIntent.SendPostIntent -> CreateAction.SendPostAction(intent.message)
            CreateIntent.ToggleCommentsIntent -> CreateAction.ChangeCommentsAction
            CreateIntent.ToggleNotificationsIntent -> CreateAction.ChangeNotificationsAction
        }

    override suspend fun performAction(action: CreateAction): CreateEffect =
        when (action) {
            CreateAction.ChangePostPublicityAction -> CreateEffect.ChangePostPublicityEffect
            CreateAction.CloseDialogsAction -> CreateEffect.CloseDialogsEffect
            CreateAction.OpenMentionsListAction -> CreateEffect.OpenMentionsDialogEffect
            CreateAction.OpenDelaySelectionAction -> CreateEffect.OpenDelayPickerDialogEffect
            is CreateAction.SetDelayTimeAction -> CreateEffect.SetDelayTimeEffect(action.delayTime)
            is CreateAction.ChangeAttachmentDialogModeAction -> CreateEffect.SetAttachDialogModeEffect(
                action.mode
            )
            is CreateAction.AttachFileAction -> throw UnsupportedOperationException("666")
            CreateAction.OpenSettingsListAction -> CreateEffect.OpenSettingsDialogEffect
            is CreateAction.SendPostAction -> {
                val result = VKWallPostCommand(
                    message = action.message,
                    friendsOnly = !viewStateLiveData.value!!.isPublicPost,
                    closeComments = viewStateLiveData.value!!.isCommentsDisabled,
                    publishDate = viewStateLiveData.value!!.timeDelay,
                    muteNotifications = viewStateLiveData.value!!.isNotificationDisabled
                ).awaitResult()
                when(result) {
                    is VkResult.Success -> CreateEffect.PostSentEffect
                    is VkResult.Failure -> CreateEffect.PostSendingErrorEffect(result.exception)
                }
            }
            CreateAction.ChangeCommentsAction -> CreateEffect.ChangeCommentsEffect
            CreateAction.ChangeNotificationsAction -> CreateEffect.ChangeNotificationsEffect
        }

    override fun stateReducer(oldState: CreateViewState, effect: CreateEffect): CreateViewState =
        when (effect) {
            CreateEffect.ChangePostPublicityEffect -> CreateViewState(
                isPublicPost = !oldState.isPublicPost,
                isDelayPickerOpened = oldState.isDelayPickerOpened,
                isMentionListOpened = oldState.isMentionListOpened,
                isSettingsOpened = oldState.isSettingsOpened,
                isNotificationDisabled = oldState.isNotificationDisabled,
                isCommentsDisabled = oldState.isCommentsDisabled,
                attachmentMode = oldState.attachmentMode,
                timeDelay = oldState.timeDelay,
                mentionsList = oldState.mentionsList,
                attachments = oldState.attachments
            )
            CreateEffect.OpenDelayPickerDialogEffect -> CreateViewState(
                isPublicPost = oldState.isPublicPost,
                isDelayPickerOpened = true,
                isMentionListOpened = oldState.isMentionListOpened,
                isSettingsOpened = oldState.isSettingsOpened,
                isNotificationDisabled = oldState.isNotificationDisabled,
                isCommentsDisabled = oldState.isCommentsDisabled,
                attachmentMode = oldState.attachmentMode,
                timeDelay = oldState.timeDelay,
                mentionsList = oldState.mentionsList,
                attachments = oldState.attachments
            )
            CreateEffect.CloseDialogsEffect -> CreateViewState(
                isPublicPost = oldState.isPublicPost,
                isDelayPickerOpened = false,
                isMentionListOpened = false,
                isSettingsOpened = false,
                isNotificationDisabled = oldState.isNotificationDisabled,
                isCommentsDisabled = oldState.isCommentsDisabled,
                attachmentMode = 0,
                timeDelay = oldState.timeDelay,
                mentionsList = oldState.mentionsList,
                attachments = oldState.attachments
            )
            is CreateEffect.SetDelayTimeEffect -> CreateViewState(
                isPublicPost = oldState.isPublicPost,
                isDelayPickerOpened = oldState.isDelayPickerOpened,
                isMentionListOpened = oldState.isMentionListOpened,
                isSettingsOpened = oldState.isSettingsOpened,
                isNotificationDisabled = oldState.isNotificationDisabled,
                isCommentsDisabled = oldState.isCommentsDisabled,
                attachmentMode = oldState.attachmentMode,
                timeDelay = effect.timeDelay,
                mentionsList = oldState.mentionsList,
                attachments = oldState.attachments
            )
            is CreateEffect.SetAttachDialogModeEffect -> CreateViewState(
                isPublicPost = oldState.isPublicPost,
                isDelayPickerOpened = oldState.isDelayPickerOpened,
                isMentionListOpened = oldState.isMentionListOpened,
                isSettingsOpened = oldState.isSettingsOpened,
                isNotificationDisabled = oldState.isNotificationDisabled,
                isCommentsDisabled = oldState.isCommentsDisabled,
                attachmentMode = effect.mode,
                timeDelay = oldState.timeDelay,
                mentionsList = oldState.mentionsList,
                attachments = oldState.attachments
            )
            CreateEffect.OpenMentionsDialogEffect -> CreateViewState(
                isPublicPost = oldState.isPublicPost,
                isDelayPickerOpened = oldState.isDelayPickerOpened,
                isMentionListOpened = true,
                isSettingsOpened = oldState.isSettingsOpened,
                isNotificationDisabled = oldState.isNotificationDisabled,
                isCommentsDisabled = oldState.isCommentsDisabled,
                attachmentMode = oldState.attachmentMode,
                timeDelay = oldState.timeDelay,
                mentionsList = oldState.mentionsList,
                attachments = oldState.attachments
            )
            is CreateEffect.LoadedMentionListEffect -> CreateViewState(
                isPublicPost = oldState.isPublicPost,
                isDelayPickerOpened = oldState.isDelayPickerOpened,
                isMentionListOpened = oldState.isMentionListOpened,
                isSettingsOpened = oldState.isSettingsOpened,
                isNotificationDisabled = oldState.isNotificationDisabled,
                isCommentsDisabled = oldState.isCommentsDisabled,
                attachmentMode = oldState.attachmentMode,
                timeDelay = oldState.timeDelay,
                mentionsList = effect.mentionList,
                attachments = oldState.attachments
            )
            CreateEffect.OpenSettingsDialogEffect -> CreateViewState(
                isPublicPost = oldState.isPublicPost,
                isDelayPickerOpened = oldState.isDelayPickerOpened,
                isMentionListOpened = oldState.isMentionListOpened,
                isSettingsOpened = true,
                isNotificationDisabled = oldState.isNotificationDisabled,
                isCommentsDisabled = oldState.isCommentsDisabled,
                attachmentMode = oldState.attachmentMode,
                timeDelay = oldState.timeDelay,
                mentionsList = oldState.mentionsList,
                attachments = oldState.attachments
            )
            CreateEffect.PostSentEffect -> CreateViewState(
                isPublicPost = oldState.isPublicPost,
                isDelayPickerOpened = oldState.isDelayPickerOpened,
                isMentionListOpened = oldState.isMentionListOpened,
                isSettingsOpened = oldState.isSettingsOpened,
                isPostSent = true,
                isNotificationDisabled = oldState.isNotificationDisabled,
                isCommentsDisabled = oldState.isCommentsDisabled,
                attachmentMode = oldState.attachmentMode,
                timeDelay = oldState.timeDelay,
                mentionsList = oldState.mentionsList,
                attachments = oldState.attachments
            )
            is CreateEffect.PostSendingErrorEffect -> CreateViewState(
                isPublicPost = oldState.isPublicPost,
                isDelayPickerOpened = oldState.isDelayPickerOpened,
                isMentionListOpened = oldState.isMentionListOpened,
                isSettingsOpened = oldState.isSettingsOpened,
                isNotificationDisabled = oldState.isNotificationDisabled,
                isCommentsDisabled = oldState.isCommentsDisabled,
                attachmentMode = oldState.attachmentMode,
                timeDelay = oldState.timeDelay,
                mentionsList = oldState.mentionsList,
                attachments = oldState.attachments,
                postSendingError = null
            )
            CreateEffect.ChangeCommentsEffect ->  CreateViewState(
                isPublicPost = oldState.isPublicPost,
                isDelayPickerOpened = oldState.isDelayPickerOpened,
                isMentionListOpened = oldState.isMentionListOpened,
                isSettingsOpened = oldState.isSettingsOpened,
                isNotificationDisabled = oldState.isNotificationDisabled,
                isCommentsDisabled = !oldState.isCommentsDisabled,
                attachmentMode = oldState.attachmentMode,
                timeDelay = oldState.timeDelay,
                mentionsList = oldState.mentionsList,
                attachments = oldState.attachments,
                postSendingError = null
            )
            CreateEffect.ChangeNotificationsEffect ->  CreateViewState(
                isPublicPost = oldState.isPublicPost,
                isDelayPickerOpened = oldState.isDelayPickerOpened,
                isMentionListOpened = oldState.isMentionListOpened,
                isSettingsOpened = oldState.isSettingsOpened,
                isNotificationDisabled = !oldState.isNotificationDisabled,
                isCommentsDisabled = oldState.isCommentsDisabled,
                attachmentMode = oldState.attachmentMode,
                timeDelay = oldState.timeDelay,
                mentionsList = oldState.mentionsList,
                attachments = oldState.attachments,
                postSendingError = null
            )
        }

}