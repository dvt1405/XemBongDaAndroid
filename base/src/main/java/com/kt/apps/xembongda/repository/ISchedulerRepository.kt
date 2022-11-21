package com.kt.apps.xembongda.repository

import com.kt.apps.xembongda.model.League
import com.kt.apps.xembongda.model.LiveScoreDTO
import io.reactivex.rxjava3.core.Observable

interface ISchedulerRepository {
    fun getListLeagueAvailableScheduler() : Observable<List<League>>
    fun getSchedulerForLeague() : Observable<LiveScoreDTO.Match>
}