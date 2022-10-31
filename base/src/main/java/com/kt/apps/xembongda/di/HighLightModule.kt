package com.kt.apps.xembongda.di

import com.kt.apps.xembongda.repository.IHighLightRepository
import com.kt.apps.xembongda.repository.highlight.XoiLacHighLightRepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class HighLightModule {

    @Provides
    @BaseScope
    fun providesHighLightRepository(repository: XoiLacHighLightRepositoryImpl): IHighLightRepository =
        repository
}