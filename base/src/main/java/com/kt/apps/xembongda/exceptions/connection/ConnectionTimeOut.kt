package com.kt.apps.xembongda.exceptions.connection

import com.kt.apps.xembongda.exceptions.ErrorCode
import com.kt.apps.xembongda.exceptions.MyException

class ConnectionTimeOut(override val message: String, override val cause: Throwable? = null) :
    MyException(ErrorCode.CONNECT_TIMEOUT, message) {
}