package com.kt.apps.xembongda.usecase.highlights

import com.kt.apps.xembongda.model.highlights.HighLightDTO
import com.kt.apps.xembongda.repository.IHighLightRepository
import com.kt.apps.xembongda.usecase.BaseUseCase
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetAllHighLights @Inject constructor(
    private val repository: IHighLightRepository
) : BaseUseCase<List<HighLightDTO>>() {
    override fun prepareExecute(params: Map<String, Any>): Observable<List<HighLightDTO>> {
        return repository.getHighlights(params["page"] as Int)
    }

    operator fun invoke(page: Int) = execute(
        mapOf(
            "page" to page
        )
    )
}