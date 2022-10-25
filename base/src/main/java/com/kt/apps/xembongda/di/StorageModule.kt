package com.kt.apps.xembongda.di

import com.kt.apps.xembongda.repository.IAuthenticateRepository
import com.kt.apps.xembongda.repository.IFootballMatchRepository
import com.kt.apps.xembongda.repository.auth.AuthenticateRepositoryImpl
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.storage.IKeyValueStorage
import com.kt.apps.xembongda.storage.KeyValueStorageImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.Multibinds


@Module
abstract class StorageModule {

    @Binds
    abstract fun keyValueStorage(instance: KeyValueStorageImpl): IKeyValueStorage

    @Multibinds
    abstract fun iFootballMatchRepositoryMap(): Map<FootballRepoSourceFrom, IFootballMatchRepository>

    @Binds
    abstract fun provideAuthenticateRepository(repoImpl: AuthenticateRepositoryImpl): IAuthenticateRepository
}
