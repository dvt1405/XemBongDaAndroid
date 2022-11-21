package com.kt.apps.xembongda.repository

import com.kt.apps.xembongda.model.League
import com.kt.apps.xembongda.model.LeagueRankingItem
import io.reactivex.rxjava3.core.Observable

interface IRankingRepository {
    fun getListLeagueRanking() : Observable<List<League>>
    fun getRankingForLeague(league: League) : Observable<List<LeagueRankingItem>>
}