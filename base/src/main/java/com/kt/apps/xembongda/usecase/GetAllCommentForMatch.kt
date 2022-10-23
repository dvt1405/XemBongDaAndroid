package com.kt.apps.xembongda.usecase

import com.kt.apps.xembongda.model.FootballMatch
import io.reactivex.rxjava3.core.Observable
import org.w3c.dom.Comment
import javax.inject.Inject

class GetAllCommentForMatch @Inject constructor() : BaseUseCase<List<Comment>>() {
    override fun prepareExecute(params: Map<String, Any>): Observable<List<Comment>> {
        TODO("Not yet implemented")
    }

    operator fun invoke(match: FootballMatch) {}
}