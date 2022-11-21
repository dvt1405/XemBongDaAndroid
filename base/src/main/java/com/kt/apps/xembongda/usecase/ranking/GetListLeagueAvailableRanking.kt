package com.kt.apps.xembongda.usecase.ranking

import com.kt.apps.xembongda.model.League
import com.kt.apps.xembongda.repository.IRankingRepository
import com.kt.apps.xembongda.usecase.BaseUseCase
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetListLeagueAvailableRanking @Inject constructor(
    private val repository: IRankingRepository
) : BaseUseCase<List<League>>() {
    override fun prepareExecute(params: Map<String, Any>): Observable<List<League>> {


        return repository.getListLeagueRanking()
    }

    operator fun invoke() = execute(mapOf())

    operator fun invoke(filer: String) = execute(mapOf())
}