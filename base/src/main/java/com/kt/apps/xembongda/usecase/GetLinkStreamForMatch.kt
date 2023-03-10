package com.kt.apps.xembongda.usecase

import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.model.FootballMatchWithStreamLink
import com.kt.apps.xembongda.repository.IFootballMatchRepository
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetLinkStreamForMatch @Inject constructor(
    private val mapRepo: Map<FootballRepoSourceFrom, @JvmSuppressWildcards IFootballMatchRepository>
) : BaseUseCase<FootballMatchWithStreamLink>() {
    override fun prepareExecute(params: Map<String, Any>): Observable<FootballMatchWithStreamLink> {
        val repo = mapRepo[params["sourceFrom"] as FootballRepoSourceFrom]!!
        val match = params["match"] as FootballMatch
        params["html"]?.let {
            return@let repo.getLinkLiveStream(match, it as String)
        }
        return repo.getLinkLiveStream(match)
    }

    operator fun invoke(match: FootballMatch, sourceFrom: FootballRepoSourceFrom) = execute(
        mapOf(
            "sourceFrom" to sourceFrom,
            "match" to match
        )
    )

    operator fun invoke(match: FootballMatch, sourceFrom: FootballRepoSourceFrom, html: String) = execute(
        mapOf(
            "sourceFrom" to sourceFrom,
            "match" to match,
            "html" to html
        )
    )

    operator fun invoke(match: FootballMatch) = invoke(match, match.sourceFrom)
    operator fun invoke(match: FootballMatch, html: String) = invoke(match, match.sourceFrom, html)
}