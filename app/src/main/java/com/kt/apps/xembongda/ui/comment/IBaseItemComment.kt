package com.kt.apps.xembongda.ui.comment

import com.google.firebase.database.IgnoreExtraProperties
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

}
