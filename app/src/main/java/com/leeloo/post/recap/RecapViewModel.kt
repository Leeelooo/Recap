package com.leeloo.post.recap

import androidx.hilt.lifecycle.ViewModelInject
import com.leeloo.post.base.BaseViewModel
import com.leeloo.post.utils.Result

class RecapViewModel @ViewModelInject constructor(
    private val recapUseCase: RecapUseCase
) : BaseViewModel<RecapViewState, RecapEffect, RecapIntent, RecapAction>() {
    override fun initialState(): RecapViewState = RecapViewState()

    override fun intentInterpreter(intent: RecapIntent): RecapAction =
        when (intent) {
            is RecapIntent.InitialIntent -> RecapAction.LoadInitialPostsAction
            is RecapIntent.ReloadPostsIntent -> RecapAction.LoadPostsAction
            is RecapIntent.MapLoadedIntent -> RecapAction.ShowMapAction
            is RecapIntent.ChoosePostIntent -> RecapAction.SetCurrentPostAction(intent.post)
            is RecapIntent.LogoutIntent -> RecapAction.LogoutAction
        }

    override suspend fun performAction(action: RecapAction): RecapEffect =
        when (action) {
            is RecapAction.ShowMapAction -> RecapEffect.MapLoadedEffect
            is RecapAction.SetCurrentPostAction -> RecapEffect.CurrentPostChosenEffect(action.post)
            is RecapAction.LogoutAction -> RecapEffect.UserLogoutEffect
            is RecapAction.LoadInitialPostsAction -> {
                addIntermediateEffect(RecapEffect.InitialFeedLoadingEffect)
                when (val result = recapUseCase.getFriendsPosts()) {
                    is Result.Success -> RecapEffect.FeedLoadedEffect(result.data)
                    is Result.Error -> RecapEffect.FeedLoadingErrorEffect(result.throwable)
                }
            }
            is RecapAction.LoadPostsAction -> {
                addIntermediateEffect(RecapEffect.FeedLoadingEffect)
                when (val result = recapUseCase.getFriendsPosts()) {
                    is Result.Success -> RecapEffect.FeedLoadedEffect(result.data)
                    is Result.Error -> RecapEffect.FeedLoadingErrorEffect(result.throwable)
                }
            }
        }

    override fun stateReducer(oldState: RecapViewState, effect: RecapEffect): RecapViewState =
        when (effect) {
            is RecapEffect.InitialFeedLoadingEffect -> RecapViewState(
                isUserLoggedIn = true,
                isMapLoading = true,
                isFeedLoading = true,
                friendsFeed = null,
                feedLoadingError = null,
                currentPost = null
            )
            is RecapEffect.FeedLoadingEffect -> RecapViewState(
                isUserLoggedIn = true,
                isMapLoading = oldState.isMapLoading,
                isFeedLoading = true,
                friendsFeed = null,
                feedLoadingError = null,
                currentPost = null
            )
            is RecapEffect.MapLoadedEffect -> RecapViewState(
                isUserLoggedIn = true,
                isMapLoading = false,
                isFeedLoading = oldState.isFeedLoading,
                friendsFeed = oldState.friendsFeed,
                feedLoadingError = oldState.feedLoadingError,
                currentPost = oldState.currentPost
            )
            is RecapEffect.FeedLoadedEffect -> RecapViewState(
                isUserLoggedIn = true,
                isMapLoading = oldState.isMapLoading,
                isFeedLoading = false,
                friendsFeed = effect.feed,
                feedLoadingError = null,
                currentPost = if (effect.feed.items.isEmpty()) null else effect.feed.items[0]
            )
            is RecapEffect.FeedLoadingErrorEffect -> RecapViewState(
                isUserLoggedIn = true,
                isMapLoading = oldState.isMapLoading,
                isFeedLoading = false,
                friendsFeed = null,
                feedLoadingError = effect.throwable,
                currentPost = null
            )
            is RecapEffect.CurrentPostChosenEffect -> RecapViewState(
                isUserLoggedIn = true,
                isMapLoading = oldState.isMapLoading,
                isFeedLoading = false,
                friendsFeed = oldState.friendsFeed,
                feedLoadingError = null,
                currentPost = effect.post
            )
            is RecapEffect.UserLogoutEffect -> RecapViewState(
                isUserLoggedIn = false,
                isMapLoading = oldState.isMapLoading,
                isFeedLoading = false,
                friendsFeed = null,
                feedLoadingError = null,
                currentPost = null
            )
        }

}