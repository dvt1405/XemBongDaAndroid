package com.kt.apps.xembongda.model.football

import com.google.gson.annotations.SerializedName

class BinhLuanFootballMatchModel : ArrayList<BinhLuanFootballMatchModelItem>()

data class BinhLuanFootballMatchModelItem(
    @SerializedName("data")
    val data: List<Data>,
    val title: String
) {

    data class Data(
        val _id: String,
        val dateStartPlay: String,
        val dateStartPlayMod: String,
        val dayinweek: String,
        val guestInfo: GuestInfo,
        val guestScore: String,
        val homeInfo: HomeInfo,
        val homeScore: String,
        val id: String,
        val imageHost: String,
        val status: Boolean,
        val statusDisplay: String,
        val timeStartPlay: String,
        val timeStartPlayMod: String,
        val timestampPresentLive: Int,
        val title: String,
        val tournamentInfo: TournamentInfo
    )

    data class GuestInfo(
        val _id: String,
        val imageHost: String,
        val name: String
    )

    data class HomeInfo(
        val _id: String,
        val imageHost: String,
        val name: String
    )

    data class TournamentInfo(
        val _id: String,
        val imageHost: String,
        val name: String
    )



    data class Detail(
        val _id: String,
        val arbitration: String,
        val audience: String,
        val dateStartPlay: String,
        val dateStartPlayMod: String,
        val dayinweek: String,
        val guestInfo: GuestInfo,
        val guestScore: String,
        val homeInfo: HomeInfo,
        val homeScore: String,
        val id: String,
        val imageHost: String,
        val playingFlagsGuest: List<String>,
        val playingFlagsHome: List<String>,
        val playingGuestContent: List<String>,
        val playingHomeContent: List<String>,
        val playingMinute: List<String>,
        val squadGuestAsText: String,
        val squadGuestCoach: String,
        val squadGuestNameMain: List<String>,
        val squadGuestNameSub: List<String>,
        val squadGuestNumberMain: List<String>,
        val squadGuestNumberSub: List<String>,
        val squadHomeAsText: String,
        val squadHomeCoach: String,
        val squadHomeNameMain: List<String>,
        val squadHomeNameSub: List<String>,
        val squadHomeNumberMain: List<String>,
        val squadHomeNumberSub: List<String>,
        val stadium: String,
        val statisticAction: List<String>,
        val statisticGuestNote: List<String>,
        val statisticHomeNote: List<String>,
        val statisticRatio: List<String>,
        val status: Boolean,
        val statusDisplay: String,
        val streamsLink: List<StreamsLink>,
        val timeStartPlay: String,
        val timeStartPlayMod: String,
        val timestampPresentLive: Int,
        val title: String,
        val tournamentInfo: TournamentInfo
    )

    data class StreamsLink(
        val label: String,
        val link: String,
        val permission: String,
        val secure: String,
        val status: String,
        val type: String
    )

}
