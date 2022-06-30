package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.core.User

class SignInViewModel : ViewModel() {
    val user = MutableLiveData<User?>()
    val isFatValid = MutableLiveData<Boolean>()
    val isLoginAvailable = MutableLiveData<Boolean?>()
}