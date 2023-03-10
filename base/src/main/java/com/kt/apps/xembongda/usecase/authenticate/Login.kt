package com.kt.apps.xembongda.usecase.authenticate

import com.kt.apps.xembongda.model.UserDTO
import com.kt.apps.xembongda.model.authenticate.AuthenticateMethod
import com.kt.apps.xembongda.repository.IAuthenticateRepository
import com.kt.apps.xembongda.usecase.BaseUseCase
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class Login @Inject constructor(
    private val repository: IAuthenticateRepository
) : BaseUseCase<UserDTO>() {
    override fun prepareExecute(params: Map<String, Any>): Observable<UserDTO> {
        val method = params["method"] as AuthenticateMethod

        return repository.login(method)
    }

    operator fun invoke(userName: String, password: String) = execute(
        mapOf(
            "method" to AuthenticateMethod.Email(userName, password)
        )
    )

    operator fun invoke(google: AuthenticateMethod.Google): Observable<UserDTO> {
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .requestId()
//            .requestProfile()
//            .build()
//
//        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        return execute(
            mapOf(
                "method" to google
            )
        )
    }
}