package com.kt.apps.xembongda.di

import com.kt.apps.xembongda.repository.IFootballMatchRepository
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.storage.IKeyValueStorage
import com.kt.apps.xembongda.storage.KeyValueStorageImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.Multibinds


@Module
abstract class StorageModule {

    @Binds
    abstract fun keyValueStorage(instance: KeyValueStorageImpl): IKeyValueStorage

    @Multibinds
    abstract fun mapRepo(): Map<FootballRepoSourceFrom, IFootballMatchRepository>
}
