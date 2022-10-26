package com.kt.apps.xembongda.model.comments

import com.kt.apps.xembongda.model.UserDTO

class CommentDTO(
    val uID: String,
    val titleName: String,
    val avatarUrl: String?,
    val commentDetail: String,
    val systemTime: Long
) {

    fun gzipToStr(): String {
        return "$uID|;|$titleName|;|${avatarUrl ?: ""}|;|$commentDetail|;|$systemTime"
    }


    companion object {
        fun wrap(text: String, userDTO: UserDTO) = CommentDTO(
            uID = userDTO.uid,
            titleName = userDTO.name,
            avatarUrl = userDTO.photoUrl.toString(),
            commentDetail = text,
            systemTime = System.currentTimeMillis()
        )

        fun fromFireStoreStr(str: String) : CommentDTO? {
            val result = str.split("|;|")
            return try {
                CommentDTO(result[0], result[1], result[2], result[3], result[4].toLong())
            } catch (e: Exception) {
                null
            }
        }

    }
}