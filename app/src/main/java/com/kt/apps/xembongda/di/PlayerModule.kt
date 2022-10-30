package com.kt.apps.xembongda.di

import android.content.Context
import com.kt.apps.xembongda.di.main.MainActivityComponents
import com.kt.apps.xembongda.di.main.MainActivityScope
import com.kt.apps.xembongda.player.AudioFocusManager
import com.kt.apps.xembongda.player.ExoPlayerManager
import dagger.Module
import dagger.Provides

@Module()
class PlayerModule {

    @Provides
    @AppScope
    fun provideAudioManger(context: Context) = AudioFocusManager(context)

    @Provides
    @AppScope
    fun provideExoPlayerManager(
        context: Context,
        audioFocusManager: AudioFocusManager
    ) = ExoPlayerManager(context, audioFocusManager)
}