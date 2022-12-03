package com.kt.apps.xembongda.model

data class LinkStreamWithReferer(
    val m3u8Link: String,
    val referer: String
) {
    var token: String? = null
    var host: String? = null
}