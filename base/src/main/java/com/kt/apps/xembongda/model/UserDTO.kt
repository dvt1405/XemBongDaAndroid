package com.kt.apps.xembongda.model

import android.net.Uri

class UserDTO(
    val name: String,
    val email: String,
    val photoUrl: Uri?,
    val emailVerified: Boolean,
    val uid: String
) {}