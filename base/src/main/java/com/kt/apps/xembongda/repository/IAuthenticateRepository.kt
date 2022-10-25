package com.kt.apps.xembongda.repository

import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.kt.apps.xembongda.model.UserDTO
import com.kt.apps.xembongda.model.authenticate.AccessTokenDTO
import com.kt.apps.xembongda.model.authenticate.AccountRegisterResponse
import com.kt.apps.xembongda.model.authenticate.AuthenticateMethod
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface IAuthenticateRepository {
    fun getCurrentUserInfo(): Observable<UserDTO>
    fun getAccessToken(): Observable<AccessTokenDTO>
    fun isAccessTokenValid() : Observable<Boolean>
    fun registerNewAccount(loginMethod: AuthenticateMethod): Observable<UserDTO>
    fun logout(): Completable
    fun login(loginMethod: AuthenticateMethod): Observable<UserDTO>
    fun refreshAccessToken(): Observable<AccessTokenDTO>
    fun saveToken(registerResponse: AccountRegisterResponse)
    fun deleteAccountInfo()
}