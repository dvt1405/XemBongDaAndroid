package com.kt.apps.xembongda.usecase.authenticate

import com.kt.apps.xembongda.model.UserDTO
import com.kt.apps.xembongda.model.authenticate.AuthenticateMethod
import com.kt.apps.xembongda.repository.IAuthenticateRepository
import com.kt.apps.xembongda.usecase.BaseUseCase
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class RegisterNewUser @Inject constructor(
    private val authenRepo: IAuthenticateRepository
) : BaseUseCase<UserDTO>() {
    override fun prepareExecute(params: Map<String, Any>): Observable<UserDTO> {
        val method = params["method"] as AuthenticateMethod
        return authenRepo.registerNewAccount(method)
    }

    operator fun invoke(email: String, pass: String) = execute(
        mapOf("method" to AuthenticateMethod.Email(email, pass))
    )
}