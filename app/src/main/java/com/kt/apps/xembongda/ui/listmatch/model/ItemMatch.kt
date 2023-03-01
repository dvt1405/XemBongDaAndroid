package com.kt.apps.xembongda.ui.listmatch.model

import com.google.android.gms.ads.nativead.NativeAd
import com.kt.apps.xembongda.model.highlights.HighLightDTO

sealed class ItemMatch {
    class Ads(var ad: NativeAd? = null) : ItemMatch()
    class DTO(val dto: HighLightDTO) : ItemMatch()
}