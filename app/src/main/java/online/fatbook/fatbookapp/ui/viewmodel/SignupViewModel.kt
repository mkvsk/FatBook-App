package online.fatbook.fatbookapp.ui.viewmodel

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.repository.AuthenticationRepository
import online.fatbook.fatbookapp.util.ContextHolder

class SignupViewModel : ViewModel() {

    private val repository by lazy { AuthenticationRepository(ContextHolder.get()) }

    var email = MutableLiveData("")
    var password = MutableLiveData<String?>()
    var username = MutableLiveData<String?>()

    var vCode = MutableLiveData<String?>()

    var usernameAvailable = MutableLiveData<Boolean?>()

    var currentCountdown = MutableLiveData<Long?>()
    var timestampSet = MutableLiveData(false)
    val resendVCTimer = MutableLiveData(20L)

    private var timer: CountDownTimer? = null

    fun usernameCheck(username: String) {
        repository.usernameCheck(username, object : ResultCallback<Boolean> {
            override fun onResult(value: Boolean?) {
                value?.let {
                    usernameAvailable.value = it
                }
            }
        })
    }

    fun startTimer(seconds: Long) {
        Log.d("CODE SENT TO: ", email.value.toString())
        timer = object : CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentCountdown.value = millisUntilFinished / 1000
            }

            override fun onFinish() {
                timestampSet.value = false
            }
        }.start()
    }

    fun cancelTimer() {
        if (timer != null) {
            timer!!.cancel()
        }
    }
}