package online.fatbook.fatbookapp.ui.viewmodel

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.RequestBody
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.authentication.AuthenticationRequest
import online.fatbook.fatbookapp.core.authentication.AuthenticationResponse
import online.fatbook.fatbookapp.core.LoginResponse
import online.fatbook.fatbookapp.repository.AuthenticationRepository
import online.fatbook.fatbookapp.util.ContextHolder

class AuthenticationViewModel : ViewModel() {

    private val repository by lazy { AuthenticationRepository(ContextHolder.get()) }

    var userEmail = MutableLiveData("")
    var password = MutableLiveData("")
    var username = MutableLiveData("")

    var jwtAccess = MutableLiveData("")
    var jwtRefresh = MutableLiveData("")

    var vCode = MutableLiveData<String?>()

    var usernameAvailable = MutableLiveData<Boolean?>()

    var currentCountdown = MutableLiveData<Long?>()
    var isTimerRunning = MutableLiveData(false)
    val resendVCTimer = MutableLiveData(10L)

    private var timer: CountDownTimer? = null

    fun usernameCheck(username: String) {
        repository.usernameCheck(username, object : ResultCallback<Boolean> {
            override fun onResult(value: Boolean?) {
                value?.let {
                    usernameAvailable.value = it
                }
            }

            override fun onFailure(value: Boolean?) {
            }
        })
    }

    fun startTimer(seconds: Long) {
        Log.d("CODE SENT TO: ", userEmail.value.toString())
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
                value?.let {
                    callback.onResult(it)
                }
            }

            override fun onFailure(value: AuthenticationResponse?) {
                value?.let {
                    callback.onFailure(it)
                }
            }
        })
    }

    fun login(request: RequestBody, callback: ResultCallback<LoginResponse>) {
        repository.login(request, object : ResultCallback<LoginResponse> {
            override fun onResult(value: LoginResponse?) {
                value?.let {
                    callback.onResult(it)
                }
            }

            override fun onFailure(value: LoginResponse?) {
            }
        })
    }

    fun register(request: AuthenticationRequest, callback: ResultCallback<AuthenticationResponse>) {
        repository.register(request, object : ResultCallback<AuthenticationResponse> {
            override fun onResult(value: AuthenticationResponse?) {
                value?.let {
                    callback.onResult(it)
                }
            }

            override fun onFailure(value: AuthenticationResponse?) {
            }
        })
    }

    fun confirmVCode(vCode: String, email: String, callback: ResultCallback<AuthenticationResponse>) {
        repository.confirmVCode(vCode, email, object : ResultCallback<AuthenticationResponse> {
            override fun onResult(value: AuthenticationResponse?) {
                value?.let {
                    callback.onResult(it)
                }
            }

            override fun onFailure(value: AuthenticationResponse?) {
                value?.let {
                    callback.onFailure(it)
                }
            }
        })
    }
}