package com.kt.apps.xembongda.ui.login

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kt.apps.xembongda.App
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.BaseViewModel
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.model.UserDTO
import com.kt.apps.xembongda.usecase.authenticate.GetAccessToken
import com.kt.apps.xembongda.usecase.authenticate.GetUserInfo
import com.kt.apps.xembongda.usecase.authenticate.Login
import com.kt.apps.xembongda.usecase.authenticate.RegisterNewUser
import com.kt.apps.xembongda.utils.firebase.toUserDto
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

data class LoginInteractors @Inject constructor(
    val login: Login,
    val getUserInfo: GetUserInfo,
    val getAccessToken: GetAccessToken,
    val registerNewUser: RegisterNewUser
)

fun String.isEmailValid(): Boolean {
    val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(this)
    return matcher.find()
}

fun String.isPasswordValid(): Boolean {
    if (this.contains(" ")) return false
    return this.trim().replace(" ", "").length > 7
}

val VALID_EMAIL_ADDRESS_REGEX: Pattern =
    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)

class LoginViewModel @Inject constructor(
    val interactors: LoginInteractors,
) : BaseViewModel() {

    val userName by lazy { MutableLiveData<String>() }
    val password by lazy { MutableLiveData<String>() }
    val retypePassword by lazy { MutableLiveData<String>() }
    val isRegisterMode = ObservableBoolean(false)
    val loginEnable by lazy {
        MediatorLiveData<Boolean>()
    }
    val registerEnable by lazy {
        MediatorLiveData<Boolean>()
    }
    val emailInvalid = Transformations.map(userName) {
        if (it.isEmpty()) {
            null
        } else {
            it.isEmailValid()
        }
    }

    val passwordInvalid = Transformations.map(password) {
        if (it.isEmpty()) {
            null
        } else {
            it.isPasswordValid()
        }
    }

    val retypePasswordInvalid = Transformations.map(retypePassword) {
        if (it.trim().replace(" ", "").isEmpty()) {
            null
        } else {
            passwordInvalid.value == true && it == password.value
        }
    }


    private val _loginDataState by lazy {
        MutableLiveData<DataState<UserDTO>>()
    }
    var isNeedReLogin: Boolean

    init {
        registerEnable.value = false
        loginEnable.addSource(userName) {
            loginEnable.value = checkUserPasswordValid()
        }
        loginEnable.addSource(password) {
            loginEnable.value = checkUserPasswordValid()
        }

        registerEnable.addSource(userName) {
            registerEnable.value = checkRegisterValid()
        }
        registerEnable.addSource(password) {
            registerEnable.value = checkRegisterValid()
        }
        registerEnable.addSource(retypePassword) {
            registerEnable.value = checkRegisterValid()
        }
        try {
            val token = interactors.getAccessToken().blockingFirst()
            if (!token.isNeedReLogin) {
                isNeedReLogin = false
                val currentUser = interactors.getUserInfo().blockingFirst()
                _loginDataState.postValue(DataState.Success(currentUser))
            } else {
                isNeedReLogin = true
            }
        } catch (e: Exception) {
            isNeedReLogin = true
        }
    }

    private fun checkUserPasswordValid(): Boolean {
        val user = userName.value ?: return false
        val pass = password.value ?: return false
        Log.e("TAG", "${user.isEmailValid() && pass.isPasswordValid()}")
        return (user.isEmailValid() && pass.isPasswordValid())
    }

    private fun checkRegisterValid(): Boolean {
        val user = userName.value ?: return false
        val pass = password.value ?: return false
        val retype = retypePassword.value ?: return false
        Log.e("TAG", "${(user.isEmailValid() && pass.isPasswordValid()) && pass == retype}")
        return (user.isEmailValid() && pass.isPasswordValid()) && pass == retype
    }

    private val _googleSignIn by lazy {
        MutableLiveData<DataState<GoogleSignInAccount>>()
    }

    private var activityResultLauncher: ActivityResultLauncher<GoogleSignInClient>? = null
    private var fragment: Fragment? = null
    fun setup(fragment: Fragment) {
        this.fragment = fragment
        activityResultLauncher = fragment.registerForActivityResult(
            activityResultContract,
            loginGoogleCallback
        )
    }

    private val loginGoogleCallback by lazy {
        ActivityResultCallback<GoogleSignInAccount?> {
            it?.let {
                val firebaseCredential = GoogleAuthProvider.getCredential(it.idToken, null)
                Firebase.auth.signInWithCredential(firebaseCredential)
                    .addOnSuccessListener {
                        isNeedReLogin = false
                        _loginDataState.postValue(DataState.Success(it.user.toUserDto()))
                    }
                    .addOnCompleteListener {
                    }
                    .addOnFailureListener {
                        _loginDataState.postValue(DataState.Error(it))
                        _loginDataState.postValue(DataState.Loading())
                    }
            }
        }
    }

    private val activityResultContract by lazy {
        object : ActivityResultContract<GoogleSignInClient, GoogleSignInAccount?>() {
            override fun createIntent(context: Context, input: GoogleSignInClient): Intent {
                return input.signInIntent
            }

            override fun parseResult(resultCode: Int, intent: Intent?): GoogleSignInAccount? {
                return try {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(intent)
                    val rs = task.result
                    rs
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    fun loginByGoogle() {
        _loginDataState.postValue(DataState.Loading())
        val activity = fragment?.requireActivity() ?: return
        _googleSignIn.postValue(DataState.Loading())
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestId()
            .requestIdToken(App.get().getString(R.string.google_client_server_authen))
            .requestProfile()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(activity, gso)
        activityResultLauncher?.launch(googleSignInClient)

    }

    fun setAccount(account: GoogleSignInAccount?) {

    }

    val loginDataState: LiveData<DataState<UserDTO>>
        get() = _loginDataState

    fun loginUserPassword() {
        val userName = userName.value ?: return
        val password = password.value ?: return
        _loginDataState.postValue(DataState.Loading())
        add(
            interactors.login(userName, password).subscribe({
                isNeedReLogin = false
                _loginDataState.postValue(DataState.Success(it))
            }, {
                _loginDataState.postValue(DataState.Error(it))
            })
        )
    }

    fun registerNewUser() {
        val userName = userName.value ?: return
        val password = password.value ?: return
        _loginDataState.postValue(DataState.Loading())
        add(
            interactors.registerNewUser(userName, password)
                .subscribe({
                    isNeedReLogin = false
                    _loginDataState.postValue(DataState.Success(it))
                }, {
                    _loginDataState.postValue(DataState.Error(it))
                })
        )

    }

    fun removeReference() {
        _loginDataState.postValue(DataState.None())
        activityResultLauncher?.unregister()
        activityResultLauncher = null
        fragment = null
    }

}