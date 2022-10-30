package com.kt.apps.xembongda.api

import com.kt.apps.xembongda.model.football.BinhLuanFootballMatchModelItem
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BinhLuan90PhutApi {

    @GET("api/match")
    fun getAllMatchFrom90Phut() : Observable<List<BinhLuanFootballMatchModelItem>>

    @GET("api/match/{matchId}")
    fun getMatchDetail(
        @Path(value = "matchId") mathId: String,
        @Query("id") id: String
    ): Observable<BinhLuanFootballMatchModelItem.Detail>
}