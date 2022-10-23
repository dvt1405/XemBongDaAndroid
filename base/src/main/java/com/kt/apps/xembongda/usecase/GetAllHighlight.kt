package com.kt.apps.xembongda.usecase

import com.kt.apps.xembongda.model.HighlightDTO
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetAllHighlight @Inject constructor() : BaseUseCase<HighlightDTO>() {
    override fun prepareExecute(params: Map<String, Any>): Observable<HighlightDTO> {
        TODO("Not yet implemented")
    }

    operator fun invoke() {

    }
}