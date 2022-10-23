package com.kt.apps.xembongda.model

import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom

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
}