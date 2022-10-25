package com.kt.apps.xembongda.model.comments

import com.kt.apps.xembongda.model.FootballMatch

sealed class CommentSpace {

    data class Post(
        val id: String,
        val type: String,
        val postId: String,
        val spaceOwnerId: String,
        val relatedTo: String,
        val createdTime: Long,
        val updateTime: Long
    ) : CommentSpace()

    object Public : CommentSpace()

    object Group : CommentSpace()

    data class Match(val footballMatch: FootballMatch) : CommentSpace()

}

