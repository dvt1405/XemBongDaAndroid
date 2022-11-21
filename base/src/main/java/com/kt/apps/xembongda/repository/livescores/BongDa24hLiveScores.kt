package com.kt.apps.xembongda.repository.livescores

import android.util.Log
import com.google.gson.Gson
import com.kt.apps.xembongda.model.FootballTeam
import com.kt.apps.xembongda.model.League
import com.kt.apps.xembongda.model.LiveScoreDTO
import com.kt.apps.xembongda.repository.ILiveScoresRepository
import com.kt.apps.xembongda.utils.jsoupParse
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.jsoup.nodes.Element
import javax.inject.Inject

class BongDa24hLiveScores @Inject constructor(

) : ILiveScoresRepository {
    companion object {
        private const val URL = "https://bongda24h.vn/livescore.html"
        private const val path = "/livescore.html"
    }

    private val cookies by lazy {
        mutableMapOf<String, String>()
    }

    override fun getLiveScore(league: League): Observable<List<LiveScoreDTO>> {
        TODO("Not yet implemented")
    }

    override fun getLiveScore(): Observable<List<LiveScoreDTO>> {

        return Observable.create<List<LiveScoreDTO>> { emitter ->
            val jsoup = jsoupParse(URL, cookies)
            cookies.putAll(jsoup.cookie)
            val listItems = mutableListOf<LiveScoreDTO>()
            val tableLiveScore = jsoup.body.getElementById("ltd_kq_byleague")
            tableLiveScore?.getElementsByTag("div")?.forEach {
                if (it.className() == "football-header") {
                    mapHeaderItems(it)?.let { it1 -> listItems.add(it1) }
                } else if (it.className() == "football-match-livescore") {
                    mapFootballItem(it)?.let { it1 -> listItems.add(it1) }
                }
            }
            if (listItems.isEmpty()) {
                emitter.onError(Throwable(""))
            } else {
                Log.e("TAG", Gson().toJson(listItems))
                emitter.onNext(listItems)
            }

        }.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }

    private fun mapFootballItem(element: Element): LiveScoreDTO.Match? {
        return try {
            var time: String?
            var date: String?
            var homeTea: FootballTeam? = null
            var awayTeam: FootballTeam? = null

            element.getElementsByClass("columns-time")[0].apply {
                time = getElementsByClass("time")[0].text()
                date = getElementsByClass("date")[0].text()
            }

            element.getElementsByClass("columns-club").forEach {
                val a = it.getElementsByClass("name-club")[0]
                val logo = a.getElementsByTag("img")[0]
                val team = FootballTeam(
                    name = a.text(),
                    logo = logo.attr("src"),
                    league = "",
                    id = a.text().lowercase()
                )
                if (homeTea == null) homeTea = team
                else if (awayTeam == null) awayTeam = team
            }

            val link = try {
                URL + element.getElementsByClass("columns-detail")[0]
                    .getElementsByTag("a")[0]
                    .attr("href")
            } catch (e: Exception) {
                null
            }

            val score: String? = element.getElementsByClass("columns-number")[0].text()


            LiveScoreDTO.Match(
                time, date, score, homeTea, awayTeam, link
            )
        } catch (e: NullPointerException) {
            null
        } catch (e: IndexOutOfBoundsException) {
            null
        } catch (e: Exception) {
            throw e
        }
    }

    private fun mapHeaderItems(it: Element): LiveScoreDTO.Title? {
        return try {
            val title = it.getElementsByClass("fhead-left")[0].getElementsByTag("a")[0].text()
            LiveScoreDTO.Title(title)
        } catch (e: NullPointerException) {
            null
        } catch (e: IndexOutOfBoundsException) {
            null
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getLiveScoreForDate(date: Long) {
        TODO("Not yet implemented")
    }
}