package com.kt.apps.xembongda.model.tv

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class KenhTvDetail(
    var group: String,
    var logoChannel: String,
    var name: String,
    var detailPage: String,
    var sourceFrom: String,
    var id: String
) : Parcelable
