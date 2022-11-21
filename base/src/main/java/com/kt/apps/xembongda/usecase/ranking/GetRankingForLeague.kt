package com.kt.apps.xembongda.usecase.ranking

import com.kt.apps.xembongda.model.League
import com.kt.apps.xembongda.model.LeagueRankingItem
import com.kt.apps.xembongda.repository.IRankingRepository
import com.kt.apps.xembongda.usecase.BaseUseCase
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetRankingForLeague @Inject constructor(
    private val repository: IRankingRepository
) : BaseUseCase<List<LeagueRankingItem>>() {
    override fun prepareExecute(params: Map<String, Any>): Observable<List<LeagueRankingItem>> {
        val league = params[EXTRA_LEAGUE] as League
        return repository.getRankingForLeague(league)
    }

    operator fun invoke(league: League) = execute(
        mapOf(
            EXTRA_LEAGUE to league
        )
    )


    companion object {
        private const val EXTRA_LEAGUE = "extra:league"
    }

}