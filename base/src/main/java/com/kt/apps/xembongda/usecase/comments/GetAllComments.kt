package com.kt.apps.xembongda.usecase.comments

import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.model.comments.CommentDTO
import com.kt.apps.xembongda.model.comments.CommentSpace
import com.kt.apps.xembongda.repository.ICommentRepository
import com.kt.apps.xembongda.usecase.BaseUseCase
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetAllComments @Inject constructor(
    private val repository: ICommentRepository
) : BaseUseCase<List<CommentDTO>>() {

    override fun prepareExecute(params: Map<String, Any>): Observable<List<CommentDTO>> {
        return repository.loadCommentFor(params["space"] as CommentSpace)
    }

    operator fun invoke(match: FootballMatch) = execute(
        mapOf(
            "space" to CommentSpace.Match(match)
        )
    )

    operator fun invoke() = execute(mapOf("space" to CommentSpace.Public))
}