package com.kt.apps.xembongda.repository.auth

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kt.apps.xembongda.Constants
import com.kt.apps.xembongda.exceptions.ErrorCode
import com.kt.apps.xembongda.exceptions.UnAuthenException
import com.kt.apps.xembongda.model.UserDTO
import com.kt.apps.xembongda.model.authenticate.AccessTokenDTO
import com.kt.apps.xembongda.model.authenticate.AccountRegisterResponse
import com.kt.apps.xembongda.model.authenticate.AuthenticateMethod
import com.kt.apps.xembongda.repository.IAuthenticateRepository
import com.kt.apps.xembongda.storage.IKeyValueStorage
import com.kt.apps.xembongda.utils.firebase.toUserDto
import com.kt.apps.xembongda.utils.toAccessToken
import com.kt.apps.xembongda.utils.toUserDTO
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject


class AuthenticateRepositoryImpl @Inject constructor(
    private val keyValueDB: IKeyValueStorage,
    private val context: Context,
) : IAuthenticateRepository {

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val _token: String
        get() = keyValueDB.get(Constants.KEY_ACCESS_TOKEN, String::class.java)

    override fun getCurrentUserInfo(): Observable<UserDTO> {
        return Observable.create {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if (account != null) {
                it.onNext(account.toUserDTO())
            } else if (auth.currentUser != null) {
                it.onNext(auth.currentUser!!.toUserDto())
            } else {
                it.onError(UnAuthenException(ErrorCode.REQUIRED_LOGIN))
            }
            it.onComplete()
        }
    }

    override fun getAccessToken(): Observable<AccessTokenDTO> {
        return Observable.create { emitter ->
            if (GoogleSignIn.getLastSignedInAccount(context) != null) {
                val account = GoogleSignIn.getLastSignedInAccount(context)
                emitter.onNext(account!!.toAccessToken())

            } else if (auth.currentUser != null) {
                emitter.onNext(
                    AccessTokenDTO(
                        auth.currentUser?.uid ?: "",
                        auth.currentUser == null
                    )
                )
            } else {
                emitter.onNext(AccessTokenDTO("", true))
                emitter.onComplete()
            }
        }
    }

    override fun isAccessTokenValid(): Observable<Boolean> {
        val valid = auth.currentUser != null
        return Observable.just(valid)
    }

    override fun registerNewAccount(loginMethod: AuthenticateMethod): Observable<UserDTO> {
        return Observable.create { emitter ->
            when (loginMethod) {
                is AuthenticateMethod.Email -> {
                    auth.createUserWithEmailAndPassword(loginMethod.email, loginMethod.password)
                        .addOnSuccessListener {
                            emitter.onNext(it.user.toUserDto())
                            emitter.onComplete()
                        }
                        .addOnFailureListener {
                            emitter.onError(it)
                        }
                }

                else -> {

                }
            }
        }
    }

    override fun logout(): Completable {
        return Completable.create { emitter ->
            auth.signOut()
            emitter.onComplete()
        }
    }

    override fun login(loginMethod: AuthenticateMethod): Observable<UserDTO> {
        return Observable.create { emitter ->
            when (loginMethod) {

                is AuthenticateMethod.Email -> {
                    auth.signInWithEmailAndPassword(loginMethod.email, loginMethod.password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                emitter.onNext(auth.currentUser.toUserDto())
                                emitter.onComplete()
                            } else {
                                emitter.onError(Throwable("Not sign in success"))
                            }
                        }.addOnFailureListener {
                            if (!emitter.isDisposed) {
                                emitter.onError(Throwable())
                            }
                        }
                }

                is AuthenticateMethod.Google -> {
                    val oneTapClient = Identity.getSignInClient(context)
                    val signInRequest = BeginSignInRequest.builder()
                        .setPasswordRequestOptions(
                            BeginSignInRequest.PasswordRequestOptions
                                .builder()
                                .setSupported(true)
                                .build()
                        ).setGoogleIdTokenRequestOptions(
                            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId("520904140577-3or0401b7jqn2kcdgpceugbageob4veb.apps.googleusercontent.com")
                                .setFilterByAuthorizedAccounts(true)
                                .build()
                        )
                        .setAutoSelectEnabled(true)
                        .build()


                    oneTapClient.beginSignIn(signInRequest)
                        .addOnCompleteListener {
                            val intent = it.result.pendingIntent
                        }
                        .addOnFailureListener {
                            Log.e("TAG", it.message, it)

                        }
                        .addOnCanceledListener {

                        }

                }

                is AuthenticateMethod.Phone -> {

                }

                is AuthenticateMethod.Facebook -> {

                }

            }
        }
    }

    override fun refreshAccessToken(): Observable<AccessTokenDTO> {
        return Observable.just(AccessTokenDTO("", false))
    }

    override fun saveToken(registerResponse: AccountRegisterResponse) {

    }

    override fun deleteAccountInfo() {
        auth.currentUser?.delete()
    }
}