package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.network.EditUserRequest
import online.fatbook.fatbookapp.repository.UserRepository

class UserViewModel : ViewModel() {

    private val repository by lazy { UserRepository() }

    val user = MutableLiveData<User>()

    val recipeList = MutableLiveData<ArrayList<Recipe>>()
    val bookmarkedRecipeList = MutableLiveData<ArrayList<Recipe>>()
    val forkedRecipeList = MutableLiveData<ArrayList<Recipe>>()

    val feedRecipeList = MutableLiveData<ArrayList<Recipe>?>()

    val selectedUser = MutableLiveData<User>()
    val selectedUsername = MutableLiveData<String>()

    fun getUserByUsername(username: String, callback: ResultCallback<User>) {
        repository.getUserByUsername(username, object : ResultCallback<User> {
            override fun onResult(value: User?) {
                callback.onResult(value)
            }

            override fun onFailure(value: User?) {
                callback.onResult(value)
            }
        })
    }

    fun updateUser(request: EditUserRequest, callback: ResultCallback<User>) {
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