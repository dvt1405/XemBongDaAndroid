package com.kt.apps.xembongda.model

sealed class LiveScoreDTO {
    class Title(
        var league: String? = null
    ) : LiveScoreDTO() {
        override fun equals(other: Any?): Boolean {
            if (other is Title) {
                return other.hashCode() == hashCode()
            }
            return super.equals(other)
        }

        override fun hashCode(): Int {
            return league?.trim()?.replace(" ", "")?.lowercase()?.hashCode() ?: 0
        }
    }

    class Match(
        var time: String? = null,
        var date: String? = null,
        var score: String? = null,
        var homeTea: FootballTeam? = null,
        var awayTeam: FootballTeam? = null,
        var link: String? = null,
        var matchStadium: String? = null,
        var header: Title? = null
    ) : LiveScoreDTO(

    ) {

    }

}