package com.kt.apps.xembongda.model

data class FootballMatchWithStreamLink(
    val match: FootballMatch,
    val linkStreams: List<LinkStreamWithReferer>
) {
}