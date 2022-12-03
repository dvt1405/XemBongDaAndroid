package com.kt.apps.xembongda.repository.footbalmatch

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.kt.apps.xembongda.api.BinhLuan90PhutApi
import com.kt.apps.xembongda.di.RepositoryModule
import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.model.FootballMatchWithStreamLink
import com.kt.apps.xembongda.model.FootballTeam
import com.kt.apps.xembongda.model.LinkStreamWithReferer
import com.kt.apps.xembongda.model.football.BinhLuanFootballMatchModelItem
import com.kt.apps.xembongda.repository.FirebaseLoggingUtils
import com.kt.apps.xembongda.repository.IFootballMatchRepository
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.repository.config.FootballRepositoryConfig
import com.kt.apps.xembongda.utils.trustEveryone
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class BinhLuan90PhutRepositoryImpl @Inject constructor(
    private val api: BinhLuan90PhutApi,
    @RepositoryModule.SourceBinhLuan90Config
    private val config: FootballRepositoryConfig
) : IFootballMatchRepository {
    override fun parseMatchesFromHtml(html: String): Observable<List<FootballMatch>> {
        TODO("Not yet implemented")
    }

    private val url: String?
        get() = Firebase.remoteConfig
            .getString(RepositoryModule.BinhLuan90Config)


    override fun getAllMatches(): Observable<List<FootballMatch>> {
        trustEveryone()
        return api.getAllMatchFrom90Phut()
            .map { list ->
                val totalItems = mutableListOf<BinhLuanFootballMatchModelItem.Data>()
                list.forEach {
                    totalItems.addAll(it.data)
                }
                totalItems
            }
            .map {
                it.map { data ->
                    FootballMatch(
                        homeTeam = FootballTeam(
                            name = data.homeInfo.name,
                            id = data.homeInfo._id,
                            logo = data.homeInfo.imageHost,
                            league = data.tournamentInfo.name
                        ),
                        awayTeam = FootballTeam(
                            name = data.guestInfo.name,
                            id = data.guestInfo._id,
                            logo = data.guestInfo.imageHost,
                            league = data.tournamentInfo.name
                        ),
                        kickOffTime = data.timeStartPlay,
                        statusStream = data.statusDisplay,
                        detailPage = "${config.url}live/${data.id}",
                        matchId = data.id,
                        sourceFrom = FootballRepoSourceFrom.BinhLuan91,
                        league = data.tournamentInfo.name
                    )
                }
            }
            .doOnNext {
                FirebaseLoggingUtils.logGetAllMatches(FootballRepoSourceFrom.BinhLuan91)
            }
            .doOnError {
                FirebaseLoggingUtils.logGetAllMatchesFail(FootballRepoSourceFrom.BinhLuan91, it)
            }

    }

    override fun getLinkLiveStream(match: FootballMatch): Observable<FootballMatchWithStreamLink> {
        return api.getMatchDetail(match.matchId, match.matchId)
            .map {
                FootballMatchWithStreamLink(
                    match,
                    it.streamsLink.map {
                        LinkStreamWithReferer(
                            it.link,
                            match.detailPage
                        )
                    }

                )
            }
            .doOnNext {
                FirebaseLoggingUtils.logGetMatchesDetail(match, FootballRepoSourceFrom.BinhLuan91)
            }
            .doOnError {
                FirebaseLoggingUtils.logGetMatchesDetailFail(match, FootballRepoSourceFrom.BinhLuan91, it)
            }
    }

    override fun getLinkLiveStream(
        match: FootballMatch,
        html: String
    ): Observable<FootballMatchWithStreamLink> {
        TODO("Not yet implemented")
    }
}