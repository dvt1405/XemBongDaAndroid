package com.kt.apps.xembongda.di

import com.kt.apps.xembongda.di.config.HighLightConfig
import com.kt.apps.xembongda.repository.*
import com.kt.apps.xembongda.repository.auth.AuthenticateRepositoryImpl
import com.kt.apps.xembongda.repository.comment.CommentRepositoryImpl
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.repository.ranking.RankingRepositoryImpl
import com.kt.apps.xembongda.repository.tv.VDataSourceImpl
import com.kt.apps.xembongda.storage.IKeyValueStorage
import com.kt.apps.xembongda.storage.KeyValueStorageImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.Multibinds


@Module
abstract class BindsAbstractModule {

    @Binds
    abstract fun keyValueStorage(instance: KeyValueStorageImpl): IKeyValueStorage

    @Multibinds
    abstract fun iFootballMatchRepositoryMap(): Map<FootballRepoSourceFrom, @JvmSuppressWildcards IFootballMatchRepository>

    @Multibinds
    abstract fun iFootballMatchHighLightMap(): Map<HighLightConfig.Source, @JvmSuppressWildcards IHighLightRepository>

    @Binds
    abstract fun provideAuthenticateRepository(repoImpl: AuthenticateRepositoryImpl): IAuthenticateRepository

    @Binds
    abstract fun provideCommentRepository(repoImpl: CommentRepositoryImpl): ICommentRepository

    @Binds
    abstract fun provideRankingRepository(repoImpl: RankingRepositoryImpl): IRankingRepository

    @Binds
    abstract fun provideTV(tvRepoImpl: VDataSourceImpl): ITVDataSource
}
