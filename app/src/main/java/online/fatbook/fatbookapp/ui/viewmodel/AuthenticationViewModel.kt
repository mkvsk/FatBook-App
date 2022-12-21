package online.fatbook.fatbookapp.ui.viewmodel

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.RequestBody
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.network.AuthenticationRequest
import online.fatbook.fatbookapp.network.AuthenticationResponse
import online.fatbook.fatbookapp.network.LoginResponse
import online.fatbook.fatbookapp.network.service.RetrofitFactory
import online.fatbook.fatbookapp.repository.AuthenticationRepository
import online.fatbook.fatbookapp.util.ContextHolder
import online.fatbook.fatbookapp.util.hideKeyboard
import org.intellij.lang.annotations.Identifier

class AuthenticationViewModel : ViewModel() {

    companion object {
        private const val TAG = "AuthenticationViewModel"
    }

    private val repository by lazy { AuthenticationRepository() }

    private val _isUserAuthenticated = MutableLiveData(false)
    val isUserAuthenticated: LiveData<Boolean> get() = _isUserAuthenticated

    fun setIsUserAuthenticated(value: Boolean) {
        _isUserAuthenticated.value = value
    }

    private val _userEmail = MutableLiveData("")
    val userEmail: LiveData<String> get() = _userEmail

    fun setUserEmail(value: String) {
        _userEmail.value = value
    }

    private val _password = MutableLiveData("")
    val password: LiveData<String> get() = _password

    fun setPassword(value: String) {
        _password.value = value
    }

    private val _username = MutableLiveData("")
    val username: LiveData<String> get() = _username

    fun setUsername(value: String) {
        _username.value = value
    }

    private val _jwtAccess = MutableLiveData("")
    val jwtAccess: LiveData<String> get() = _jwtAccess

    fun setJwtAccess(value: String) {
        _jwtAccess.value = value
    }

    private val _jwtRefresh = MutableLiveData("")
    val jwtRefresh: LiveData<String> get() = _jwtRefresh

    fun setJwtRefresh(value: String) {
        _jwtRefresh.value = value
    }

    private val _vCode = MutableLiveData<String?>()
    val vCode: LiveData<String?> get() = _vCode

    fun setVCode(value: String) {
        _vCode.value = value
    }

    private val _recoverIdentifier = MutableLiveData("")
    val recoverIdentifier: LiveData<String> get() = _recoverIdentifier

    fun setRecoverIdentifier(value: String) {
        _recoverIdentifier.value = value
    }

    private val _recoverEmail = MutableLiveData("")
    val recoverEmail: LiveData<String> get() = _recoverEmail

    fun setRecoverEmail(value: String) {
        _recoverEmail.value = value
    }

    private val _recoverUsername = MutableLiveData("")
    val recoverUsername: LiveData<String> get() = _recoverUsername

    fun setRecoverUsername(value: String) {
        _recoverUsername.value = value
    }

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> get() = _error

