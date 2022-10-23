package com.kt.apps.xembongda.usecase

import com.kt.apps.xembongda.model.LiveScoreDTO
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetAllLiveScore @Inject constructor() : BaseUseCase<LiveScoreDTO>() {
    override fun prepareExecute(params: Map<String, Any>): Observable<LiveScoreDTO> {
        TODO("Not yet implemented")
    }


    operator fun invoke() {

    }
}