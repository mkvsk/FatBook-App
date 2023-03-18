package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.RequestBody
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.*
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.repository.RecipeRepository
import java.io.File

class RecipeEditViewModel : ViewModel() {

    companion object {
        private const val TAG = "RecipeEditViewModel"
    }

    private val repository by lazy { RecipeRepository() }

    private val _isEditMode = MutableLiveData<Boolean>(false)
    val isEditMode: LiveData<Boolean> get() = _isEditMode

    fun setEditMode(value: Boolean) {
        _isEditMode.value = value
    }

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

    private val _recipeStepImages = MutableLiveData<HashMap<Int, Pair<String, RequestBody?>>>()
    val recipeStepImages: LiveData<HashMap<Int, Pair<String, RequestBody?>>> get() = _recipeStepImages

    fun setRecipeStepImages(value: HashMap<Int, Pair<String, RequestBody?>>) {
        _recipeStepImages.value = value
    }

    private val _recipeImage = MutableLiveData<File?>()
    val recipeImage: LiveData<File?> get() = _recipeImage

    fun setRecipeImage(value: File?) {
        _recipeImage.value = value
    }

    private val _recipeCookingMethod = MutableLiveData<CookingMethod?>()
    val recipeCookingMethod: LiveData<CookingMethod?> get() = _recipeCookingMethod

    fun setRecipeCookingMethod(value: CookingMethod?) {
        _recipeCookingMethod.value = value
    }

    private val _recipeCookingCategories = MutableLiveData<ArrayList<CookingCategory>?>()
    val recipeCookingCategories: LiveData<ArrayList<CookingCategory>?> get() = _recipeCookingCategories

    fun setRecipeCookingCategories(value: ArrayList<CookingCategory>?) {
        _recipeCookingCategories.value = value
    }

    private val _recipeCookingTimeHours = MutableLiveData<Int?>()
    val recipeCookingTimeHours: LiveData<Int?> get() = _recipeCookingTimeHours

    fun setRecipeCookingTimeHours(value: Int?) {
        _recipeCookingTimeHours.value = value
    }

    private val _recipeCookingTimeMinutes = MutableLiveData<Int?>()
    val recipeCookingTimeMinutes: LiveData<Int?> get() = _recipeCookingTimeMinutes

    fun setRecipeCookingTimeMinutes(value: Int?) {
        _recipeCookingTimeMinutes.value = value
    }

    private val _recipeIngredients = MutableLiveData<ArrayList<RecipeIngredient?>>()
    val recipeIngredients: LiveData<ArrayList<RecipeIngredient?>> get() = _recipeIngredients

    fun setRecipeIngredients(value: ArrayList<RecipeIngredient?>) {
        _recipeIngredients.value = value
    }

    private val _recipeAddIngredient = MutableLiveData<Ingredient?>()
    val recipeAddIngredient: LiveData<Ingredient?> get() = _recipeAddIngredient

    fun setRecipeAddIngredient(value: Ingredient?) {
        _recipeAddIngredient.value = value
    }

    private val _recipeAddRecipeIngredient = MutableLiveData<RecipeIngredient?>()
    val recipeAddRecipeIngredient: LiveData<RecipeIngredient?> get() = _recipeAddRecipeIngredient

    fun setRecipeAddRecipeIngredient(value: RecipeIngredient?) {
        _recipeAddRecipeIngredient.value = value
    }

    private val _selectedCookingStep = MutableLiveData<CookingStep?>()
    val selectedCookingStep: LiveData<CookingStep?> get() = _selectedCookingStep

    fun setSelectedCookingStep(value: CookingStep?) {
        _selectedCookingStep.value = value
    }

    private val _selectedCookingStepPosition = MutableLiveData<Int?>()
    val selectedCookingStepPosition: LiveData<Int?> get() = _selectedCookingStepPosition

    fun setSelectedCookingStepPosition(value: Int?) {
        _selectedCookingStepPosition.value = value
    }

    private val _isRecipeEditFinished = MutableLiveData<Boolean?>()
    val isRecipeEditFinished: LiveData<Boolean?> get() = _isRecipeEditFinished

    fun setIsRecipeEditFinishedCreated(value: Boolean?) {
        _isRecipeEditFinished.value = value
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
}