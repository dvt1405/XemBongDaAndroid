package com.kt.apps.xembongda.usecase.authenticate

import com.kt.apps.xembongda.model.authenticate.AccessTokenDTO
import com.kt.apps.xembongda.repository.IAuthenticateRepository
import com.kt.apps.xembongda.usecase.BaseUseCase
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetAccessToken @Inject constructor(
    private val authenticate: IAuthenticateRepository
) : BaseUseCase<AccessTokenDTO>() {
    override fun prepareExecute(params: Map<String, Any>): Observable<AccessTokenDTO> {
        return authenticate.getAccessToken()
    }

    operator fun invoke() = execute(mapOf())
}