package com.kt.apps.xembongda.model

import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.utils.removeAllSpecialChars

class FootballMatch(
    val homeTeam: FootballTeam,
    val awayTeam: FootballTeam,
    val kickOffTime: String,
    val statusStream: String,
    val detailPage: String,
    val sourceFrom: FootballRepoSourceFrom,
    val league: String = "",
    val matchId: String = detailPage
) {
    fun getMatchIdForComment() = "${
        homeTeam.name.trim().removeAllSpecialChars()
    }_${awayTeam.name.trim().removeAllSpecialChars()}"
}