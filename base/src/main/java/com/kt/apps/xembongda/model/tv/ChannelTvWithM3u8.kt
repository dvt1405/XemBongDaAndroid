package com.kt.apps.xembongda.model.tv

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChannelTvWithM3u8(
    var tvChannel: KenhTvDetail,
    var m3u8Links: List<String>
) : Parcelable
