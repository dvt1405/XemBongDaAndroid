package com.kt.apps.xembongda.usecase.comments

import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.model.comments.CommentDTO
import com.kt.apps.xembongda.model.comments.CommentSpace
import com.kt.apps.xembongda.model.highlights.HighLightDTO
import com.kt.apps.xembongda.repository.ICommentRepository
import com.kt.apps.xembongda.usecase.BaseUseCase
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class AddComment @Inject constructor(
    private val commentRepositoryImpl: ICommentRepository
) : BaseUseCase<CommentDTO>() {

    override fun prepareExecute(params: Map<String, Any>): Observable<CommentDTO> {
        val commentDTO = params["comment"] as CommentDTO
        val space = params["space"] as CommentSpace
        return commentRepositoryImpl.sendCommentsFor(commentDTO, space)
    }

    operator fun invoke(commentDTO: CommentDTO, match: FootballMatch) = execute(
        mapOf(
            "comment" to commentDTO,
            "space" to CommentSpace.Match(match)
        )
    )

    operator fun invoke(comment: CommentDTO, highlightDTO: HighLightDTO) = execute(
        mapOf(
            "comment" to comment,
            "space" to CommentSpace.HighLight()
        )
    )
}