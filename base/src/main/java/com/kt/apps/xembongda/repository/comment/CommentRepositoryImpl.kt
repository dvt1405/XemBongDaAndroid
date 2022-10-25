package com.kt.apps.xembongda.repository.comment

import com.kt.apps.xembongda.model.comments.CommentDTO
import com.kt.apps.xembongda.model.comments.CommentSpace
import com.kt.apps.xembongda.repository.ICommentRepository
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(): ICommentRepository {
    override fun loadCommentFor(commentSpace: CommentSpace) {
        TODO("Not yet implemented")
    }

    override fun sendCommentsFor(comment: CommentDTO, space: CommentSpace) {
        TODO("Not yet implemented")
    }

    override fun replyCommentFor(comment: CommentDTO, target: CommentDTO, space: CommentSpace) {
        TODO("Not yet implemented")
    }
}