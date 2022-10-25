package com.kt.apps.xembongda.di

import android.app.Application
import android.content.Context
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kt.apps.xembongda.repository.IAuthenticateRepository
import com.kt.apps.xembongda.repository.IFootballMatchRepository
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.usecase.GetLinkStreamForMatch
import com.kt.apps.xembongda.usecase.GetListFootballMatch
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        RepositoryModule::class,
        BaseModule::class,
        FirebaseModule::class,
        StorageModule::class
    ]
)
@BaseScope
interface BaseComponents {

    fun remoteConfig(): FirebaseRemoteConfig
    fun map(): Map<FootballRepoSourceFrom, IFootballMatchRepository>
    fun authenRepo(): IAuthenticateRepository


    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(app: Context): Builder

        fun repositoryModule(repositoryModule: RepositoryModule): Builder

        fun baseModule(baseModule: BaseModule): Builder

        fun firebaseModule(firebaseModule: FirebaseModule): Builder

        fun build(): BaseComponents

    }


}