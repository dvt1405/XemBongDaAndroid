package com.kt.apps.xembongda.ui.comment

import com.google.firebase.database.IgnoreExtraProperties
import com.kt.apps.xembongda.model.comments.CommentDTO
import com.kt.apps.xembongda.utils.getTimeAgo

interface IBaseItemComment {
    val titleName: String
    val avatarUrl: String
    val commentDetail: String
    val uID: String
    val systemTime: Long
    fun getTimeAgoString() = systemTime.getTimeAgo()
}

@IgnoreExtraProperties
class BaseCommentFootballMatch(
    override val titleName: String = "",
    override val avatarUrl: String = "",
    override val commentDetail: String = "",
    override val uID: String = "",
    override val systemTime: Long = System.currentTimeMillis()
) : IBaseItemComment {

    override fun equals(other: Any?): Boolean {
        if (other is IBaseItemComment) {
            return other.uID.equals(uID, ignoreCase = false) && other.systemTime == systemTime
        }
        return false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    companion object {
        fun fromDto(commentDTO: CommentDTO) = BaseCommentFootballMatch(
            titleName = commentDTO.titleName,
            avatarUrl = commentDTO.avatarUrl ?: "",
            commentDetail = commentDTO.commentDetail,
            uID = commentDTO.uID,
            systemTime = commentDTO.systemTime
        )
    }
}
