package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.network.UserUpdateRequest
import online.fatbook.fatbookapp.repository.UserRepository

class UserViewModel : ViewModel() {

    companion object {
        private const val TAG = "UserViewModel"
    }

    private val repository by lazy { UserRepository() }

    private val _selectedUsername = MutableLiveData("")
    val selectedUsername: LiveData<String> = _selectedUsername

    fun setSelectedUsername(value: String) {
        _selectedUsername.value = value
    }

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    fun setUser(value: User) {
        _user.value = value
    }

    private val _resultCode = MutableLiveData<Int?>()
    val resultCode: LiveData<Int?> get() = _resultCode

    fun setResultCode(value: Int?) {
        _resultCode.value = value
    }

    fun loadCurrentUser(username: String, callback: ResultCallback<User>) {
        repository.getUserByUsername(username, object : ResultCallback<User> {
            override fun onResult(value: User?) {
                setUser(value!!)
                callback.onResult(value)
            }

            override fun onFailure(value: User?) {
            }
        })
    }

    fun getUserByUsername(username: String, callback: ResultCallback<User>) {
        repository.getUserByUsername(username, object : ResultCallback<User> {
            override fun onResult(value: User?) {
                callback.onResult(value)
            }

            override fun onFailure(value: User?) {
            }
        })
    }

    fun updateUser(request: UserUpdateRequest, callback: ResultCallback<User>) {
        repository.updateUser(request, object : ResultCallback<User> {
            override fun onResult(value: User?) {
                callback.onResult(value)
            }

            override fun onFailure(value: User?) {
                callback.onFailure(value)
            }

        })
    }

}