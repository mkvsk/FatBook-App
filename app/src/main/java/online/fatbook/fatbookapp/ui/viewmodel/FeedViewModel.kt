package online.fatbook.fatbookapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.repository.FeedRepository
import online.fatbook.fatbookapp.repository.UserRepository

class FeedViewModel : ViewModel() {

    companion object {
        private const val TAG = "FeedViewModel"
    }

    private val repository by lazy { FeedRepository() }

    private val _recipes = MutableLiveData<List<RecipeSimpleObject>>()
    val recipes: LiveData<List<RecipeSimpleObject>> get() = _recipes

    fun setRecipes(list: List<RecipeSimpleObject>) {
        _recipes.value = list
    }

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun setIsLoading(value: Boolean) {
        _isLoading.value = value
    }

    fun feed(pid: Long, callback: ResultCallback<List<RecipeSimpleObject>>) {
        repository.feed(pid, object : ResultCallback<List<RecipeSimpleObject>> {
            override fun onResult(value: List<RecipeSimpleObject>?) {
                callback.onResult(value)
            }

            override fun onFailure(value: List<RecipeSimpleObject>?) {
                callback.onFailure(value)
            }
        })
    }

}