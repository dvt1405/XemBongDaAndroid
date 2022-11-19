package com.kt.apps.xembongda.model

sealed class LiveScoreDTO {
    class Title(
        var league: String? = null
    ) : LiveScoreDTO()

    class Match(
        var time: String? = null,
        var date: String? = null,
        var score: String? = null,
        var homeTea: FootballTeam? = null,
        var awayTeam: FootballTeam? = null,
        var link: String? = null,
        var matchStadium: String? = null
    ) : LiveScoreDTO(

    )
}