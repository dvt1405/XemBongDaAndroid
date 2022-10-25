package com.kt.apps.xembongda.model.authenticate

import androidx.appcompat.app.AppCompatActivity

sealed class AuthenticateMethod {
    data class Email(val email: String, val password: String) : AuthenticateMethod()
    data class Phone(val phone: String) : AuthenticateMethod()
    data class Google(val activity: AppCompatActivity) : AuthenticateMethod()
    object Facebook: AuthenticateMethod()
}