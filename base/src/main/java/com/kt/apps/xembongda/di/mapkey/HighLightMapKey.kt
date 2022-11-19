package com.kt.apps.xembongda.di.mapkey

import com.kt.apps.xembongda.di.config.HighLightConfig
import dagger.MapKey

@MapKey
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class HighLightMapKey(
    val value: HighLightConfig.Source
) {
}