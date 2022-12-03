package com.kt.apps.xembongda.repository.footbalmatch

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.kt.apps.xembongda.di.RepositoryModule
import com.kt.apps.xembongda.exceptions.FootballMatchThrowable
import com.kt.apps.xembongda.exceptions.mapToMyException
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
import com.kt.apps.xembongda.utils.trustEveryone
import io.reactivex.rxjava3.core.Observable
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

class Football91Repository @Inject constructor(
    @RepositoryModule.Source91PhutConfig
    private val config: FootballRepositoryConfig,
    private val keyValueStorage: IKeyValueStorage
) : IFootballMatchRepository {
    companion object {
        private const val EXTRA_COOKIE_NAME = "extra:cookie_91phut"
    }

    private val cookie by lazy {
        keyValueStorage.get(
            EXTRA_COOKIE_NAME,
            String::class.java,
            String::class.java
        ).toMutableMap()
    }

    private val url: String?
        get() = Firebase.remoteConfig
            .getString(RepositoryModule.SOURCE_91_PHUT)

    private val itemClassName: String by lazy {
        config.itemClassName ?: "matches__item col-lg-6 col-sm-6"
    }

    override fun parseMatchesFromHtml(html: String): Observable<List<FootballMatch>> {
        TODO("Not yet implemented")
    }

    override fun getAllMatches(): Observable<List<FootballMatch>> {
        trustEveryone()
        return Observable.create<List<FootballMatch>?> { emitter ->
            Firebase.remoteConfig.fetchAndActivate()
            val listFootballMatch = mutableListOf<FootballMatch>()
            val response = try {
                jsoupParse(url ?: config.url, cookie)
            } catch (e: Exception) {
                if (emitter.isDisposed) return@create
                emitter.onError(e.mapToMyException())
                return@create
            }
            cookie.putAll(response!!.cookie)
            val allMatches = response.body.getElementsByClass(itemClassName)

            for (match in allMatches) {
                try {
                    val matchDetail = mapHtmlElementToFootballMatch(match)
                    listFootballMatch.add(matchDetail)
                } catch (_: Exception) {
                }
            }
            if (listFootballMatch.isEmpty()) {
                if (emitter.isDisposed) return@create
                emitter.onError(FootballMatchThrowable("Gặp sự cố khi tải trang"))
            } else {
                if (emitter.isDisposed) return@create
                emitter.onNext(listFootballMatch)
                keyValueStorage.save(EXTRA_COOKIE_NAME, cookie)
            }
            emitter.onComplete()
        }.doOnNext {
            FirebaseLoggingUtils.logGetAllMatches(FootballRepoSourceFrom.Phut91)
        }.doOnError {
            FirebaseLoggingUtils.logGetAllMatchesFail(FootballRepoSourceFrom.Phut91, it)
        }
    }

    private fun mapHtmlElementToFootballMatch(match: Element): FootballMatch {
        val matchId = match.attributes().get("data-fid")
        val kickOffTime = match.attributes().get("data-runtime")
        val kickOffDay = match.attributes().get("data-day")
        val kickOffWeek = match.attributes().get("data-week")
        val a = match.getElementsByTag("a")[0]
        val link = a.attributes().get("href")
        val body = a.getElementsByClass("matches__item--body")[0]
        val header = a.getElementsByClass("matches__item--header")[0]

        val league = header.getElementsByClass("matches__item--league")[0]
            .getElementsByClass("text-ellipsis")[0].text()

        val date = header.getElementsByClass("matches__item--date")[0]
            .getElementsByClass("date")[0].text()

        val homeTeamLogo = body.getElementsByClass("matches__team matches__team--home")[0]
            .getElementsByClass("team__logo")[0]
            .getElementsByTag("img")[0].attributes().get("src")

        val homeTeamName = body.getElementsByClass("matches__team matches__team--home")[0]
            .getElementsByClass("team__name")[0].text()

        val awayTeamLogo = body.getElementsByClass("matches__team matches__team--away")[0]
            .getElementsByClass("team__logo")[0]
            .getElementsByTag("img")[0].attributes().get("src")

        val awayTeamName = body.getElementsByClass("matches__team matches__team--away")[0]
            .getElementsByClass("team__name")[0].text()

        val home = FootballTeam(
            name = homeTeamName.replace("\n", " "),
            logo = homeTeamLogo,
            id = "${matchId}_home",
            league = ""
        )
        val away = FootballTeam(
            name = awayTeamName.replace("\n", " "),
            logo = awayTeamLogo,
            id = "${matchId}_away",
            league = ""
        )
        return FootballMatch(
            home,
            away,
            kickOffTime,
            kickOffWeek,
            link,
            FootballRepoSourceFrom.Phut91,
            league
        )
    }

    override fun getLinkLiveStream(match: FootballMatch): Observable<FootballMatchWithStreamLink> {
        return Observable.create { emitter ->
            val lastMatchDetail: FootballMatchWithStreamLink?
            val listM3u8 = mutableListOf<LinkStreamWithReferer>()
            val response = try {
                jsoupParse(match.detailPage, cookie, Pair("referer", match.detailPage))
            } catch (e: Exception) {
                if (emitter.isDisposed) return@create
                emitter.onError(e.mapToMyException())
                return@create
            }
            cookie.putAll(response.cookie)
            val dom = response.body
            val iframes = dom.getElementById("player")!!.getElementsByTag("iframe")
            for (frame in iframes) {
                val src = frame.attributes().get("src")
                parseM3u8LinkFromFrame(src, url ?: match.detailPage)?.let {
                    listM3u8.addAll(it)
                }

            }

            val otherLinks = dom.getElementById("tv_links")!!.getElementsByTag("a")
            var isFirstItem = true
            if (otherLinks.size >= 2) {
                for (link in otherLinks) {
                    if (isFirstItem) {
                        isFirstItem = false
                        continue
                    }
                    try {
                        val linkDetail = link.attributes().get("href")
                        val detailDom = Jsoup.connect(linkDetail)
                            .execute().parse().body()
                            .getElementById("player")!!
                            .getElementsByTag("iframe")[0]
                            .attributes()
                            .get("src")


                        parseM3u8LinkFromFrame(detailDom, url ?: match.detailPage)?.let {
                            listM3u8.addAll(it)
                        }
                    } catch (_: Exception) {
                    }
                }
            }
            lastMatchDetail = FootballMatchWithStreamLink(match, listM3u8)
            saveFootballMatch(lastMatchDetail.match, Gson().toJson(lastMatchDetail.linkStreams))
            if (emitter.isDisposed) return@create
            emitter.onNext(lastMatchDetail)
            emitter.onComplete()
        }.doOnNext {
            FirebaseLoggingUtils.logGetMatchesDetail(match, FootballRepoSourceFrom.Phut91)
        }.doOnError {
            FirebaseLoggingUtils.logGetMatchesDetailFail(match, FootballRepoSourceFrom.Phut91, it)
        }
    }

    private fun saveFootballMatch(match: FootballMatch, linkStreamStr: String) {

    }

    private fun parseM3u8LinkFromFrame(src: String, referer: String): List<LinkStreamWithReferer>? {
        val listUrl = mutableListOf<String>()
        val response = jsoupParse(src, cookie, Pair("referer", referer))
        cookie.putAll(response.cookie)

        val m3u8Dom = response.body
        val scripts = m3u8Dom.getElementsByTag("script")
        for (i in scripts) {
            val html = i.html().trim()
            if (html.startsWith("var")) {
                val pattern: Pattern = Pattern.compile(config.regex!!)
                val matcher: Matcher = pattern.matcher(html)
                while (matcher.find()) {
                    matcher.group(0)?.let {
                        listUrl.add(it.replace("\u003d", "="))
                    }
                }
            }
        }
        return if (listUrl.isEmpty()) null else
            listUrl.map { LinkStreamWithReferer(it, src) }
    }

    override fun getLinkLiveStream(
        match: FootballMatch,
        html: String
    ): Observable<FootballMatchWithStreamLink> {
        TODO("Not yet implemented")
    }
}