package com.kt.apps.xembongda.repository.highlight

import com.kt.apps.xembongda.model.League
import com.kt.apps.xembongda.model.highlights.HighLightDTO
import com.kt.apps.xembongda.model.highlights.HighLightDetail
import com.kt.apps.xembongda.repository.IHighLightRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class Xoilac6LiveOrg @Inject constructor(

) : IHighLightRepository {

    companion object {
        private const val url = "https://xoilac6live.org/"
    }

    override fun getHighlights(page: Int, league: League): Observable<List<HighLightDTO>> {
        TODO("Not yet implemented")
    }

    override fun getHighlights(page: Int): Observable<List<HighLightDTO>> {
        TODO("Not yet implemented")
    }

    override fun getHighLightDetail(highLight: HighLightDTO): Observable<HighLightDetail> {
        TODO("Not yet implemented")
    }
}