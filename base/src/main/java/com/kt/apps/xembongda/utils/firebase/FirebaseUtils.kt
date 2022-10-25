package com.kt.apps.xembongda.utils.firebase

import com.google.firebase.auth.FirebaseUser
import com.kt.apps.xembongda.model.UserDTO

fun FirebaseUser?.toUserDto(): UserDTO {
    if (this == null) return UserDTO("", "", null, false, "")
    return UserDTO(
        name = displayName ?: "",
        email = email ?: "",
        photoUrl = photoUrl,
        emailVerified = isEmailVerified,
        uid = uid
    )
}
