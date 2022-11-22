package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.repository.FeedRepository
import online.fatbook.fatbookapp.repository.UserRepository

class FeedViewModel : ViewModel() {

    private val repository by lazy { FeedRepository() }

    private val recipes = MutableLiveData<List<RecipeSimpleObject>>()

    fun feed(username: String, pid: Long, callback: ResultCallback<List<RecipeSimpleObject>>) {
        repository.feed(username, pid, object : ResultCallback<List<RecipeSimpleObject>> {
            override fun onResult(value: List<RecipeSimpleObject>?) {
                callback.onResult(value)
            }

            override fun onFailure(value: List<RecipeSimpleObject>?) {
                callback.onFailure(value)
            }
        })
    }
}