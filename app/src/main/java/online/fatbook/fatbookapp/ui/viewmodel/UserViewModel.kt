package online.fatbook.fatbookapp.ui.viewmodel

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.repository.UserRepository
import online.fatbook.fatbookapp.util.ContextHolder

class UserViewModel : ViewModel() {

    private val repository by lazy { UserRepository(ContextHolder.get()) }
    private val handler by lazy { Handler() }

    val user = MutableLiveData<User>()
    val recipeList = MutableLiveData<ArrayList<Recipe>>()
    val bookmarkedRecipeList = MutableLiveData<ArrayList<Recipe>>()
    val forkedRecipeList = MutableLiveData<ArrayList<Recipe>>()
    val feedRecipeList = MutableLiveData<ArrayList<Recipe>?>()
    val test = MutableLiveData<String>()
    val selectedUser = MutableLiveData<User>()
    val selectedUsername = MutableLiveData<String>()

    fun getUserByUsername(username: String, callback: ResultCallback<User>) {
        repository.getUserByUsername(username, object : ResultCallback<User> {
            override fun onResult(value: User?) {
                value?.let {
                    selectedUser.value = it
                    callback.onResult(it)
                }
            }

            override fun onFailure(value: User?) {

            }
        })
    }


}