package com.kt.apps.xembongda.repository

import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.model.FootballMatchWithStreamLink
import io.reactivex.rxjava3.core.Observable

interface IFootballMatchRepository {
    fun getAllMatches(): Observable<List<FootballMatch>>
    fun getLinkLiveStream(match: FootballMatch): Observable<FootballMatchWithStreamLink>
}