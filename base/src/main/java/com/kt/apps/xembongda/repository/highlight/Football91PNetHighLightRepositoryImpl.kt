package com.kt.apps.xembongda.repository.highlight

import android.util.Log
import com.kt.apps.xembongda.base.BuildConfig
import com.kt.apps.xembongda.di.RepositoryModule
import com.kt.apps.xembongda.di.config.HighLightConfig
import com.kt.apps.xembongda.model.League
import com.kt.apps.xembongda.model.highlights.HighLightDTO
import com.kt.apps.xembongda.model.highlights.HighLightDetail
import com.kt.apps.xembongda.repository.IHighLightRepository
import com.kt.apps.xembongda.repository.config.FootballRepositoryConfig
import com.kt.apps.xembongda.storage.IKeyValueStorage
import com.kt.apps.xembongda.utils.jsoupParse
import io.reactivex.rxjava3.core.Observable
import org.jsoup.nodes.Element
import javax.inject.Inject

class Football91PNetHighLightRepositoryImpl @Inject constructor(
    @RepositoryModule.SourceBinhLuan90Config
    val config: FootballRepositoryConfig,
    val keyValueStorage: IKeyValueStorage
) : IHighLightRepository {
    companion object {
        private val TAG = XoiLacHighLightRepositoryImpl::class.java.simpleName
        private const val EXTRA_COOKIE_NAME = "extra:cookie_91p_highlight"
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
                val jsoup = jsoupParse("${config.url}highlight/$page", cookie)
                cookie.putAll(jsoup.cookie)
                keyValueStorage.save(EXTRA_COOKIE_NAME, cookie)
                val body = jsoup.body
                val items = body.getElementsByClass("grid-matches__item col-6")
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
            val thumbnail = item.getElementsByTag("a")[0]
            if (BuildConfig.DEBUG) {
                Log.d(TAG, thumbnail.html())
            }
            val href = thumbnail.attr("href")
            val logo = thumbnail.getElementsByTag("img")[0].attr("src")
            val titleElement = item.getElementsByClass("grid-matc__footer")[0]
            val title = titleElement.getElementsByClass("team")[0].text()
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
                time = title,
                title,
                href,
                sourceFrom = HighLightConfig.Source.Phut91

            )
        } catch (e: Exception) {
            Log.e("TAG", e.message, e)
            null
        }
    }

    override fun getHighLightDetail(highLight: HighLightDTO): Observable<HighLightDetail> {
        TODO("Not yet implemented")
    }
}