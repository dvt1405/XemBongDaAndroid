package com.kt.apps.xembongda.repository

import com.kt.apps.xembongda.model.League
import com.kt.apps.xembongda.model.LiveScoreDTO
import io.reactivex.rxjava3.core.Observable

interface ILiveScoresRepository {
    fun getLiveScore(league: League) : Observable<List<LiveScoreDTO>>
    fun getLiveScore():  Observable<List<LiveScoreDTO>>
    fun getLiveScoreForDate(date: Long)
}