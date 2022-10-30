package com.kt.apps.xembongda.api

import com.kt.apps.xembongda.model.football.VeBoDetail
import com.kt.apps.xembongda.model.football.VeBoModel
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface VeBoApi {

    @GET("api/match/live")
    fun getAllMatchFrom90Phut() : Observable<VeBoModel>

    @GET("api/match/featured")
    fun getAllOtherFrom90Phut() : Observable<VeBoModel>

    @GET("api/match/{matchId}/meta")
    fun getMatchDetail(
        @Path(value = "matchId") mathId: String,
    ): Observable<VeBoDetail>

}