package com.kt.apps.xembongda.ui.login

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.kt.apps.xembongda.base.BaseViewModel
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.model.UserDTO
import com.kt.apps.xembongda.model.authenticate.AuthenticateMethod
import com.kt.apps.xembongda.usecase.GetUserInfo
import com.kt.apps.xembongda.usecase.Login
import com.kt.apps.xembongda.utils.toAccessToken
import com.kt.apps.xembongda.utils.toUserDTO
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

data class LoginInteractors @Inject constructor(
    val login: Login,
    val getUserInfo: GetUserInfo
)

fun String.isEmailValid(): Boolean {
    val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(this)
    return matcher.find()
}

fun String.isPasswordValid(): Boolean {
    return this.trim().length > 7
}

val VALID_EMAIL_ADDRESS_REGEX: Pattern =
    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)

class LoginViewModel @Inject constructor(val interactors: LoginInteractors) : BaseViewModel() {

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

    private val _loginDataState by lazy {
        MutableLiveData<DataState<UserDTO>>()
    }

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
            val currentUser = interactors.getUserInfo().blockingFirst()
            _loginDataState.postValue(DataState.Success(currentUser))
        } catch (_: Exception) {}
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
                _loginDataState.postValue(DataState.Success(it.toUserDTO()))
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
                    val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)
                    val rs = task.result
                    rs
                } catch (e: Exception) {
                    Log.e("TAG", e.message, e)
//                    _loginDataState.postValue(DataState.Error(Throwable()))
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
                _loginDataState.postValue(DataState.Success(it))
            }, {
                _loginDataState.postValue(DataState.Error(it))
            })
        )
    }

    fun registerNewUser() {

    }

    fun removeReference() {
        activityResultLauncher?.unregister()
        activityResultLauncher = null
        fragment = null
    }

}