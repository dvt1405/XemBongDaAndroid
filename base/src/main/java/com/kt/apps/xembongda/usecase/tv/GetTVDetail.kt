package com.kt.apps.xembongda.usecase.tv

import com.kt.apps.xembongda.model.LinkStreamWithReferer
import com.kt.apps.xembongda.model.tv.KenhTvDetail
import com.kt.apps.xembongda.repository.ITVDataSource
import com.kt.apps.xembongda.usecase.BaseUseCase
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetTVDetail @Inject constructor(
    private val dataSource: ITVDataSource
) : BaseUseCase<List<LinkStreamWithReferer>>() {
    override fun prepareExecute(params: Map<String, Any>): Observable<List<LinkStreamWithReferer>> {
        return dataSource.getTvLinkFromDetail(
            params["channel"] as KenhTvDetail
        )
    }

    operator fun invoke(kenhTvDetail: KenhTvDetail) =
        execute(mapOf(
            "channel" to kenhTvDetail
        ))

}