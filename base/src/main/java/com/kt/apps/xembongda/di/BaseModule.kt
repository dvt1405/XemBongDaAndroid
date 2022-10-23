package com.kt.apps.xembongda.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class BaseModule {
    companion object {
        private const val SHARE_PREF_NAME = "extra:share_pref_name"
    }

    @Provides
    @BaseScope
    @Named(SHARE_PREF_NAME)
    fun providesSharePreferenceName(context: Context): String = context.packageName


    @Provides
    @BaseScope
    fun providesSharePreference(
        @Named(SHARE_PREF_NAME) name: String,
        context: Context
    ): SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)


}