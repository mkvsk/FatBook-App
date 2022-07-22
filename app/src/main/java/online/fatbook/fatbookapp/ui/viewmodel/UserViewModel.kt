package online.fatbook.fatbookapp.ui.viewmodel

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.Recipe
import online.fatbook.fatbookapp.core.User
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

    fun getUserByUsername(username: String) {
        repository.getUserByUsername(username, object : ResultCallback<User> {
            override fun onResult(value: User?) {
                value?.let {
                    user.value = it
                }
            }
        })
    }

}