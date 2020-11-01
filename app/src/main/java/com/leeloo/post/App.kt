package com.leeloo.post

import android.app.Application
import com.vk.api.sdk.BuildConfig
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiConfig
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.*

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        VK.setConfig(
            VKApiConfig(
                version = resources.getString(R.string.vk_api_version),
                lang = Locale.getDefault().language,
                context = this,
                validationHandler = null
            )
        )
    }

}