package com.leeloo.post.recap

import com.leeloo.post.base.MviEffect
import com.leeloo.post.vo.VKFeed
import com.leeloo.post.vo.VKPost

sealed class RecapEffect : MviEffect {

    object InitialFeedLoadingEffect : RecapEffect()

    object FeedLoadingEffect : RecapEffect()

    object MapLoadedEffect : RecapEffect()

    data class FeedLoadedEffect(
        val feed: VKFeed
    ) : RecapEffect()

    data class FeedLoadingErrorEffect(
        val throwable: Throwable
    ) : RecapEffect()

    data class CurrentPostChosenEffect(
        val post: VKPost
    ) : RecapEffect()

    object UserLogoutEffect : RecapEffect()

}