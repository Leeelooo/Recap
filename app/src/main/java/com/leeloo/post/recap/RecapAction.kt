package com.leeloo.post.recap

import com.leeloo.post.base.MviAction
import com.leeloo.post.vo.VKPost

sealed class RecapAction : MviAction {

    object LoadInitialPostsAction : RecapAction()

    object LoadPostsAction : RecapAction()

    object ShowMapAction : RecapAction()

    data class SetCurrentPostAction(
        val post: VKPost
    ) : RecapAction()

    object LogoutAction : RecapAction()

}