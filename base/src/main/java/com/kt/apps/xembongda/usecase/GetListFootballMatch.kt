package com.kt.apps.xembongda.usecase

import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.repository.IFootballMatchRepository
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetListFootballMatch @Inject constructor(
    private val sourceIterator: Map<FootballRepoSourceFrom, @JvmSuppressWildcards IFootballMatchRepository>
) : BaseUseCase<List<FootballMatch>>() {
    override fun prepareExecute(params: Map<String, Any>): Observable<List<FootballMatch>> {
        val sourceFrom = params["sourceFrom"] as FootballRepoSourceFrom
        val repo = sourceIterator[sourceFrom]
        params["htmlPage"]?.let {
            val html = it as String
            return repo!!.parseMatchesFromHtml(html)
        }
        return repo!!.getAllMatches()
    }

    operator fun invoke(sourceFrom: FootballRepoSourceFrom) = execute(
        mapOf(
            "sourceFrom" to sourceFrom
        )
    )

    operator fun invoke(sourceFrom: FootballRepoSourceFrom, html: String) = execute(
        mapOf(
            "sourceFrom" to sourceFrom,
            "htmlPage" to html
        )
    )
}