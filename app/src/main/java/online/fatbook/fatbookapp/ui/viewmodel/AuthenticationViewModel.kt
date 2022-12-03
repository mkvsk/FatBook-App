package online.fatbook.fatbookapp.ui.viewmodel

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

    fun emailCheck(email: String, callback: ResultCallback<AuthenticationResponse>) {
        repository.emailCheck(email, object : ResultCallback<AuthenticationResponse> {
            override fun onResult(value: AuthenticationResponse?) {
                callback.onResult(value)
            }

            override fun onFailure(value: AuthenticationResponse?) {
                callback.onFailure(value)
            }
        })
    }

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

    fun register(request: AuthenticationRequest, callback: ResultCallback<AuthenticationResponse>) {
        repository.register(request, object : ResultCallback<AuthenticationResponse> {
            override fun onResult(value: AuthenticationResponse?) {
                callback.onResult(value)
            }

            override fun onFailure(value: AuthenticationResponse?) {
                callback.onFailure(value)
            }
        })
    }

    fun confirmVCode(
        vCode: String, email: String, callback: ResultCallback<AuthenticationResponse>
    ) {
        repository.confirmVCode(vCode, email, object : ResultCallback<AuthenticationResponse> {
            override fun onResult(value: AuthenticationResponse?) {
                callback.onResult(value)
            }

            override fun onFailure(value: AuthenticationResponse?) {
                callback.onFailure(value)
            }
        })
    }

    fun recoverPassword(identifier: String, callback: ResultCallback<AuthenticationResponse>) {
        repository.recoverPassword(identifier, object : ResultCallback<AuthenticationResponse> {
            override fun onResult(value: AuthenticationResponse?) {
                callback.onResult(value)
            }

            override fun onFailure(value: AuthenticationResponse?) {
                callback.onFailure(value)
            }
        })
    }

    fun changePassword(
        username: String,
        password: String,
        callback: ResultCallback<AuthenticationResponse>
    ) {
        repository.changePassword(
            username,
            password,
            object : ResultCallback<AuthenticationResponse> {
                override fun onResult(value: AuthenticationResponse?) {
                    callback.onResult(value)
                }

                override fun onFailure(value: AuthenticationResponse?) {
                    callback.onFailure(value)
                }
            })
    }

}