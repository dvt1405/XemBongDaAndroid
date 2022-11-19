package com.kt.apps.xembongda.di

import com.kt.apps.xembongda.di.config.HighLightConfig
import com.kt.apps.xembongda.di.mapkey.HighLightMapKey
import com.kt.apps.xembongda.repository.IHighLightRepository
import com.kt.apps.xembongda.repository.highlight.HoofootRepository
import com.kt.apps.xembongda.repository.highlight.XoiLacHighLightRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class HighLightModule {

    @Provides
    @BaseScope
    @IntoMap
    @HighLightMapKey(HighLightConfig.Source.Mitom10)
    fun providesHighLightRepository(repository: XoiLacHighLightRepositoryImpl): IHighLightRepository =
        repository

    @Provides
    @BaseScope
    @IntoMap
    @HighLightMapKey(HighLightConfig.Source.HooFoot)
    fun providesHooFootHighLightRepository(repository: HoofootRepository): IHighLightRepository =
        repository


}