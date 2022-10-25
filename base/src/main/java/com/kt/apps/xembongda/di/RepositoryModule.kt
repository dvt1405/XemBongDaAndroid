package com.kt.apps.xembongda.di

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kt.apps.xembongda.di.mapkey.FootballRepositoryMapKey
import com.kt.apps.xembongda.repository.IAuthenticateRepository
import com.kt.apps.xembongda.repository.IFootballMatchRepository
import com.kt.apps.xembongda.repository.auth.AuthenticateRepositoryImpl
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.repository.config.FootballRepositoryConfig
import com.kt.apps.xembongda.repository.footbalmatch.Football91Repository
import com.kt.apps.xembongda.repository.footbalmatch.MitomRepository
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Named
import javax.inject.Qualifier

@Module
class RepositoryModule {
    companion object {
        private const val SOURCE_91_PHUT = "91phut"
        private const val SOURCE_MITOM = "mitom"
    }

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Source91Phut

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Regex91Phut

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class SourceMitom

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Source91PhutConfig

    @Qualifier
    @Retention
    annotation class SourceMitomConfig

    @Provides
    @Source91Phut
    fun provides91PhutUrl(config: FirebaseRemoteConfig) = config.getString(SOURCE_91_PHUT)

    @Provides
    @Regex91Phut
    fun providesRegex91Phut(config: FirebaseRemoteConfig) = "(?<=urlStream\\s=\\s\").*?(?=\")"

    @Provides
    @SourceMitom
    fun providesMiTomPhutUrl(config: FirebaseRemoteConfig) = config.getString(SOURCE_MITOM)

    @Provides
    @BaseScope
    @Source91PhutConfig
    fun providesFootball91PhutConfig(
        @Source91Phut urlFootball90P: String,
        @Regex91Phut regex: String
    ): FootballRepositoryConfig = FootballRepositoryConfig(
        urlFootball90P,
        regex
    )

    @Provides
    @BaseScope
    @SourceMitomConfig
    fun providesFootballMiTomConfig(
        @SourceMitom urlFootball90P: String
    ): FootballRepositoryConfig = FootballRepositoryConfig(
        urlFootball90P
    )

    @Provides
    @IntoMap
    @BaseScope
    @FootballRepositoryMapKey(FootballRepoSourceFrom.Phut91)
    fun providePhut91Repo(repoImpl: Football91Repository): IFootballMatchRepository = repoImpl

    @Provides
    @IntoMap
    @BaseScope
    @FootballRepositoryMapKey(FootballRepoSourceFrom.MiTom)
    fun provideMitomRepo(repoImpl: MitomRepository): IFootballMatchRepository = repoImpl
}