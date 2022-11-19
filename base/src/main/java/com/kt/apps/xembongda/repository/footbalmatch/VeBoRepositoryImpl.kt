package com.kt.apps.xembongda.repository.footbalmatch

import android.util.Log
import com.kt.apps.xembongda.api.VeBoApi
import com.kt.apps.xembongda.di.RepositoryModule
import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.model.FootballMatchWithStreamLink
import com.kt.apps.xembongda.model.FootballTeam
import com.kt.apps.xembongda.model.LinkStreamWithReferer
import com.kt.apps.xembongda.repository.IFootballMatchRepository
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.repository.config.FootballRepositoryConfig
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject
import javax.inject.Named

class VeBoRepositoryImpl @Inject constructor(
    @RepositoryModule.SourceVeBoConfig
    private val config: FootballRepositoryConfig,
    private val api: VeBoApi,
    @Named("DetailVeBo")
    private val detail: VeBoApi,
) : IFootballMatchRepository {
    override fun parseMatchesFromHtml(html: String): Observable<List<FootballMatch>> {
        TODO("Not yet implemented")
    }

    override fun getAllMatches(): Observable<List<FootballMatch>> {
        return Observable.merge(api.getAllMatchFrom90Phut(), detail.getAllOtherFrom90Phut())
            .doOnError {
                throw it
            }
            .map {
                it.data.map {  data ->
                    FootballMatch(
                        homeTeam = FootballTeam(
                            name = data.home.name,
                            id = data.home.id,
                            logo = data.home.logo,
                            league = data.tournament.name
                        ),
                        awayTeam = FootballTeam(
                            name = data.away.name,
                            id = data.away.id,
                            logo = data.away.logo,
                            league = data.tournament.name
                        ),
                        kickOffTime = "${data.timestamp/1000}",
                        statusStream = data.match_status,
                        detailPage = "${config.referer}truc-tiep/${data.id}",
                        matchId = data.id,
                        sourceFrom = FootballRepoSourceFrom.VeBo,
                        league = data.tournament.name
                    )
                }
            }
    }

    override fun getLinkLiveStream(match: FootballMatch): Observable<FootballMatchWithStreamLink> {
        Log.e("TAG", match.detailPage)
        return detail.getMatchDetail(match.matchId)
            .map {
                FootballMatchWithStreamLink(
                    match,
                    it.data.play_urls.map {
                        LinkStreamWithReferer(
                            it.url,
                             config.referer ?: "https://vebotv.cc/"
                        )
                    }

                )
            }
    }

    private fun downloadM3u8() {

    }

    override fun getLinkLiveStream(
        match: FootballMatch,
        html: String
    ): Observable<FootballMatchWithStreamLink> {
        TODO("Not yet implemented")
    }
}