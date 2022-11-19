package com.kt.apps.xembongda.repository.config

import javax.inject.Named

class FootballRepositoryConfig(
    var url: String,
    val regex: String? = null,
    val itemClassName: String? = null,
    val referer: String? = null
) {
}