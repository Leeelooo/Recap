package com.leeloo.post.recap

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.leeloo.post.R
import com.leeloo.post.base.BaseActivity
import com.leeloo.post.base.recycler.RecyclerState
import com.leeloo.post.create.CreateActivity
import com.leeloo.post.recap.recycler.*
import com.leeloo.post.utils.getPostSource
import com.leeloo.post.vo.getOtherAttachments
import com.leeloo.post.vo.getPhotoAttachments
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_recap.*
import kotlinx.android.synthetic.main.bottom_sheet_post.*
import kotlinx.android.synthetic.main.login_view.*
import timber.log.Timber

@AndroidEntryPoint
class RecapActivity : BaseActivity<RecapViewState, RecapIntent>() {
    override val layoutResourceId: Int = R.layout.activity_recap
    override val viewModel: RecapViewModel by viewModels()

    private lateinit var adapter: RecapAdapter
    private lateinit var photoAdapter: PhotoAttachmentAdapter
    private lateinit var otherAdapter: OtherAttachmentAdapter
    private lateinit var copyPhotoAdapter: PhotoAttachmentAdapter
    private lateinit var copyOtherAdapter: OtherAttachmentAdapter

    private lateinit var postBottomBehavior: BottomSheetBehavior<View>
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap

    private val loginIntents = MutableLiveData<RecapIntent>().also { intents ->
        _intentLiveData.addSource(intents) {
            _intentLiveData.value = it
        }
    }
    private val recapIntents = MutableLiveData<RecapIntent>().also { intents ->
        _intentLiveData.addSource(intents) {
            _intentLiveData.value = it
        }
    }

