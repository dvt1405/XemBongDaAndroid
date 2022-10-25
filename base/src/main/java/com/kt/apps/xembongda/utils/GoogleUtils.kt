package com.kt.apps.xembongda.utils

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.Gson
import com.kt.apps.xembongda.model.UserDTO
import com.kt.apps.xembongda.model.authenticate.AccessTokenDTO

fun GoogleSignInAccount.toAccessToken(): AccessTokenDTO {
    return AccessTokenDTO(
        this.id ?: this.email ?: "",
        this.isExpired
    )
}

fun GoogleSignInAccount.toUserDTO(): UserDTO {
    Log.e("TAG", "toUserDTO: ${Gson().toJson(this)}")
    return UserDTO(
        name = this.displayName ?: "",
        email = this.email ?: "",
        photoUrl = this.photoUrl,
        emailVerified = true,
        uid = this.id ?: ""
    )
}