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

    val user = MutableLiveData<User>()

    fun getUserByUsername(username: String, callback: ResultCallback<User>) {
        repository.getUserByUsername(username, object : ResultCallback<User> {
            override fun onResult(value: User?) {
                callback.onResult(value)
            }

            override fun onFailure(value: User?) {
                callback.onFailure(value)
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