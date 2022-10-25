package com.kt.apps.xembongda.exceptions

import androidx.annotation.IntDef


class UnAuthenException(
    @DefaultValue
    val code: Int,
    override val message: String? = UnAuthenException::class.java.simpleName
) : Throwable(message) {
    @IntDef(value = [ErrorCode.UN_AUTHENTICATE, ErrorCode.REQUIRED_LOGIN], flag = true)
    annotation class DefaultValue

}