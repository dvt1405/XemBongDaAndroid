package com.kt.apps.xembongda.model.highlights

import android.os.Parcelable
import com.kt.apps.xembongda.di.config.HighLightConfig
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import kotlinx.parcelize.Parcelize

@Parcelize
class HighLightDTO(
    val matchId: String,
    val thumbs: String,
    val home: String,
    val away: String,
    val time: String,
    val title: String,
    val detailPage: String,
    val sourceFrom: HighLightConfig.Source
) : Parcelable {
}