package com.kt.apps.xembongda.model.schedule

import com.kt.apps.xembongda.model.FootballTeam

sealed class LeagueSchedulerDTO {
    class Title(
        var league: String? = null
    ) : LeagueSchedulerDTO()

    class Match(
        var time: String? = null,
        var date: String? = null,
        var score: String? = null,
        var homeTea: FootballTeam? = null,
        var awayTeam: FootballTeam? = null,
        var link: String? = null,
        var matchStadium: String? = null
    ) : LeagueSchedulerDTO()
}