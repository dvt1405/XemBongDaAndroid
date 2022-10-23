package com.kt.apps.xembongda.di.player

import com.kt.apps.xembongda.ui.PlayerActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityPlayerModule {

    @ContributesAndroidInjector
    abstract fun playerActivity(): PlayerActivity
}