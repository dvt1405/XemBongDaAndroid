package com.kt.apps.xembongda.repository.highlight

import android.util.Log
import com.google.gson.Gson
import com.kt.apps.xembongda.di.RepositoryModule
import com.kt.apps.xembongda.model.LinkStreamWithReferer
import com.kt.apps.xembongda.model.highlights.HighLightDTO
import com.kt.apps.xembongda.model.highlights.HighLightDetail
import com.kt.apps.xembongda.repository.IHighLightRepository
import com.kt.apps.xembongda.repository.config.FootballRepositoryConfig
import com.kt.apps.xembongda.storage.IKeyValueStorage
import com.kt.apps.xembongda.utils.jsoupParse
import io.reactivex.rxjava3.core.Observable
import org.jsoup.nodes.Element
import java.util.regex.Pattern
import javax.inject.Inject

class XoiLacHighLightRepositoryImpl @Inject constructor(
    @RepositoryModule.SourceXoiLac10Config
    private val config: FootballRepositoryConfig,
    private val keyValueStorage: IKeyValueStorage
) : IHighLightRepository {
    companion object {
        private const val EXTRA_COOKIE_NAME = "extra:cookie_mitom"
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

    override fun getHighlights(page: Int): Observable<List<HighLightDTO>> {
        return Observable.create { emitter ->
            if (page < cachePage) {
                emitter.onNext(cacheList.subList((page - 1) * numPerPage, page * numPerPage))
            } else {
                cachePage = maxOf(cachePage, page)
                val oldIndex = cacheList.size
                val jsoup = jsoupParse("${config.url}xem-lai-bong-da/page/$page", cookie)
                cookie.putAll(jsoup.cookie)
                keyValueStorage.save(EXTRA_COOKIE_NAME, cookie)
                val body = jsoup.body
                val items = body.getElementsByClass("post-item col-lg-3 col-12 mb-4")
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
            val thumbnail = item.getElementsByClass("post-thumbnail col-5 col-lg-12 p-0")[0]
                .getElementsByTag("a")[0]
            val href = thumbnail.attr("href")
            val logo = thumbnail.getElementsByTag("img")[0].attr("src")
            val titleElement = item.getElementsByClass("post-title col-7 col-lg-12 p-3")[0]
            val title = titleElement.getElementsByTag("a")[0].text()
            var home = title
            var away = title
            try {
                val titleTmp = try {
                    title.substring(title.indexOf("("), title.length)
                } catch (_: Exception) {
                    title
                }
                val titleSplit = titleTmp.trim().split("vs")
                home = titleSplit[0]
                away = titleSplit[0]
            } catch (_: Exception) {
            }
            return HighLightDTO(
                href,
                logo,
                home = home,
                away = away,
                time = title,
                title,
                href
            )
        } catch (e: Exception) {
            Log.e("TAG", e.message, e)
            null
        }

    }

    override fun getHighLightDetail(highLight: HighLightDTO): Observable<HighLightDetail> {
        return Observable.create {
            val response = jsoupParse(highLight.detailPage, cookie)
            this.cookie.putAll(response.cookie)
            val pattern = Pattern.compile("(?<=file:\\s\').*?(?=\')")
            val rs = response.body
            val listLink = mutableListOf<LinkStreamWithReferer>()
            rs.getElementsByTag("script")
                .forEach {
                    try {
                        val html = it.html()
                        if (html.contains(".m3u8")) {
                            val matcher = pattern.matcher(html)
                            while (matcher.find()) {
                                val link = matcher.group(0) ?: continue
                                listLink.add(LinkStreamWithReferer(link, highLight.detailPage))
                            }
                        }
                    } catch (_: Exception) {
                    }
                }
            Log.e("TAG", Gson().toJson(listLink))
            if (listLink.isEmpty()) {
                it.onError(Throwable(""))
            } else {
                it.onNext(HighLightDetail(highLight, listLink))
            }
        }
    }
}