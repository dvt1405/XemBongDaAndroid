package com.kt.apps.xembongda.di

import android.content.Context
import com.kt.apps.xembongda.App
import com.kt.apps.xembongda.di.main.MainModule
import com.kt.apps.xembongda.di.player.ActivityPlayerModule
import com.kt.apps.xembongda.di.viewmodels.ViewModelModule
import com.kt.apps.xembongda.di.webview.WebViewModule
import com.kt.apps.xembongda.repository.IFootballMatchRepository
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@Component(
    dependencies = [
        BaseComponents::class,
    ],
    modules = [
        MainModule::class,
        AndroidSupportInjectionModule::class,
        ViewModelModule::class,
        PlayerModule::class,
        ActivityPlayerModule::class,
        WebViewModule::class
    ]
)
@AppScope
interface AppComponents : AndroidInjector<App> {
    fun map(): Map<FootballRepoSourceFrom, IFootballMatchRepository>

    @Component.Factory
    interface Factory {

        fun create(
            baseComponents: BaseComponents,
            @BindsInstance context: Context,
        ): AppComponents
    }

}