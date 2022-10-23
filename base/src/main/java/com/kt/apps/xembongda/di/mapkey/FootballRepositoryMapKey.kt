package com.kt.apps.xembongda.di.mapkey

import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import dagger.MapKey

@MapKey
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class FootballRepositoryMapKey(
    val value: FootballRepoSourceFrom
) {
}