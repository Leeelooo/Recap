package com.leeloo.post.recap

import com.leeloo.post.base.MviIntent
import com.leeloo.post.vo.VKPost

sealed class RecapIntent : MviIntent {

    object InitialIntent : RecapIntent()

    object MapLoadedIntent : RecapIntent()

    object ReloadPostsIntent : RecapIntent()

    data class ChoosePostIntent(
        val post: VKPost
    ) : RecapIntent()

    object LogoutIntent : RecapIntent()

}