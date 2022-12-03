package com.kt.apps.xembongda.repository.footbalmatch

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.kt.apps.xembongda.di.RepositoryModule
import com.kt.apps.xembongda.exceptions.FootballMatchThrowable
import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.model.FootballMatchWithStreamLink
import com.kt.apps.xembongda.model.FootballTeam
import com.kt.apps.xembongda.model.LinkStreamWithReferer
import com.kt.apps.xembongda.repository.FirebaseLoggingUtils
import com.kt.apps.xembongda.repository.IFootballMatchRepository
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.repository.config.FootballRepositoryConfig
import com.kt.apps.xembongda.storage.IKeyValueStorage
import com.kt.apps.xembongda.utils.jsoupParse
import io.reactivex.rxjava3.core.Observable
import org.json.JSONArray
import org.jsoup.Jsoup
import java.util.regex.Pattern
import javax.inject.Inject

class MitomRepository @Inject constructor(
    @RepositoryModule.SourceMitomConfig
    private val config: FootballRepositoryConfig,
    private val keyValueStorage: IKeyValueStorage

) : IFootballMatchRepository {
    companion object {
        private const val EXTRA_COOKIE_NAME = "extra:cookie_mitom"
    }

    private val cookie: MutableMap<String, String> by lazy {
        keyValueStorage.get(
            EXTRA_COOKIE_NAME,
            String::class.java,
            String::class.java
        ).toMutableMap()
    }

    private val url: String?
        get() = Firebase.remoteConfig
            .getString(RepositoryModule.SOURCE_MITOM)


    override fun parseMatchesFromHtml(html: String): Observable<List<FootballMatch>> {
        return Observable.create<List<FootballMatch>?> { emiter ->
            val bodyHtml = Jsoup.parse(html)
            val matchItems = bodyHtml.getElementsByClass("list-channel col-xs-12 col-sm-6")
            val listMatch = mutableListOf<FootballMatch>()
            for (item in matchItems) {
                try {
                    val a = item.getElementsByTag("a")[0]
                    if (a.className().contains("item")) {
                        val teams = item.getElementsByClass("team col-xs-6")
                        val homeLogo = teams[0].getElementsByTag("img")[0].attr("src")
                        val awayLogo = teams[1].getElementsByTag("img")[0].attr("src")
                        val title = item.getElementsByClass("title")[0].text().trim()
                        val league = item.getElementsByClass("league")[0].html().trim()
                        val timeDiv = item.getElementsByClass("time")[0]

                        val statusStream = timeDiv.getElementsByTag("span")[0].text()
                        val timeStream = timeDiv.ownText()

                        val href = a.attr("href")
                        val teamNames: List<String> = try {
                            val arr = title.trim().split("-")
                            if (arr.size < 2) throw Exception()
                            arr
                        } catch (e: Exception) {
                            Log.e("TAG", e.message, e)
                            listOf(title, title)
                        }
                        val match = FootballMatch(
                            matchId = href,
                            kickOffTime = timeStream,
                            homeTeam = FootballTeam(
                                name = teamNames[0],
                                logo = homeLogo,
                                id = href,
                                league = league
                            ),
                            awayTeam = FootballTeam(
                                name = teamNames[1],
                                logo = awayLogo,
                                id = href,
                                league = league
                            ),
                            detailPage = href,
                            league = league,
                            statusStream = statusStream,
                            sourceFrom = FootballRepoSourceFrom.MiTom
                        )
                        listMatch.add(match)
                    }
                } catch (e: Exception) {
                    Log.e("TAG", e.message, e)
                }
            }
            if (listMatch.isEmpty()) {
//                emiter.onError(FootballMatchThrowable("Gặp sự cố với link"))
            } else {
                emiter.onNext(listMatch)
            }
            emiter.onComplete()
        }.doOnNext {
            FirebaseLoggingUtils.logGetAllMatches(FootballRepoSourceFrom.MiTom)
        }.doOnError {
            FirebaseLoggingUtils.logGetAllMatchesFail(FootballRepoSourceFrom.MiTom, it)
        }
    }
    override fun getAllMatches(): Observable<List<FootballMatch>> {
        return Observable.create<List<FootballMatch>?> { emiter ->
            val mainPage = jsoupParse(config.url, cookie)
            cookie.putAll(mainPage.cookie)
            val bodyHtml = mainPage.body
            val matchItems = bodyHtml.getElementsByClass("list-channel col-xs-12 col-sm-6")
            val listMatch = mutableListOf<FootballMatch>()
            for (item in matchItems) {
                try {
                    val a = item.getElementsByTag("a")[0]
                    if (a.className().contains("item")) {
                        val teams = item.getElementsByClass("team col-xs-6")
                        val homeLogo = teams[0].getElementsByTag("img")[0].attr("src")
                        val awayLogo = teams[1].getElementsByTag("img")[0].attr("src")
                        val title = item.getElementsByClass("title")[0].text().trim()
                        val league = item.getElementsByClass("league")[0].html().trim()
                        val timeDiv = item.getElementsByClass("time")[0]

                        val statusStream = timeDiv.getElementsByTag("span")[0].text()
                        val timeStream = timeDiv.ownText()

                        val href = a.attr("href")
                        val teamNames: List<String> = try {
                            val arr = title.trim().split("-")
                            if (arr.size < 2) throw Exception()
                            arr
                        } catch (e: Exception) {
                            Log.e("TAG", e.message, e)
                            listOf(title, title)
                        }
                        val match = FootballMatch(
                            matchId = href,
                            kickOffTime = timeStream,
                            homeTeam = FootballTeam(
                                name = teamNames[0],
                                logo = homeLogo,
                                id = href,
                                league = league
                            ),
                            awayTeam = FootballTeam(
                                name = teamNames[1],
                                logo = awayLogo,
                                id = href,
                                league = league
                            ),
                            detailPage = href,
                            league = league,
                            statusStream = statusStream,
                            sourceFrom = FootballRepoSourceFrom.MiTom
                        )
                        listMatch.add(match)
                    }
                } catch (e: Exception) {
                    Log.e("TAG", e.message, e)
                }
            }
            if (listMatch.isEmpty()) {
                emiter.onError(FootballMatchThrowable("Gặp sự cố với link"))
            } else {
                emiter.onNext(listMatch)
            }
            emiter.onComplete()
        }
            .doOnNext {
                FirebaseLoggingUtils.logGetAllMatches(FootballRepoSourceFrom.MiTom)
            }
            .doOnError {
                FirebaseLoggingUtils.logGetAllMatchesFail(FootballRepoSourceFrom.MiTom, it)
            }
    }

    override fun getLinkLiveStream(match: FootballMatch): Observable<FootballMatchWithStreamLink> {
        return Observable.create { emiter ->
            val detailUrl = if (match.detailPage.startsWith("http")) {
                match.detailPage
            } else {
                "${url ?: config.url}${match.detailPage.removePrefix("/")}"
            }
            Log.e("TAG", "Detail page: $detailUrl")
            val list = mutableListOf<String>()
            val jsoupResponse = jsoupParse(detailUrl, cookie)
            cookie.putAll(jsoupResponse.cookie)
            val body = jsoupResponse.body
            Log.e("TAG", "Detail page: ${body.html()}")

            val scripts = body.getElementsByTag("script")
            for (script in scripts) {
                val html = script.html().trim()
                if (html.contains("urls")) {
                    findLinkM3u8WithUrlInBodyElement(html, list)
                } else if (html.contains("video_url")) {
                    findM3u8WithVideoUrlInElement(html, list)
                }
            }
            emiter.onNext(
                FootballMatchWithStreamLink(
                    match,
                    list.map { LinkStreamWithReferer(it, detailUrl) })
            )
            emiter.onComplete()
        }.doOnNext {
            FirebaseLoggingUtils.logGetMatchesDetail(match, FootballRepoSourceFrom.MiTom)
        }.doOnError {
            FirebaseLoggingUtils.logGetMatchesDetailFail(match, FootballRepoSourceFrom.MiTom, it)
        }
    }

    private fun findM3u8WithVideoUrlInElement(
        html: String,
        list: MutableList<String>,
    ): MutableList<String> {
        val pattern = Pattern.compile("(?<=video_url\\s=\\s\").*?(?=\")")
        val matcher = pattern.matcher(html)
        while (matcher.find()) {
            matcher.group(0)?.let { url ->
                if (!list.contains(url)) {
                    list.add(url)
                }
            }
        }
        return list
    }

    private fun findLinkM3u8WithUrlInBodyElement(
        html: String,
        list: MutableList<String>,
    ): MutableList<String> {
        val pattern = Pattern.compile("(?<=urls\\s=\\s\").*?(?=\")")
        val matcher = pattern.matcher(html)
        while (matcher.find()) {
            matcher.group(0)?.let {
                val jsonArray = JSONArray(it)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val url = jsonObject.optString("value")
                    if (url.isNotEmpty()) {
                        if (!list.contains(url)) {
                            list.add(url)
                        }
                    }
                }

            }
        }
        return list
    }

    override fun getLinkLiveStream(
        match: FootballMatch,
        html: String
    ): Observable<FootballMatchWithStreamLink> {
        return Observable.create { emiter ->
            val detailUrl = if (match.detailPage.startsWith("http")) {
                match.detailPage
            } else {
                "${config.url}${match.detailPage.removePrefix("/")}"
            }
//            Log.e("TAG", "Detail page: $detailUrl")
            val list = mutableListOf<String>()
//            val jsoupResponse = jsoupParse(detailUrl, cookie)
//            cookie.putAll(jsoupResponse.cookie)
            val body = Jsoup.parse(html)
            Log.e("TAG", "Detail page: ${body.html()}")

            val scripts = body.getElementsByTag("script")
            for (script in scripts) {
                val html = script.html().trim()
                if (html.contains("urls")) {
                    findLinkM3u8WithUrlInBodyElement(html, list)
                } else if (html.contains("video_url")) {
                    findM3u8WithVideoUrlInElement(html, list)
                }
            }
            emiter.onNext(
                FootballMatchWithStreamLink(
                    match,
                    list.map { LinkStreamWithReferer(it, detailUrl) })
            )
            emiter.onComplete()
        }
    }
}