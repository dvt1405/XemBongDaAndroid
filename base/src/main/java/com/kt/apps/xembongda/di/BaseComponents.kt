package com.kt.apps.xembongda.di

import android.content.Context
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kt.apps.xembongda.api.BinhLuan90PhutApi
import com.kt.apps.xembongda.di.config.HighLightConfig
import com.kt.apps.xembongda.repository.*
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.storage.IKeyValueStorage
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        RepositoryModule::class,
        BaseModule::class,
        FirebaseModule::class,
        BindsAbstractModule::class,
        HighLightModule::class,
        LiveScoreModule::class
    ]
)
@BaseScope
interface BaseComponents {

    fun remoteConfig(): FirebaseRemoteConfig
    fun map(): Map<FootballRepoSourceFrom, @JvmSuppressWildcards IFootballMatchRepository>
    fun authenRepo(): IAuthenticateRepository
    fun commentRepo(): ICommentRepository
    fun api(): BinhLuan90PhutApi
    fun iKeyValueStorage(): IKeyValueStorage
    fun iHighLightRepo(): Map<HighLightConfig.Source, @JvmSuppressWildcards IHighLightRepository>
    fun iLiveScores(): ILiveScoresRepository


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