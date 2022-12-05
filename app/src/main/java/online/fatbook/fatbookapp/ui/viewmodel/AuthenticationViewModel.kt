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

//    private val _reconnectCount = MutableLiveData(1)
//    val reconnectCount: LiveData<Int> get() = _reconnectCount
//
//    fun setReconnectCount() {
//        _reconnectCount.value = _reconnectCount.value?.inc()
//    }

//    private val _isReconnectCancelled = MutableLiveData(false)
//    val isReconnectCancelled: LiveData<Boolean> get() = _isReconnectCancelled
//
//    fun setIsReconnectCancelled(value: Boolean) {
//        _isReconnectCancelled.value = value
//    }

    private val _allowSetNewPass = MutableLiveData(false)
    val allowSetNewPass: LiveData<Boolean> get() = _allowSetNewPass

    fun setAllowSetNewPass(value: Boolean) {
        _allowSetNewPass.value = value
    }

    fun emailCheck(email: String) {
        repository.emailCheck(email, object : ResultCallback<AuthenticationResponse> {
            override fun onResult(value: AuthenticationResponse?) {
                value?.let {
                    when(it.code) {
                        0-> {
                            setUsername(it.username.toString())
                            setVCode(it.vcode.toString())
                            setResultCode(0)
                        }
                        4-> {
                            setError(ContextHolder.get().getString(R.string.dialog_email_used_register_email))
                            setResultCode(4)
                        }
                        else-> {
                            setError(ContextHolder.get().getString(R.string.dialog_register_error))
                            setResultCode(4)
                        }
                    }
                    setIsLoading(false)
                }
            }

            override fun onFailure(value: AuthenticationResponse?) {
                setError(ContextHolder.get().getString(R.string.dialog_register_error))
                setResultCode(-1)
                setIsLoading(false)
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
                    setUsername(it.username!!)
                    setPassword(password)
                    setJwtAccess(it.access_token.toString())
                    setJwtRefresh(it.refresh_token.toString())
                    setIsUserAuthenticated(true)
                    RetrofitFactory.updateJWT(it.access_token!!, it.username)
                }
                setIsLoading(false)
            }

            override fun onFailure(value: LoginResponse?) {
                setError(ContextHolder.get().getString(R.string.dialog_register_error))
                setIsLoading(false)
            }
        })
    }

    fun register(request: AuthenticationRequest) {
        repository.register(request, object : ResultCallback<AuthenticationResponse> {
            override fun onResult(value: AuthenticationResponse?) {
                value?.let {
                    when (it.code) {
                        0 -> {
                            setResultCode(0)
                        }
                        4 -> {
                            setResultCode(4)
                            setError(
                                ContextHolder.get().getString(R.string.dialog_register_email_error)
                            )
                        }
                        5 -> {
                            setResultCode(5)
                            setError(
                                ContextHolder.get()
                                    .getString(R.string.dialog_register_username_unavailable)
                            )
                        }
                        else -> {
                            setResultCode(6)
                            setError(ContextHolder.get().getString(R.string.dialog_register_error))
                        }
                    }
                    setIsLoading(false)
                }
            }

            override fun onFailure(value: AuthenticationResponse?) {
                setResultCode(-1)
                setError(ContextHolder.get().getString(R.string.dialog_register_error))
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
            recoverEmail.value.toString(),
            object : ResultCallback<AuthenticationResponse> {
                override fun onResult(value: AuthenticationResponse?) {
                    setIsLoading(false)
                    value?.let {
                        when (it.code) {
                            0 -> {
                                setAllowSetNewPass(true)
                                setResultCode(0)
                            }
                            1 -> {
                                setError(
                                    ContextHolder.get()
                                        .getString(R.string.dialog_wrong_verification_code_1)
                                )
                                setResultCode(1)
                            }
                            2 -> {
                                setError(
                                    ContextHolder.get()
                                        .getString(R.string.dialog_wrong_verification_code_2_500)
                                )
                                setResultCode(2)
                            }
                            3 -> {
                                setError(
                                    ContextHolder.get()
                                        .getString(R.string.dialog_wrong_verification_code_3)
                                )
                                setResultCode(3)
                            }
                            else -> {
                                setError(
                                    ContextHolder.get()
                                        .getString(R.string.dialog_wrong_verification_code_2_500)
                                )
                                setResultCode(500)
                            }
                        }
                    }
                }

                override fun onFailure(value: AuthenticationResponse?) {
                    setError(ContextHolder.get().getString(R.string.dialog_register_error))
                    setResultCode(-1)
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
                        setResultCode(0)
                        setRecoverIdentifier(identifier)
                        setRecoverEmail(value.email.toString())
                        setRecoverUsername(value.username.toString())
                        setVCode(value.vcode.toString())
                    }
                    6 -> {
                        setResultCode(6)
                        setError(
                            ContextHolder.get()
                                .getString(R.string.dialog_recover_pass_user_not_found)
                        )
                    }
                    else -> {
                        setResultCode(7)
                    }
                }
            }

            override fun onFailure(value: AuthenticationResponse?) {
                setResultCode(-1)
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
                            setResultCode(0)
                            setUsername(recoverUsername.value.toString())
                            setPassword(password)
                        }
                        6 -> {
                            setResultCode(6)
                            setError(
                                ContextHolder.get()
                                    .getString(R.string.dialog_recover_pass_user_not_found)
                            )
                        }
                        else -> {
                            setResultCode(-1)
                            setError(
                                ContextHolder.get().getString(R.string.dialog_connection_error)
                            )
                        }
                    }
                    setIsLoading(false)
                }

                override fun onFailure(value: AuthenticationResponse?) {
                    setResultCode(-1)
                    setError(ContextHolder.get().getString(R.string.dialog_connection_error))
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