package online.fatbook.fatbookapp.ui.recipe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.network.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.core.recipe.RecipeComment
import online.fatbook.fatbookapp.ui.recipe.repository.RecipeRepository

class RecipeViewViewModel : ViewModel() {

    companion object {
        private const val TAG = "RecipeViewViewModel"
    }

    private val repository by lazy { RecipeRepository() }

    private val _recipe = MutableLiveData<Recipe?>()
    val recipe: LiveData<Recipe?> get() = _recipe

    fun setRecipe(value: Recipe) {
        _recipe.value = value
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun setIsLoading(value: Boolean) {
        _isLoading.value = value
    }

    private val _selectedRecipe = MutableLiveData<Recipe?>()
    val selectedRecipe: LiveData<Recipe?> get() = _selectedRecipe

    fun setSelectedRecipe(value: Recipe?) {
        _selectedRecipe.value = value
    }

    private val _selectedRecipeId = MutableLiveData<Long>()
    val selectedRecipeId: LiveData<Long> get() = _selectedRecipeId

    fun setSelectedRecipeId(value: Long) {
        _selectedRecipeId.value = value
    }

    fun recipeCreate(recipe: Recipe, callback: ResultCallback<Boolean>) {
        repository.recipeCreate(recipe, object : ResultCallback<Boolean> {
            override fun onResult(value: Boolean?) {
                callback.onResult(value)
            }

            override fun onFailure(value: Boolean?) {
                callback.onFailure(value)
            }
        })
    }

    fun getRecipeById(id: Long, callback: ResultCallback<Recipe>) {
        repository.getRecipeById(id, object : ResultCallback<Recipe> {
            override fun onResult(value: Recipe?) {
                callback.onResult(value)
            }

            override fun onFailure(value: Recipe?) {
                callback.onFailure(value)
            }
        })
    }

    fun addComment(
        pidRecipe: Long,
        comment: String,
        callback: ResultCallback<List<RecipeComment>>
    ) {
        repository.addComment(pidRecipe, comment, object : ResultCallback<List<RecipeComment>> {
            override fun onResult(value: List<RecipeComment>?) {
                callback.onResult(value)
            }

            override fun onFailure(value: List<RecipeComment>?) {
                callback.onFailure(value)
            }
        })
    }

    fun recipeFork(pidRecipe: Long, fork: Boolean, callback: ResultCallback<Int>) {
        repository.recipeFork(pidRecipe, fork, object : ResultCallback<Int> {
            override fun onResult(value: Int?) {
                callback.onResult(value)
            }

            override fun onFailure(value: Int?) {
                callback.onFailure(value)
            }
        })
    }
}