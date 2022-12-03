package com.kt.apps.xembongda.repository.footbalmatch

import android.util.JsonToken
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kt.apps.xembongda.Constants
import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.model.FootballMatchWithStreamLink
import com.kt.apps.xembongda.model.FootballTeam
import com.kt.apps.xembongda.model.LinkStreamWithReferer
import com.kt.apps.xembongda.repository.FirebaseLoggingUtils
import com.kt.apps.xembongda.repository.IFootballMatchRepository
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.storage.IKeyValueStorage
import io.reactivex.rxjava3.core.Observable
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import java.util.regex.Pattern
import javax.inject.Inject

class VTVBackupRepository @Inject constructor(
    private val keyValueStorage: IKeyValueStorage,
    private val client: OkHttpClient
) : IFootballMatchRepository {
    companion object {
        private const val EXTRA_COOKIE_NAME = "extra:cookie_vtv_go"
    }

    private val cookie by lazy {
        keyValueStorage.get(
            EXTRA_COOKIE_NAME,
            String::class.java,
            String::class.java
        ).toMutableMap()
    }

    private val url: String
        get() {
            val a = Firebase.remoteConfig
                .getString("vtv")
            return if (a.trim().isNotEmpty()) a
            else "https://vtvgo.vn/trang-chu.html"
        }


    override fun parseMatchesFromHtml(html: String): Observable<List<FootballMatch>> {
        TODO("Not yet implemented")
    }

    override fun getAllMatches(): Observable<List<FootballMatch>> {
        return Observable.create<List<FootballMatch>?> { emitter ->
            val document = Jsoup.connect(url)
                .cookies(cookie)
                .execute()
            cookie.putAll(document.cookies())
            Log.e("TAG", "Cookie: ${buildCookie()}")
            val body = document.parse().body()
            val listChannelDetail = mutableListOf<FootballMatch>()
            body.getElementsByClass("list_channel")
                .forEach {
                    val detail = it.getElementsByTag("a").first()
                    val link = detail!!.attr("href")
                    val name = detail.attr("alt")
                    val logo = detail.getElementsByTag("img").first()!!.attr("src")
                    val regex = "[*?<=vtv\\d]*?(\\d+)"
                    val pattern = Pattern.compile(regex)
                    val matcher = pattern.matcher(link)
                    val listMatcher = mutableListOf<String>()
                    while (matcher.find()) {
                        matcher.group(0)?.let { it1 -> listMatcher.add(it1) }
                    }
                    var channelId: String? = null
                    if (listMatcher.isNotEmpty()) {
                        channelId = try {
                            listMatcher[1]
                        } catch (e: Exception) {
                            name.lowercase().replace("[^\\dA-Za-z ]", "")
                                .replace("\\s+", "+")
                                .lowercase()
                                .removeSuffix("hd")
                                .trim()
                        }
                    }
                    val match = FootballMatch(
                        homeTeam = FootballTeam(
                            name = name,
                            id = channelId ?: name,
                            logo = logo,
                            league = "WorldCup"
                        ),
                        awayTeam = FootballTeam(
                            name = name,
                            id = channelId ?: name,
                            logo = logo,
                            league = "WorldCup"
                        ),
                        kickOffTime = "Trực tiếp",
                        statusStream = "Trực tiếp",
                        detailPage = link,
                        matchId = channelId ?: name,
                        sourceFrom = FootballRepoSourceFrom.VTV,
                        league = "WorldCup"
                    )
                    listChannelDetail.add(match)
                }
            emitter.onNext(listChannelDetail)
            emitter.onComplete()
        }.doOnNext {
            FirebaseLoggingUtils.logGetAllMatches(FootballRepoSourceFrom.VTV)
        }.doOnError {
            FirebaseLoggingUtils.logGetAllMatchesFail(FootballRepoSourceFrom.VTV, it)
        }
    }

    override fun getLinkLiveStream(match: FootballMatch): Observable<FootballMatchWithStreamLink> {
        return getLinkLiveStream(match, "")
    }

    override fun getLinkLiveStream(match: FootballMatch, html: String): Observable<FootballMatchWithStreamLink> {
        return Observable.create { emitter ->
            Log.e("TAG", "Detail page: ${match.detailPage}")
            val document = Jsoup.connect(match.detailPage)
                .cookies(cookie)
                .header("referer", match.detailPage)
                .header("origin", match.detailPage.toOrigin())
                .execute()
            cookie.putAll(document.cookies())
            keyValueStorage.save(EXTRA_COOKIE_NAME, cookie)
            val body = document.parse().body()
            val script = body.getElementsByTag("script")
            for (it in script) {
                if (it.html().trim().contains("token")) {
                    Log.e("TAG", "Token: ${it.html()}")
                    val token: String? = getVarFromHtml("token", it.html())
                    val id = getVarNumberFromHtml("id", it.html())
                    val typeId: String? = getVarFromHtml("type_id", it.html())
                    val appId: String? = getVarFromHtml("app_id", it.html())
                    val hlsToken: String? = getVarFromHtml("token_hls", it.html())
                    val time: String? = getVarFromHtml("time", it.html())
                    if (anyNotNull(token, id, typeId, time)) {
                        getStream(
                            match,
                            token!!,
                            id!!,
                            typeId!!,
                            time!!,
                            appId!!,
                            hlsToken!!,
                            {
                                emitter.onNext(FootballMatchWithStreamLink(match, it))
                            }, {
                                Log.e("TAG", it.message, it)
                            }
                        )
                        break
                    }
                }
            }
        }
            .doOnNext {
                FirebaseLoggingUtils.logGetMatchesDetail(match, FootballRepoSourceFrom.VTV)
            }
            .doOnError {
                FirebaseLoggingUtils.logGetMatchesDetailFail(match, FootballRepoSourceFrom.VTV, it)

            }
    }

    private fun getStream(
        match: FootballMatch,
        token: String,
        id: String,
        typeId: String,
        time: String,
        appId: String,
        hlsToken: String,
        onSuccess: (data: List<LinkStreamWithReferer>) -> Unit,
        onError: (t: Throwable) -> Unit
    ) {
        val bodyRequest = FormBody.Builder()
            .add("type_id", typeId)
            .add("id", id)
            .add("time", time)
            .add("token", token)
            .build()
        val url = "${baseUrl}${getLinkStreamPath}"
        Log.e("TAG", "Cookie: ${buildCookie()}")
        val request = Request.Builder()
            .url(url)
            .post(bodyRequest)
            .header("cookie", buildCookie())
            .header("X-Requested-With", "XMLHttpRequest")
            .header("Accept-Language", "en-US,en;q=0.5")
            .header("sec-fetch-site", "same-origin")
            .header("sec-fetch-mode", "cors")
            .header("sec-fetch-dest", "empty")
            .header("origin", baseUrl)
            .header("referer", match.detailPage.toHttpUrl().toString())
            .header("user-agent", Constants.USER_AGENT)
            .header("accept-encoding", "application/json")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body.string().let {
                    try {
                        val vtvStream = Gson().fromJson(it, VtvStream::class.java)
                        val rs = client.newCall(
                            Request.Builder()
                                .url("https://kms.vtvdigital.vn/identity")
                                .header("user-agent", Constants.USER_AGENT)
                                .post(
                                    FormBody.Builder()
                                        .addEncoded("app_id", appId)
                                        .addEncoded("device_type", "2")
                                        .addEncoded("sign", hlsToken)
                                        .build()
                                )
                                .build()
                        ).execute()
                        if (rs.code == 200) {
                            val json = rs.body.string()
                            val token = JSONObject(json).getString("token")
                            getRealChunks(vtvStream, token, onSuccess, match, onError)
                        } else {
                            onSuccess(vtvStream.stream_url.map { stream ->
                                LinkStreamWithReferer(stream, match.detailPage)
                            })
                        }
                    } catch (e: Exception) {
                        Log.e("TAG", e.message, e)
                        onError(e)
                    }

                }
            }

        })
    }

    private fun getRealChunks(
        vtvStream: VtvStream,
        token: String,
        onSuccess: (data: List<LinkStreamWithReferer>) -> Unit,
        match: FootballMatch,
        onError: (t: Throwable) -> Unit
    ) {
        getRealChunks(vtvStream.stream_url, token, {
            onSuccess(
                it.map {
                    LinkStreamWithReferer(it, match.detailPage).apply {
                        this.token = token
//                        this.host = "w1tow2cvcylivtc.vcdn.com.vn"
                    }
                }
            )
            it.forEach { link ->
                Log.e("TAG", link)
                client.newCall(Request.Builder()
                    .url(link)
                    .build()
                ).execute()
            }

        }, {
            onError(it)
        })
    }

    private fun buildCookie(): String {
        val cookieBuilder = StringBuilder()
        for (i in cookie.entries) {
            cookieBuilder.append(i.key)
                .append("=")
                .append(i.value)
                .append(";")
                .append(" ")
        }
        return cookieBuilder.toString().trim().removeSuffix(";")
    }

    private val baseUrl = "https://vtvgo.vn/"
    private val mainPagePath = "trang-chu.html"
    private val getLinkStreamPath = "ajax-get-stream"
    private fun getRealChunks(
        streamUrl: List<String>,
        token: String,
        onSuccess: (realChunks: List<String>) -> Unit,
        onError: (t: Throwable) -> Unit,
        retry: Int = 3
    ) {
        val m3u8Url = streamUrl.first()
        var url = m3u8Url
        client.newCall(
            Request.Builder()
                .url(m3u8Url)
                .addHeader("Origin", baseUrl.removeSuffix("/"))
                .addHeader("Referer", baseUrl.toHttpUrl().toString())
                .addHeader("Cookie", buildCookie())
                .addHeader("User-Agent", Constants.USER_AGENT)
//                .addHeader("Host", m3u8Url.toHttpUrl().host)
                .addHeader("Authorization", token)
                .addHeader("token", token)
                .build()
        ).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e("TAG", "CODE: ${response.code}")
                if (response.code != 200) {
                    onSuccess(streamUrl)
                    return
                }
//                if (response.code == 403 && retry > 0) {
//                    getRealChunks(streamUrl.map {
//                        val url = it.replace("https://live-drm.vtvdigital.vn", "https://w1tow2cvcylivtc.vcdn.com.vn")
//                        url
//                    }, token, onSuccess, onError, retry - 1)
//                    return
//                } else
                val res: List<String>? = response.body?.string()?.let {
                    val realChunks = it.split("\n").filter {
                        it.trim().isNotEmpty() && !it.trim().startsWith("#")
                    }
                    val index = m3u8Url.indexOf(".m3u8")
                    val subUrl = m3u8Url.substring(0, index + 5)
                    val lastIndex = subUrl.lastIndexOf("/")
                    val host = subUrl.substring(0, lastIndex)
                    realChunks.map {
                        "$host/$it"
                    }
                }

                res?.let { onSuccess(it) } ?: onSuccess(listOf(m3u8Url))
            }

        })
    }

    private fun getVarFromHtml(name: String, text: String): String? {
        val regex = "(?<=var\\s$name\\s=\\s\').*?(?=\')"
        Log.e("TAG", regex)
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(text)
        var value: String? = null

        while (matcher.find()) {
            value = matcher.group(0)
        }
        return value
    }

    private fun getVarNumberFromHtml(name: String, text: String): String? {
        val regex = "(?<=var\\s$name\\s=\\s)(\\d+)"
        Log.e("TAG", regex)
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(text)
        while (matcher.find()) {
            return matcher.group(0)
        }
        return null
    }

    private fun anyNotNull(vararg variable: Any?): Boolean {
        for (v in variable) {
            if (v == null) return false
        }
        return true
    }
}

fun String.toOrigin(): String {
    return this.toHttpUrl().toOrigin()
}

fun HttpUrl.toOrigin(): String {
    return "${this.scheme}://${this.host}/"
}

data class VtvStream(
    val ads_tags: String,
    val ads_time: String,
    val channel_name: String,
    val chromecast_url: String,
    val content_id: Int,
    val date: String,
    val geoname_id: String,
    val is_drm: Boolean,
    val player_type: String,
    val remoteip: String,
    val stream_info: String,
    val stream_url: List<String>
)