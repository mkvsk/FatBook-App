package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDateTime

class SignupViewModel : ViewModel() {
    var email = MutableLiveData<String?>()
    var password = MutableLiveData<String?>()
    var username = MutableLiveData<String?>()

    var isVCSend = MutableLiveData(false)
    var VCCode = MutableLiveData<String?>()
    var VCResendTimestamp = MutableLiveData<LocalDateTime?>()
    var timestampSet = MutableLiveData(false)

}