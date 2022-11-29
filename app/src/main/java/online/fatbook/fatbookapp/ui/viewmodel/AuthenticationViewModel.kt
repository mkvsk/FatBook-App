package online.fatbook.fatbookapp.ui.viewmodel

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.RequestBody
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.network.AuthenticationRequest
import online.fatbook.fatbookapp.network.AuthenticationResponse
import online.fatbook.fatbookapp.network.LoginResponse
import online.fatbook.fatbookapp.repository.AuthenticationRepository

class AuthenticationViewModel : ViewModel() {

    private val repository by lazy { AuthenticationRepository() }

    var isUserAuthenticated = MutableLiveData(false)

    var userEmail = MutableLiveData("")




    private val _password = MutableLiveData("")
    val password: LiveData<String> get() = _password

    private val _username = MutableLiveData("")
    val username: LiveData<String> get() = _username




    var jwtAccess = MutableLiveData("")
    var jwtRefresh = MutableLiveData("")

    var vCode = MutableLiveData<String?>()

    var currentCountdown = MutableLiveData<Long?>()
    var isTimerRunning = MutableLiveData(false)
    val resendVCTimer = MutableLiveData(10L)

    private var timer: CountDownTimer? = null

    var recoverIdentifier = MutableLiveData("")
    var recoverEmail = MutableLiveData("")
    var recoverUsername = MutableLiveData("")

    fun startTimer(seconds: Long) {
        Log.d(
                "CODE SENT TO", if (userEmail.value.isNullOrEmpty()) {
            recoverIdentifier.value.toString()
        } else {
            userEmail.value.toString()
        }
        )
        timer = object : CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentCountdown.value = millisUntilFinished / 1000
            }

            override fun onFinish() {
                isTimerRunning.value = false
            }
        }.start()
    }

    fun cancelTimer() {
        if (timer != null) {
            timer!!.cancel()
        }
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

    fun login(request: RequestBody, callback: ResultCallback<LoginResponse>) {
        repository.login(request, object : ResultCallback<LoginResponse> {
            override fun onResult(value: LoginResponse?) {
                callback.onResult(value)
            }

            override fun onFailure(value: LoginResponse?) {
                callback.onFailure(value)
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

    fun changePassword(username: String, password: String, callback: ResultCallback<AuthenticationResponse>) {
        repository.changePassword(username, password, object : ResultCallback<AuthenticationResponse> {
            override fun onResult(value: AuthenticationResponse?) {
                callback.onResult(value)
            }

            override fun onFailure(value: AuthenticationResponse?) {
                callback.onFailure(value)
            }
        })
    }

    fun getUsername(): String { return username.value.toString() }

    fun getPassword(): String { return password.value.toString() }
}