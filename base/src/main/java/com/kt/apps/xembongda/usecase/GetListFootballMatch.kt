package com.kt.apps.xembongda.usecase

import android.util.Log
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
        return repo!!.getAllMatches()
    }

    operator fun invoke(sourceFrom: FootballRepoSourceFrom) = execute(
        mapOf(
            "sourceFrom" to sourceFrom
        )
    )
}