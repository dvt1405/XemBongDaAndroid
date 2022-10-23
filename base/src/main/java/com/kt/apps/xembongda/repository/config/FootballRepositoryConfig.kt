package com.kt.apps.xembongda.repository.config

import javax.inject.Named

class FootballRepositoryConfig(
    val url: String,
    val regex: String? = null,
    val itemClassName: String? = null
) {
}