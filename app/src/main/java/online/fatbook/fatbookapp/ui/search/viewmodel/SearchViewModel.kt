package online.fatbook.fatbookapp.ui.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.network.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.*
import online.fatbook.fatbookapp.core.user.UserSimpleObject
import online.fatbook.fatbookapp.network.request.RecipeSearchRequest
import online.fatbook.fatbookapp.network.request.UserSearchRequest
import online.fatbook.fatbookapp.ui.search.repository.SearchRepository

class SearchViewModel : ViewModel() {

    companion object {
        private const val TAG = "SearchViewModel"
    }

    private val repository by lazy { SearchRepository() }

    private val _isSearchRecipe = MutableLiveData(true)
    val isSearchRecipe: LiveData<Boolean> get() = _isSearchRecipe

    fun setIsSearchRecipe(value: Boolean) {
        _isSearchRecipe.value = value
    }

    private val _recipeSearchRequest = MutableLiveData<RecipeSearchRequest>()
    val recipeSearchRequest: LiveData<RecipeSearchRequest> get() = _recipeSearchRequest

    fun setRecipeSearchRequest(value: RecipeSearchRequest) {
        _recipeSearchRequest.value = value
    }

    private val _searchRecipes = MutableLiveData<List<RecipeSimpleObject>>()
    val searchRecipes: LiveData<List<RecipeSimpleObject>> get() = _searchRecipes

    fun setSearchRecipes(value: List<RecipeSimpleObject>) {
        _searchRecipes.value = value
    }

    private val _categories = MutableLiveData<ArrayList<CookingCategory>>()
    val categories: LiveData<ArrayList<CookingCategory>> get() = _categories

    fun setCategories(value: ArrayList<CookingCategory>) {
        _categories.value = value
    }

    private val _methods = MutableLiveData<ArrayList<CookingMethod>>()
    val methods: LiveData<ArrayList<CookingMethod>> get() = _methods

    fun setMethods(value: ArrayList<CookingMethod>) {
        _methods.value = value
    }

    private val _difficulties = MutableLiveData<ArrayList<CookingDifficulty>>()
    val difficulties: LiveData<ArrayList<CookingDifficulty>> get() = _difficulties

    fun setDifficulties(value: ArrayList<CookingDifficulty>) {
        _difficulties.value = value
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun setIsLoading(value: Boolean) {
        _isLoading.value = value
    }

    fun searchRecipe(callback: ResultCallback<List<RecipeSimpleObject>>) {
        repository.searchRecipe(
            recipeSearchRequest.value!!,
            object : ResultCallback<List<RecipeSimpleObject>> {
                override fun onResult(value: List<RecipeSimpleObject>?) {
                    callback.onResult(value)
                }

                override fun onFailure(value: List<RecipeSimpleObject>?) {
                    callback.onFailure(value)
                }
            })
    }

    fun searchUser(callback: ResultCallback<List<UserSimpleObject>>) {
        repository.searchUser(
            userSearchRequest.value!!,
            object : ResultCallback<List<UserSimpleObject>> {
                override fun onResult(value: List<UserSimpleObject>?) {
                    callback.onResult(value)
                }

                override fun onFailure(value: List<UserSimpleObject>?) {
                    callback.onFailure(value)
                }
            })
    }

    private val _userSearchRequest = MutableLiveData<UserSearchRequest>()
    val userSearchRequest: LiveData<UserSearchRequest> get() = _userSearchRequest

    fun setUserSearchRequest(value: UserSearchRequest) {
        _userSearchRequest.value = value
    }

    private val _searchUsers = MutableLiveData<List<UserSimpleObject>>()
    val searchUsers: LiveData<List<UserSimpleObject>> get() = _searchUsers

    fun setSearchUsers(value: List<UserSimpleObject>) {
        _searchUsers.value = value
    }

}