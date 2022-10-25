package com.kt.apps.xembongda.usecase

import com.kt.apps.xembongda.model.UserDTO
import com.kt.apps.xembongda.repository.IAuthenticateRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetUserInfo @Inject constructor(
    private val authenticateRepo: IAuthenticateRepository
) : BaseUseCase<UserDTO>() {
    override fun prepareExecute(params: Map<String, Any>): Observable<UserDTO> {
        return authenticateRepo.getCurrentUserInfo()
    }

    operator fun invoke() = execute(mapOf())

}