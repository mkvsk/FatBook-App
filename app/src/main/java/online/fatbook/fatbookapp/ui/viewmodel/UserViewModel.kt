package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.core.Recipe
import online.fatbook.fatbookapp.core.User

class UserViewModel : ViewModel() {
    val user = MutableLiveData<User>()
    val recipeList = MutableLiveData<ArrayList<Recipe>>()
    val bookmarkedRecipeList = MutableLiveData<ArrayList<Recipe>>()
    val forkedRecipeList = MutableLiveData<ArrayList<Recipe>>()
    val feedRecipeList = MutableLiveData<ArrayList<Recipe>?>()

}