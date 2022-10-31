package com.kt.apps.xembongda.usecase.highlights

import com.kt.apps.xembongda.model.highlights.HighLightDTO
import com.kt.apps.xembongda.model.highlights.HighLightDetail
import com.kt.apps.xembongda.repository.IHighLightRepository
import com.kt.apps.xembongda.usecase.BaseUseCase
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetHighLightsDetail @Inject constructor(
    private val repository: IHighLightRepository
) : BaseUseCase<HighLightDetail>() {
    override fun prepareExecute(params: Map<String, Any>): Observable<HighLightDetail> {
        return repository.getHighLightDetail(params["highLight"] as HighLightDTO)
    }

    operator fun invoke(highLightDTO: HighLightDTO) = execute(
        mapOf(
            "highLight" to highLightDTO
        )
    )
}