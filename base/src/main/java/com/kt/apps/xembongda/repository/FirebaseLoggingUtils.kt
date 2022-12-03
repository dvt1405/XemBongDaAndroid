package com.kt.apps.xembongda.repository

import android.util.Log
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom

object FirebaseLoggingUtils {
    private val firebaseAnalytics: FirebaseAnalytics
        get() = Firebase.analytics


    fun logGetAllMatches(source: FootballRepoSourceFrom?) {
        firebaseAnalytics.logEvent(
            "GetAllMatches", bundleOf(
                "sourceFrom" to (source?.name ?: "BACKUP")
            )
        )
    }

    fun logGetAllMatchesFail(source: FootballRepoSourceFrom?, throwable: Throwable) {
        firebaseAnalytics.logEvent(
            "GetAllMatchesFail", bundleOf(
                "sourceFrom" to (source?.name ?: "BACKUP"),
                "throwableClass" to throwable::class.java.name,
                "throwableStackTrace" to Log.getStackTraceString(throwable),

            )
        )
    }

    fun logGetMatchesDetail(match: FootballMatch, source: FootballRepoSourceFrom?) {
        firebaseAnalytics.logEvent(
            "GetMatchDetail", bundleOf(
                "sourceFrom" to (source?.name ?: "BACKUP"),
                "match" to "${match.homeTeam.name}_${match.awayTeam.name}"
            )
        )
    }

    fun logGetMatchesDetailFail(match: FootballMatch, source: FootballRepoSourceFrom?, throwable: Throwable) {
        firebaseAnalytics.logEvent(
            "GetMatchDetailFail", bundleOf(
                "sourceFrom" to (source?.name ?: "BACKUP"),
                "match" to "${match.homeTeam.name}_${match.awayTeam.name}",
                "link" to match.detailPage,
                "throwableClass" to throwable::class.java.name,
                "throwableStackTrace" to Log.getStackTraceString(throwable),

                )
        )
    }


}