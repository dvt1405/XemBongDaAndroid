package com.kt.apps.xembongda.model.football

data class VeBoModel(
    val data: List<Data>,
    val status: Int
) {

    data class Data(
        val away: Team,
        val away_red_cards: Int,
        val commentators: List<Commentator>,
        val date: String,
        val has_lineup: Boolean,
        val has_tracker: Boolean,
        val home: Team,
        val home_red_cards: Int,
        val id: String,
        val is_featured: Boolean,
        val is_live: Boolean,
        val key_sync: String,
        val live_tracker: String,
        val match_status: String,
        val name: String,
        val scores: Scores,
        val slug: String,
        val sport_type: String,
        val time_str: String,
        val timestamp: Long,
        val tournament: Tournament,
        val win_code: Int
    )

    data class Team(
        val id: String,
        val logo: String,
        val name: String,
        val short_name: String,
        val slug: String
    )

    data class Commentator(
        val avatar: String,
        val id: String,
        val name: String
    )

    data class Scores(
        val away: Int,
        val home: Int,
        val sport_type: String
    )

    data class Tournament(
        val logo: String,
        val name: String,
        val priority: Int,
        val unique_tournament: UniqueTournament
    )

    data class UniqueTournament(
        val id: String,
        val is_featured: Boolean,
        val logo: String,
        val name: String,
        val priority: Int,
        val slug: String
    )

}

data class VeBoDetail(
    val data: Data,
    val status: Int
) {

    data class Data(
        val commentators: List<Commentator>,
        val has_lineup: Boolean,
        val has_tracker: Boolean,
        val id: String,
        val play_urls: List<PlayUrl>
    )

    data class Commentator(
        val avatar: String,
        val id: String,
        val name: String
    )

    data class PlayUrl(
        val cdn: String,
        val name: String,
        val role: String,
        val url: String
    )

}
