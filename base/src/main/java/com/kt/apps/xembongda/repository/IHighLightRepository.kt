package com.kt.apps.xembongda.repository

import com.kt.apps.xembongda.model.highlights.HighLightDTO
import com.kt.apps.xembongda.model.highlights.HighLightDetail
import io.reactivex.rxjava3.core.Observable

interface IHighLightRepository {
    fun getHighlights(page: Int) : Observable<List<HighLightDTO>>
    fun getHighLightDetail(highLight: HighLightDTO) : Observable<HighLightDetail>
}