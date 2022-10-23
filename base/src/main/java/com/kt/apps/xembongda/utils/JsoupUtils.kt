package com.kt.apps.xembongda.utils

import com.kt.apps.xembongda.Constants
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Element


data class JsoupResponse(
    val body: Element,
    val cookie: Map<String, String>
)

fun jsoupConnect(
    url: String,
    cookie: Map<String, String>,
    vararg header: Pair<String, String>
): Connection {
    return Jsoup.connect(url)
        .header("User-agent", Constants.USER_AGENT)
        .apply {
            header.forEach {
                this.header(it.first, it.second)
            }
        }
        .cookies(cookie)
}

fun jsoupParse(
    url: String,
    cookie: Map<String, String>,
    vararg header: Pair<String, String>
): JsoupResponse {
    val connection = jsoupConnect(url, cookie, *header).execute()
    val body = connection.parse().body()
    return JsoupResponse(
        body,
        mutableMapOf<String, String>().apply {
            putAll(cookie)
            putAll(connection.cookies())
        }
    )
}

