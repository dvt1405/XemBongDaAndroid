package com.kt.apps.xembongda.utils

import android.util.Log
import com.kt.apps.xembongda.Constants
import com.kt.apps.xembongda.base.BuildConfig
import com.kt.apps.xembongda.exceptions.MyException
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Element


data class JsoupResponse(
    val body: Element,
    val cookie: Map<String, String>
)
private const val MAX_RETRY_COUNT = 2
fun jsoupConnect(
    url: String,
    cookie: Map<String, String>,
    vararg header: Pair<String, String>,
    retry: Int = MAX_RETRY_COUNT
): Connection {
    if (retry == 0) throw Throwable("Max retry")
    return try {
        Jsoup.connect(url)
            .followRedirects(true)
            .header("User-agent", Constants.USER_AGENT)
            .apply {
                header.forEach {
                    this.header(it.first, it.second)
                }
            }
            .cookies(cookie)
    } catch (e: InterruptedException) {
        jsoupConnect(url, cookie, *header)
    }

}

fun jsoupParse(
    url: String,
    cookie: Map<String, String>,
    vararg header: Pair<String, String>
): JsoupResponse {
    if (BuildConfig.DEBUG) {
        Log.d("Jsoup", url)
    }
    val connection = jsoupConnect(url, cookie, *header)
        .timeout(10_000)
        .followRedirects(true)
        .execute()

    if (BuildConfig.DEBUG) {
        Log.d("Jsoup", url)
        Log.d("Jsoup", "${connection.statusCode()}")
        Log.d("Jsoup", connection.statusMessage())
    }

    val body = connection.parse().body()
    return JsoupResponse(
        body,
        mutableMapOf<String, String>().apply {
            putAll(cookie)
            putAll(connection.cookies())
        }
    )
}

