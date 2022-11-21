package com.kt.apps.xembongda.ui.worldcup.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EuroFootballMatchItem(
    val matchs: List<Match>,
    val name: String,
    val table: List<FootbalMatchTable>
) : Parcelable

@Parcelize
data class Match(
    val awayTeam: String,
    val homeTeam: String,
    val matchTime: String,
    val scoreA: String,
    val scoreH: String
) : Parcelable {
    val matchTimeMilli
        get() = matchTime.toLong()
}

@Parcelize
data class FootbalMatchTable(
    val Draws: String,
    val GoalsA: String,
    val GoalsD: String,
    val GoalsF: String,
    val Losts: String,
    val Played: String,
    val Points: String,
    val Wins: String,
    val teamName: String
) : Parcelable {
    fun toListTableData() = listOf(Played, Points, Wins, Draws, Losts, GoalsA, GoalsF, GoalsD)

}