    private val tokenTracker = object : VKTokenExpiredHandler {
        override fun onTokenExpired() {
            loginIntents.value = RecapIntent.LogoutIntent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VK.addTokenExpiredHandler(tokenTracker)
        if (savedInstanceState == null && VK.isLoggedIn()) {
            loginIntents.value = RecapIntent.InitialIntent
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                loginIntents.value = RecapIntent.InitialIntent
            }

            override fun onLoginFailed(errorCode: Int) {
                Timber.e("Error auth code $errorCode")
            }
        }

        if (!VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun initViews() {
        postBottomBehavior = BottomSheetBehavior.from(bottom_sheet_post)

        this.mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        login_button.setOnClickListener {
            VK.login(this, VK_SCOPES)
        }
        create_post_btn.setOnClickListener {
            startActivity(Intent(this, CreateActivity::class.java))
        }
        bottom_sheet_dismiss.setOnClickListener {
            postBottomBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        adapter = RecapAdapter(
            { recapIntents.value = RecapIntent.ChoosePostIntent(it) },
            { loginIntents.value = RecapIntent.ReloadPostsIntent }
        )
        appbar_posts.adapter = adapter
        appbar_posts.addItemDecoration(
            RecapItemDecoration(
                resources.getDimensionPixelSize(R.dimen.gutter_default).div(2)
            )
        )
        appbar_posts.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        appbar_posts.setHasFixedSize(true)

        photoAdapter = PhotoAttachmentAdapter()
        bottom_sheet_photo_attachments.adapter = photoAdapter
        bottom_sheet_photo_attachments.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        bottom_sheet_photo_attachments.addItemDecoration(
            PhotoAttachmentDecoration(
                resources.getDimensionPixelSize(R.dimen.margin_default)
            )
        )

        otherAdapter = OtherAttachmentAdapter()
        bottom_sheet_other_attachments.adapter = otherAdapter
        bottom_sheet_other_attachments.setHasFixedSize(true)

        copyPhotoAdapter = PhotoAttachmentAdapter()
        bottom_sheet_copy_photo_attachments.adapter = copyPhotoAdapter
        bottom_sheet_copy_photo_attachments.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        bottom_sheet_copy_photo_attachments.addItemDecoration(
            PhotoAttachmentDecoration(
                resources.getDimensionPixelSize(R.dimen.margin_default)
            )
        )

        copyOtherAdapter = OtherAttachmentAdapter()
        bottom_sheet_copy_other_attachments.adapter = copyOtherAdapter
        bottom_sheet_copy_other_attachments.setHasFixedSize(true)
    }

    override fun render(viewState: RecapViewState) {
        val state = when {
            viewState.isFeedLoading -> RecyclerState.LOADING
            viewState.feedLoadingError != null -> RecyclerState.ERROR
            viewState.friendsFeed?.items?.isEmpty() ?: false -> RecyclerState.EMPTY
            else -> RecyclerState.ITEM
        }
        adapter.updateData(
            items = viewState.friendsFeed?.items ?: emptyList(),
            users = viewState.friendsFeed?.profiles ?: emptyList(),
            currentPost = viewState.currentPost,
            state = state
        )

        login_view.isVisible = !viewState.isUserLoggedIn
        create_post_btn.isVisible = viewState.isUserLoggedIn
        appbar_posts.isVisible = viewState.isUserLoggedIn
        supportFragmentManager.beginTransaction().apply {
            if (viewState.isUserLoggedIn) {
                show(mapFragment).commitAllowingStateLoss()
                if (!this@RecapActivity::googleMap.isInitialized) {
                    mapFragment.getMapAsync {
                        this@RecapActivity.googleMap = it
                        recapIntents.value = RecapIntent.MapLoadedIntent
                        it.setOnMarkerClickListener {
                            postBottomBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                            true
                        }
                    }
                }
            } else {
                hide(mapFragment).commitAllowingStateLoss()
            }
        }

        if (this@RecapActivity::googleMap.isInitialized) {
            googleMap.clear()
        }

        if (!viewState.isMapLoading && viewState.currentPost != null && viewState.currentPost.geo != null) {
            val userPhotoLink = viewState.friendsFeed?.profiles
                ?.firstOrNull { it.id == viewState.currentPost.sourceId }
                ?.fullPhotoUrl
            val postLocation = LatLng(
                viewState.currentPost.geo.place.latitude,
                viewState.currentPost.geo.place.longitude
            )
            createAndSetCustomMarker(
                userPhotoLink,
                postLocation
            )
        }
        showBottomSheet(viewState)
    }

    private fun showBottomSheet(viewState: RecapViewState) {
        val photoAttachments = viewState.currentPost?.attachments.getPhotoAttachments()
        val otherAttachments = viewState.currentPost?.attachments.getOtherAttachments()
        val copyPhotoAttachments =
            viewState.currentPost?.copyPost?.firstOrNull()?.attachments.getPhotoAttachments()
        val copyOtherAttachments =
            viewState.currentPost?.copyPost?.firstOrNull()?.attachments.getOtherAttachments()

        when {
            viewState.currentPost != null -> {
                val user = getPostSource(
                    viewState.friendsFeed?.profiles,
                    viewState.friendsFeed?.groups,
                    viewState.currentPost.sourceId
                )
                Glide.with(bottom_sheet_user_icon)
                    .load(user?.iconUrl)
                    .into(bottom_sheet_user_icon)
                bottom_sheet_post_time.text =
                    DateUtils.getRelativeTimeSpanString(
                        viewState.currentPost.date * 1000,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS
                    )
                bottom_sheet_post_time.setOnClickListener {
                    val browserIntent =
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://vk.com/wall${viewState.currentPost.sourceId}_${viewState.currentPost.postId}")
                        )
                    startActivity(browserIntent)
                }
                bottom_sheet_user_name.text = user?.name
                bottom_sheet_post_text.isVisible = !viewState.currentPost.text.isNullOrEmpty()
                bottom_sheet_post_text.text = viewState.currentPost.text
                bottom_sheet_likes_text.isVisible = viewState.currentPost.likes != null
                bottom_sheet_likes_btn.isVisible = viewState.currentPost.likes != null
                bottom_sheet_likes_text.text =
                    viewState.currentPost.likes?.count.toString()
                if (viewState.currentPost.copyPost?.firstOrNull() != null) {
                    val copySource = getPostSource(
                        viewState.friendsFeed?.profiles,
                        viewState.friendsFeed?.groups,
                        viewState.currentPost.copyPost.firstOrNull()!!.source
                    )
                    Glide.with(bottom_sheet_copy_user_icon)
                        .load(copySource?.iconUrl)
                        .into(bottom_sheet_copy_user_icon)
                    bottom_sheet_copy_user_name.text = copySource?.name
                    bottom_sheet_copy_post_text.isVisible =
                        !viewState.currentPost.copyPost.firstOrNull()?.text.isNullOrEmpty()
                    bottom_sheet_copy_post_text.text =
                        viewState.currentPost.copyPost.firstOrNull()?.text
                    bottom_sheet_copy_post_time.text =
                        DateUtils.getRelativeTimeSpanString(
                            viewState.currentPost.copyPost.firstOrNull()!!.date * 1000,
                            System.currentTimeMillis(),
                            DateUtils.MINUTE_IN_MILLIS
                        )
                }
                copy_post.isVisible = viewState.currentPost.copyPost?.firstOrNull() != null
                postBottomBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            else -> {
                postBottomBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        bottom_sheet_photo_attachments.isVisible = photoAttachments.isNotEmpty()
        bottom_sheet_other_attachments.isVisible = otherAttachments.isNotEmpty()

        photoAdapter.updateData(photoAttachments)
        otherAdapter.updateData(otherAttachments)

        bottom_sheet_copy_photo_attachments.isVisible = copyPhotoAttachments.isNotEmpty()
        bottom_sheet_copy_other_attachments.isVisible = copyOtherAttachments.isNotEmpty()

        copyPhotoAdapter.updateData(copyPhotoAttachments)
        copyOtherAdapter.updateData(copyOtherAttachments)
    }

    private fun createAndSetCustomMarker(
        resourceUrl: String?,
        postLocation: LatLng
    ) {
        val cameraPosition = CameraPosition.Builder()
            .target(postLocation)
            .zoom(15F)
            .bearing(0F)
            .tilt(25F)
            .build()

        val markerSize = resources.getDimensionPixelSize(R.dimen.marker_dimen)
        val markerCornersSize = resources.getDimensionPixelSize(R.dimen.margin_default)
        Glide.with(this)
            .asBitmap()
            .load(resourceUrl)
            .apply(
                RequestOptions()
                    .override(markerSize, markerSize)
                    .transform(RoundedCorners(markerCornersSize))
            )
            .into(object : CustomViewTarget<FloatingActionButton, Bitmap>(create_post_btn) {
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(postLocation)
                    )
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(postLocation)
                            .icon(BitmapDescriptorFactory.fromBitmap(resource))
                    )
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }

                override fun onResourceCleared(placeholder: Drawable?) {
                }
            })
    }

    companion object {
        private val VK_SCOPES = listOf(VKScope.WALL, VKScope.PHOTOS, VKScope.VIDEO, VKScope.FRIENDS)
    }

}