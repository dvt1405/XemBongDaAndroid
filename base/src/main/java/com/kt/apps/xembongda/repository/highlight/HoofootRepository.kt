package com.kt.apps.xembongda.repository.highlight

import android.util.Log
import androidx.core.text.htmlEncode
import com.kt.apps.xembongda.base.BuildConfig
import com.kt.apps.xembongda.di.config.HighLightConfig
import com.kt.apps.xembongda.model.League
import com.kt.apps.xembongda.model.LinkStreamWithReferer
import com.kt.apps.xembongda.model.highlights.HighLightDTO
import com.kt.apps.xembongda.model.highlights.HighLightDetail
import com.kt.apps.xembongda.repository.IHighLightRepository
import com.kt.apps.xembongda.storage.IKeyValueStorage
import com.kt.apps.xembongda.utils.jsoupParse
import io.reactivex.rxjava3.core.Observable
import org.jsoup.nodes.Element
import java.util.regex.Pattern
import javax.inject.Inject

class HoofootRepository @Inject constructor(
    private val keyValueStorage: IKeyValueStorage
) : IHighLightRepository {
    companion object {
        private const val EXTRA_COOKIE_NAME = "extra:cookie_mitom"
        private const val URL = "https://hoofoot.com/"
        private val TAG = HoofootRepository::class.java.simpleName
    }


    private val cacheList by lazy {
        mutableListOf<HighLightDTO>()
    }

    private var cachePage: Int = 0

    private val numPerPage: Int
        get() = if (cachePage == 0) 10 else cacheList.size / cachePage


    private val cookie: MutableMap<String, String> by lazy {
        keyValueStorage.get(
            EXTRA_COOKIE_NAME,
            String::class.java,
            String::class.java
        ).toMutableMap()
    }

    override fun getHighlights(page: Int, league: League): Observable<List<HighLightDTO>> {
        TODO("Not yet implemented")
    }

    override fun getHighlights(page: Int): Observable<List<HighLightDTO>> {
        return Observable.create { emitter ->
            if (page < cachePage) {
                emitter.onNext(cacheList.subList((page - 1) * numPerPage, page * numPerPage))
            } else {
                cachePage = maxOf(cachePage, page)
                val oldIndex = cacheList.size
                val jsoup = jsoupParse("${URL}?page=$page", cookie)
                cookie.putAll(jsoup.cookie)
                keyValueStorage.save(EXTRA_COOKIE_NAME, cookie)
                val body = jsoup.body
                val items = body.getElementsByClass("box")[0].getElementsByTag("table")
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, body.html())
                }
                items.forEach {
                    mapToHighLightItems(it)?.let {
                        cacheList.add(it)
                    }
                }
                emitter.onNext(cacheList.subList(oldIndex, cacheList.size))
            }
        }
    }

    private fun mapToHighLightItems(item: Element): HighLightDTO? {
        return try {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, item.html())
            }
            val linkDetail = item.getElementsByTag("a")[0]
            var href = linkDetail.attr("href")
            Log.e(TAG, href)

            val logo = item.getElementsByTag("img")[0].attr("src")
            Log.e(TAG, logo)

            var time = item.getElementsByTag("font")[0].text()

            if (time.trim().isEmpty()) {
                time = " ($time)"
            }
            Log.e(TAG, time)
            val title = item.getElementsByTag("h2")[0].text()
            Log.e(TAG, title)

            var home = title
            var away = title
            try {
                val titleTmp = try {
                    title.substring(title.indexOf("("), title.length)
                } catch (_: Exception) {
                    title
                }
                val titleSplit = titleTmp.trim().split(" v ")
                home = titleSplit[0]
                away = titleSplit[0]
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, e.message, e)
                }
            }
            return HighLightDTO(
                href,
                logo,
                home = home,
                away = away,
                time = time.replace("(", "").replace(")", ""),
                title = title,
                detailPage = if (href.startsWith("http")) href else {
                    while (!href.startsWith("/")) {
                        href = href.substring(1, href.length)
                    }
                    URL + href
                },
                sourceFrom = HighLightConfig.Source.HooFoot
            )
        } catch (e: Exception) {
            null
        }
    }

    override fun getHighLightDetail(highLight: HighLightDTO): Observable<HighLightDetail> {
        return Observable.create {
            val jsoup = jsoupParse(highLight.detailPage, cookie)
            cookie.putAll(jsoup.cookie)
            val body = jsoup.body
            Log.e(TAG, body.html())
            val player = body.getElementById("player")!!
            val href = player.getElementsByTag("a")[0].attr("href")
            val links = makeM3u8Link(href).map {
                LinkStreamWithReferer(it, highLight.detailPage)
            }
            if (links.isNotEmpty()) {
                it.onNext(HighLightDetail(highLight, links))
            }
        }
    }

    private fun makeM3u8Link(href: String): List<String> {
        val jsoup = jsoupParse(href, cookie)
        cookie.putAll(jsoup.cookie)
        val body = jsoup.body.html()
        Log.e(TAG, body)
        val regex = Pattern.compile("(?<=hls:').*?(?=')")
        val matcher = regex.matcher(body)
        val listLink = mutableListOf<String>()
        while (matcher.find()) {
            matcher.group(0)?.let {
                if (it.startsWith("http")) {
                    listLink.add(it)
                } else {
                    listLink.add("https:$it")
                }
            }
        }
        return listLink
    }
}

