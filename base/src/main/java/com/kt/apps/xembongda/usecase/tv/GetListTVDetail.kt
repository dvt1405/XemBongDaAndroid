package com.kt.apps.xembongda.usecase.tv

import com.kt.apps.xembongda.model.tv.KenhTvDetail
import com.kt.apps.xembongda.repository.ITVDataSource
import com.kt.apps.xembongda.usecase.BaseUseCase
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetListTVDetail @Inject constructor(
    private val dataSource: ITVDataSource
) : BaseUseCase<List<KenhTvDetail>>() {
    override fun prepareExecute(params: Map<String, Any>): Observable<List<KenhTvDetail>> {
        return dataSource.getTvList()
    }

    operator fun invoke() = execute(mapOf())
}