package com.kt.apps.xembongda.repository

import com.kt.apps.xembongda.model.comments.CommentDTO
import com.kt.apps.xembongda.model.comments.CommentSpace

interface ICommentRepository {
    fun loadCommentFor(commentSpace: CommentSpace)
    fun sendCommentsFor(comment: CommentDTO, space: CommentSpace)
    fun replyCommentFor(comment: CommentDTO, target: CommentDTO, space: CommentSpace)
}