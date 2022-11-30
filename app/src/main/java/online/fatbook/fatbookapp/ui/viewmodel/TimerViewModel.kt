package online.fatbook.fatbookapp.ui.viewmodel

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel : ViewModel() {

    companion object {
        private const val TAG = "TimerViewModel"
    }

    private val _currentCountdown = MutableLiveData<Long?>()
    val currentCountdown: LiveData<Long?> get() = _currentCountdown

    fun setCurrentCountdown(value: Long) {
        _currentCountdown.value = value
    }

    private val _isTimerRunning = MutableLiveData(false)
    val isTimerRunning: LiveData<Boolean> get() = _isTimerRunning

    fun setIsTimerRunning(value: Boolean) {
        _isTimerRunning.value = value
    }

    private val _resendVCTimer = MutableLiveData(10L)
    val resendVCTimer: LiveData<Long> get() = _resendVCTimer

    fun setResendVCTimer(value: Long) {
        _resendVCTimer.value = value
    }

    private var timer: CountDownTimer? = null

    fun startTimer(seconds: Long) {

        timer = object : CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                setCurrentCountdown(millisUntilFinished / 1000)
            }

            override fun onFinish() {
                setIsTimerRunning(false)
            }
        }.start()
    }

    fun cancelTimer() {
        if (timer != null) {
            timer!!.cancel()
        }
    }
}