package com.leeloo.post.di

import com.leeloo.post.recap.RecapUseCase
import com.leeloo.post.recap.RecapUseCaseImpl
import com.leeloo.post.vo.VKFeed
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(
    ApplicationComponent::class
)
object AppModule {
    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun provideFeedAdapter(moshi: Moshi): JsonAdapter<VKFeed> = moshi.adapter(VKFeed::class.java)

    @Provides
    fun provideRecapUseCase(feedAdapter: JsonAdapter<VKFeed>): RecapUseCase =
        RecapUseCaseImpl(feedAdapter)
}