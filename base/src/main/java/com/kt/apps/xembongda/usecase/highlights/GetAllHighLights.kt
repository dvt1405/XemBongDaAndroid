package com.kt.apps.xembongda.usecase.highlights

import com.kt.apps.xembongda.di.config.HighLightConfig
import com.kt.apps.xembongda.model.highlights.HighLightDTO
import com.kt.apps.xembongda.repository.IHighLightRepository
import com.kt.apps.xembongda.usecase.BaseUseCase
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetAllHighLights @Inject constructor(
    private val sourceRepository: Map<HighLightConfig.Source, @JvmSuppressWildcards IHighLightRepository>
) : BaseUseCase<List<HighLightDTO>>() {
    override fun prepareExecute(params: Map<String, Any>): Observable<List<HighLightDTO>> {
        val repository = sourceRepository[params["source"]]
        return repository!!.getHighlights(params["page"] as Int)
    }

    operator fun invoke(page: Int, source: HighLightConfig.Source = HighLightConfig.Source.Mitom10) = execute(
        mapOf(
            "page" to page,
            "source" to source
        )
    )
}