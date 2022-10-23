package com.kt.apps.xembongda.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.kt.apps.xembongda.App
import com.kt.apps.xembongda.MainActivity
import com.kt.apps.xembongda.di.main.MainModule
import com.kt.apps.xembongda.di.player.ActivityPlayerModule
import com.kt.apps.xembongda.di.viewmodels.ViewModelModule
import com.kt.apps.xembongda.repository.IFootballMatchRepository
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.ui.MainInteractors
import com.kt.apps.xembongda.ui.MainViewModelFactory
import com.kt.apps.xembongda.usecase.GetLinkStreamForMatch
import com.kt.apps.xembongda.usecase.GetListFootballMatch
import dagger.BindsInstance
import dagger.Component
import dagger.Component.Factory
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Named

@Component(
    dependencies = [
        BaseComponents::class,
    ],
    modules = [
        MainModule::class,
        AndroidSupportInjectionModule::class,
        ViewModelModule::class,
        PlayerModule::class,
        ActivityPlayerModule::class
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