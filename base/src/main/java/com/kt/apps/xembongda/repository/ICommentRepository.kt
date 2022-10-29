package com.kt.apps.xembongda.repository

import com.kt.apps.xembongda.model.comments.CommentDTO
import com.kt.apps.xembongda.model.comments.CommentSpace
import io.reactivex.rxjava3.core.Observable

interface ICommentRepository {
    fun loadTotalCommentCount(): Observable<Int>
    fun loadCommentFor(commentSpace: CommentSpace): Observable<List<CommentDTO>>
    fun sendCommentsFor(comment: CommentDTO, space: CommentSpace): Observable<CommentDTO>
    fun replyCommentFor(comment: CommentDTO, target: CommentDTO, space: CommentSpace)
    fun increaseComment(amount: Int)
}