    fun setError(message: String?) {
        _error.value = message
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun setIsLoading(value: Boolean) {
        _isLoading.value = value
    }

    private val _resultCode = MutableLiveData<Int?>()
    val resultCode: LiveData<Int?> get() = _resultCode

    fun setResultCode(value: Int?) {
        _resultCode.value = value
    }

    private val _codeResent = MutableLiveData(false)
    val codeResent: LiveData<Boolean> get() = _codeResent

    fun setCodeResent(value: Boolean) {
        _codeResent.value = value
    }

    private val _allowSetNewPass = MutableLiveData(false)
    val allowSetNewPass: LiveData<Boolean> get() = _allowSetNewPass

    fun setAllowSetNewPass(value: Boolean) {
        _allowSetNewPass.value = value
    }

    private val _resultCodeEmail = MutableLiveData<Int?>(null)
    val resultCodeEmail: LiveData<Int?> get() = _resultCodeEmail

    fun setResultCodeEmail(value: Int) {
        _resultCodeEmail.value = value
    }

    private val _resultCodeVCode = MutableLiveData<Int?>(null)
    val resultCodeVCode: LiveData<Int?> get() = _resultCodeVCode

    fun setResultCodeVCode(value: Int?) {
        _resultCodeVCode.value = value
    }

    private val _resultCodeRegister = MutableLiveData<Int?>(null)
    val resultCodeRegister: LiveData<Int?> get() = _resultCodeRegister

    fun setResultCodeRegister(value: Int?) {
        _resultCodeRegister.value = value
    }

    private val _resultCodeAuth = MutableLiveData<Int?>(null)
    val resultCodeAuth: LiveData<Int?> get() = _resultCodeAuth

    fun setResultCodeAuth(value: Int?) {
        _resultCodeAuth.value = value
    }

    private val _resultCodeRecoverPass = MutableLiveData<Int?>(null)
    val resultCodeRecoverPass: LiveData<Int?> get() = _resultCodeRecoverPass

    fun setResultCodeRecoverPass(value: Int?) {
        _resultCodeRecoverPass.value = value
    }

    private val _resultCodeChangePass = MutableLiveData<Int?>(null)
    val resultCodeChangePass: LiveData<Int?> get() = _resultCodeChangePass

    fun setResultCodeChangePass(value: Int?) {
        _resultCodeChangePass.value = value
    }

    fun emailCheck(email: String) {
        repository.emailCheck(email, object : ResultCallback<AuthenticationResponse> {
            override fun onResult(value: AuthenticationResponse?) {
                value?.let {
                    if (codeResent.value == true) {
                        setVCode(it.vcode.toString())
                    } else {
                        when (it.code) {
                            0 -> {
                                setUserEmail(it.email.toString())
                                setVCode(it.vcode.toString())
                                setResultCodeEmail(0)
                            }
                            4 -> {
                                setError(
                                    ContextHolder.get()
                                        .getString(R.string.dialog_email_used_register_email)
                                )
                                setResultCodeEmail(4)
                                setIsLoading(false)
                            }
                            else -> {
                                setError(
                                    ContextHolder.get().getString(R.string.dialog_register_error)
                                )
                                setResultCodeEmail(4)
                                setIsLoading(false)
                            }
                        }

                    }
                }
            }

            override fun onFailure(value: AuthenticationResponse?) {
                if (codeResent.value == false) {
                    setError(ContextHolder.get().getString(R.string.dialog_register_error))
                    setResultCodeEmail(-1)
                    setIsLoading(false)
                }
            }
        })
    }

//    fun emailCheck(email: String, callback: ResultCallback<AuthenticationResponse>) {
//        repository.emailCheck(email, object : ResultCallback<AuthenticationResponse> {
//            override fun onResult(value: AuthenticationResponse?) {
//                callback.onResult(value)
//            }
//
//            override fun onFailure(value: AuthenticationResponse?) {
//                callback.onFailure(value)
//            }
//        })
//    }

    fun login(request: RequestBody, password: String) {
        setIsLoading(true)
        repository.login(request, object : ResultCallback<LoginResponse> {
            override fun onResult(value: LoginResponse?) {
                value?.let {
                    setUsername(it.username.toString())
                    setPassword(password)
                    setJwtAccess(it.access_token.toString())
                    setJwtRefresh(it.refresh_token.toString())
                    setIsUserAuthenticated(true)
                    RetrofitFactory.updateJWT(it.access_token.toString(), it.username.toString())
                }
                setResultCodeAuth(1)
            }

            override fun onFailure(value: LoginResponse?) {
                setError(ContextHolder.get().getString(R.string.dialog_register_error))
                setResultCodeAuth(-1)
                setIsUserAuthenticated(false)
                setIsLoading(false)
            }
        })
    }

    fun loginFeed(request: RequestBody) {
        repository.login(request, object : ResultCallback<LoginResponse> {
            override fun onResult(value: LoginResponse?) {
                value?.let {
                    if (it.access_token.isNullOrEmpty()) {
                        setResultCode(0)
                    } else {
                        setJwtAccess(it.access_token.toString())
                        setJwtRefresh(it.refresh_token.toString())
                        RetrofitFactory.updateJWT(it.access_token, it.username!!)
                        setResultCode(1)
                    }
                }
            }

            override fun onFailure(value: LoginResponse?) {
            }
        })
    }

    fun register(request: AuthenticationRequest) {
        repository.register(request, object : ResultCallback<AuthenticationResponse> {
            override fun onResult(value: AuthenticationResponse?) {
                value?.let {
                    when (it.code) {
                        0 -> {
                            setResultCodeRegister(0)
                        }
                        4 -> {
                            setError(
                                ContextHolder.get().getString(R.string.dialog_register_email_error)
                            )
                            setResultCodeRegister(4)
                            setIsLoading(false)
                        }
                        5 -> {
                            setError(
                                ContextHolder.get()
                                    .getString(R.string.dialog_register_username_unavailable)
                            )
                            setResultCodeRegister(5)
                            setIsLoading(false)
                        }
                        else -> {
                            setError(ContextHolder.get().getString(R.string.dialog_register_error))
                            setResultCodeRegister(6)
                            setIsLoading(false)
                        }
                    }
                }
            }

            override fun onFailure(value: AuthenticationResponse?) {
                setError(ContextHolder.get().getString(R.string.dialog_register_error))
                setResultCodeRegister(-1)
                setIsLoading(false)
            }
        })
    }

//    fun confirmVCode(vCode: String) {
//        repository.confirmVCode(vCode, recoverEmail.toString(), object : ResultCallback<AuthenticationResponse> {
//            override fun onResult(value: AuthenticationResponse?) {
//                callback.onResult(value)
//            }
//
//            override fun onFailure(value: AuthenticationResponse?) {
//            }
//
//        })
//    }

    /*
        * resultCode 0 - V_CODE_CONFIRMED
        * resultCode 1 - V_CODE_ALREADY_CONFIRMED
        * resultCode 2 - V_CODE_NOT_FOUND
        * resultCode 3 - V_CODE_EXPIRED
        * resultCode 500 - V_CODE_UNKNOWN
        * */
    fun confirmVCode(vCode: String) {
        repository.confirmVCode(
            vCode,
            userEmail.value.toString(),
            object : ResultCallback<AuthenticationResponse> {
                override fun onResult(value: AuthenticationResponse?) {
                    setIsLoading(false)
                    value?.let {
                        when (it.code) {
                            0 -> {
                                setAllowSetNewPass(true)
                                setResultCodeVCode(0)
                            }
                            1 -> {
                                setError(
                                    ContextHolder.get()
                                        .getString(R.string.dialog_wrong_verification_code_1)
                                )
                                setResultCodeVCode(1)
                            }
                            2 -> {
                                setError(
                                    ContextHolder.get()
                                        .getString(R.string.dialog_wrong_verification_code_2_500)
                                )
                                setResultCodeVCode(2)
                            }
                            3 -> {
                                setError(
                                    ContextHolder.get()
                                        .getString(R.string.dialog_wrong_verification_code_3)
                                )
                                setResultCodeVCode(3)
                            }
                            else -> {
                                setError(
                                    ContextHolder.get()
                                        .getString(R.string.dialog_wrong_verification_code_2_500)
                                )
                                setResultCodeVCode(500)
                            }
                        }
                        Log.d(TAG, "onResult: ${it.code}")
                    }
                }

                override fun onFailure(value: AuthenticationResponse?) {
                    setError(ContextHolder.get().getString(R.string.dialog_register_error))
                    setResultCodeVCode(-1)
                    setIsLoading(false)
                }
            })
    }

    fun recoverPassword(identifier: String) {
        repository.recoverPassword(identifier, object : ResultCallback<AuthenticationResponse> {
            override fun onResult(value: AuthenticationResponse?) {
                setIsLoading(false)
                when (value?.code) {
                    0 -> {
                        setResultCodeRecoverPass(0)
                        setRecoverIdentifier(identifier)
                        setRecoverEmail(value.email.toString())
                        setRecoverUsername(value.username.toString())
                        setVCode(value.vcode.toString())
                    }
                    6 -> {
                        setResultCodeRecoverPass(6)
                        setError(
                            ContextHolder.get()
                                .getString(R.string.dialog_recover_pass_user_not_found)
                        )
                    }
                    else -> {
                        setResultCodeRecoverPass(7)
                    }
                }
            }

            override fun onFailure(value: AuthenticationResponse?) {
                setResultCodeRecoverPass(-1)
                setError(ContextHolder.get().getString(R.string.dialog_register_error))
                setIsLoading(false)
            }
        })
    }

//    fun recoverPassword(identifier: String) {
//        repository.recoverPassword(identifier, object : ResultCallback<AuthenticationResponse> {
//
//            override fun onResult(value: AuthenticationResponse?) {
//                setVCode(value?.vcode.toString())
//                Log.d("CODE ================= ", value?.vcode.toString())
//            }
//
//            override fun onFailure(value: AuthenticationResponse?) {
//            }
//        })
//    }

    fun changePassword(password: String) {
        repository.changePassword(
            recoverUsername.value.toString(),
            password,
            object : ResultCallback<AuthenticationResponse> {
                override fun onResult(value: AuthenticationResponse?) {
                    when (value?.code) {
                        0 -> {
                            setUsername(recoverUsername.value.toString())
                            setPassword(password)
                            setResultCodeChangePass(0)
                            setIsLoading(false)
                        }
                        6 -> {
                            setError(
                                ContextHolder.get()
                                    .getString(R.string.dialog_recover_pass_user_not_found)
                            )
                            setResultCodeChangePass(6)
                            setIsLoading(false)
                        }
                        else -> {
                            setError(
                                ContextHolder.get().getString(R.string.dialog_connection_error)
                            )
                            setResultCodeChangePass(-1)
                            setIsLoading(false)
                        }
                    }
                }

                override fun onFailure(value: AuthenticationResponse?) {
                    setError(ContextHolder.get().getString(R.string.dialog_connection_error))
                    setResultCodeChangePass(-1)
                    setIsLoading(false)
                }
            })
    }

//    fun changePassword(
//        username: String,
//        password: String,
//        callback: ResultCallback<AuthenticationResponse>
//    ) {
//        repository.changePassword(
//            username,
//            password,
//            object : ResultCallback<AuthenticationResponse> {
//                override fun onResult(value: AuthenticationResponse?) {
//                    callback.onResult(value)
//                }
//
//                override fun onFailure(value: AuthenticationResponse?) {
//                    callback.onFailure(value)
//                }
//            })
//    }

}