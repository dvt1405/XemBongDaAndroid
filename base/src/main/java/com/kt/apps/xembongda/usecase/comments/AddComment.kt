package com.kt.apps.xembongda.usecase.comments

import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.model.comments.CommentDTO
import com.kt.apps.xembongda.model.comments.CommentSpace
import com.kt.apps.xembongda.repository.ICommentRepository
import com.kt.apps.xembongda.usecase.BaseUseCase
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class AddComment @Inject constructor(
    private val commentRepositoryImpl: ICommentRepository
) : BaseUseCase<CommentDTO>() {

    override fun prepareExecute(params: Map<String, Any>): Observable<CommentDTO> {
        val commentDTO = params["comment"] as CommentDTO
        val match = params["match"] as FootballMatch
        return commentRepositoryImpl.sendCommentsFor(commentDTO, CommentSpace.Match(match))
    }

    operator fun invoke(commentDTO: CommentDTO, match: FootballMatch) = execute(
        mapOf(
            "comment" to commentDTO,
            "match" to match
        )
    )
}