package com.leeloo.post.recap

import com.leeloo.post.base.MviViewState
import com.leeloo.post.vo.VKFeed
import com.leeloo.post.vo.VKPost

data class RecapViewState(
    val isUserLoggedIn: Boolean = false,
    val isMapLoading: Boolean = false,
    val isFeedLoading: Boolean = false,
    val feedLoadingError: Throwable? = null,
    val friendsFeed: VKFeed? = null,
    val currentPost: VKPost? = null
) : MviViewState