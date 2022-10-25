package com.kt.apps.xembongda.model.authenticate

data class AccessTokenDTO(
    val uid: String,
    val isNeedReLogin: Boolean
) {
}