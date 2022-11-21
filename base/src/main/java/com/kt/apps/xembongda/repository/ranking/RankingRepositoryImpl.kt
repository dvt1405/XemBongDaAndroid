package com.kt.apps.xembongda.repository.ranking

import android.util.Log
import com.google.firebase.remoteconfig.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kt.apps.xembongda.model.FootballTeam
import com.kt.apps.xembongda.model.League
import com.kt.apps.xembongda.model.LeagueRankingItem
import com.kt.apps.xembongda.repository.IRankingRepository
import com.kt.apps.xembongda.storage.IKeyValueStorage
import com.kt.apps.xembongda.utils.jsoupParse
import io.reactivex.rxjava3.core.Observable
import org.jsoup.nodes.Element
import javax.inject.Inject

class RankingRepositoryImpl @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    private val keyValueStorage: IKeyValueStorage
) : IRankingRepository {
    private val url by lazy {
        remoteConfig.getString(EXTRA_URL).ifBlank {
            URL
        }
    }

    private val mainPath by lazy {
        remoteConfig.getString(EXTRA_PATH_RANKING_BONGDA_24h).ifBlank {
            "bang-xep-hang.html"
        }
    }

    private val cookie: MutableMap<String, String> by lazy {
        keyValueStorage.get(
            EXTRA_COOKIE_NAME,
            String::class.java,
            String::class.java
        ).toMutableMap()
    }

    override fun getListLeagueRanking(): Observable<List<League>> {
        return Observable.create {
            val jsoupRes = jsoupParse("$url$mainPath", cookie)
            cookie.putAll(jsoupRes.cookie)
            keyValueStorage.save(EXTRA_COOKIE_NAME, cookie)
            val body = jsoupRes.body
            val totalRankingList = mutableListOf<League>()
            body.getElementsByClass("sidebar-left")[0]
                .getElementsByClass("sidebar")[0]
                .getElementsByClass("ul-sbar")[0]
                .getElementsByTag("li").forEach {
                    totalRankingList.add(mapLiTagToListLeague(it))
                }
            if (totalRankingList.isNotEmpty()) {
                it.onNext(totalRankingList.sortedBy {
                    it.priority
                })
            } else {
                it.onError(Throwable("Try again later"))
            }
            it.onComplete()
        }
    }

    private fun mapLiTagToListLeague(element: Element): League {
        val a = element.getElementsByTag("a")[0]
        val title = a.text()
        val href = a.attr("href")
        return League(
            title, title, href, if (title.trim()
                    .replace(" ", "")
                    .lowercase()
                    .contains("worldcup")
            ) 0 else 1
        ).apply {
            rankingUrl = if (href.contains("http")) href else "$url${href.trim().removePrefix("/")}"
        }
    }

    override fun getRankingForLeague(league: League): Observable<List<LeagueRankingItem>> {
        return Observable.create {
            val rankUrl: String = league.rankingUrl ?: return@create
            val jsoupResponse = jsoupParse(rankUrl, cookie)
            cookie.putAll(jsoupResponse.cookie)
            keyValueStorage.save(EXTRA_COOKIE_NAME, cookie)
            if (BuildConfig.DEBUG) {
                Log.e(TAG, jsoupResponse.body.html())
            }
            val body = jsoupResponse.body
            if (league.priority == 0) {
                it.onNext(mapWorldCupItem(league, body))
            } else {
                it.onNext(mapNormalItem(body))
            }
            it.onComplete()
        }
    }

    private fun mapNormalItem(element: Element): List<LeagueRankingItem> {
        val listItem = mutableListOf<LeagueRankingItem>()


        return listItem
    }

    private fun mapWorldCupItem(league: League, element: Element): List<LeagueRankingItem> {
        val listItem = mutableListOf<LeagueRankingItem>()
        element.getElementsByClass("row table-scrol-x").forEach {

            val table = it.getElementsByTag("table")[0]

            table.getElementsByTag("tbody")[0]
                .getElementsByTag("tr")
                .forEachIndexed { index, element ->
                    try {
                        Log.e("TAG", "tr")
                        Log.e("TAG", element.html())
                        val td = element.getElementsByTag("td")
                        val rank = try {
                            td[0].text().toInt()
                        } catch (e: Exception) {
                            index
                        }
                        val teamElement = td[1]
                        val teamLogo = teamElement.getElementsByTag("img")[0]
                            .attr("src")
                        val teamName = teamElement.getElementsByClass("text-link-club")[0]
                            .text()
                        val totalPlayed = td[2].text().toInt()
                        val win = td[3].text().toInt()
                        val draw = td[4].text().toInt()
                        val lose = td[5].text().toInt()
                        val totalGoal = td[6].text().toInt()
                        val goalDifferent = td[7].text().toInt()
                        val score = td[8].text().toInt()
                        val last5Matches = td[9].getElementsByTag("img").map {
                            val src = it.attr("src")
                            if (src.contains("http")) {
                                src
                            } else {
                                "$url$src"
                            }
                        }

                        val match = LeagueRankingItem(
                            rank = rank,
                            table = table.getElementsByClass("w-bang-doi uppercase")[0].text(),
                            team = FootballTeam(
                                name = teamName,
                                id = teamLogo,
                                league = league.name,
                                logo = teamLogo
                            ),
                            score = score,
                            totalPlayed = totalPlayed,
                            win = win,
                            lose = lose,
                            draw = draw,
                            goalDifference = goalDifferent,
                            last5Match = last5Matches.toTypedArray(),
                            totalGoal = totalGoal
                        )
                        listItem.add(match)
                    } catch (e: Exception) {
                        Log.e("TAG", e.message, e)
                    }

                }

        }
        return listItem
    }


    companion object {
        private val TAG = RankingRepositoryImpl::class.java.simpleName
        private const val EXTRA_COOKIE_NAME = "extra:cookie_ranking_24h"
        private const val URL = "https://bongda24h.vn/"
        private const val EXTRA_URL = "extra:main_url_bongda_24h"
        private const val EXTRA_PATH_RANKING_BONGDA_24h = "extra:path_ranking_bongda_24h"
    }
}