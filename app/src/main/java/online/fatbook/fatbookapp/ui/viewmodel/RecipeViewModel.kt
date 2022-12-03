package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.*
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.repository.RecipeRepository
import java.io.File

class RecipeViewModel : ViewModel() {

    companion object {
        private const val TAG = "RecipeViewModel"
    }

    private val repository by lazy { RecipeRepository() }

    private val _recipe = MutableLiveData<Recipe?>()
    val recipe: LiveData<Recipe?> get() = _recipe

    fun setRecipe(value: Recipe) {
        _recipe.value = value
    }

    private val _selectedRecipeIngredients = MutableLiveData<MutableList<RecipeIngredient?>>()
    val selectedRecipeIngredients: LiveData<MutableList<RecipeIngredient?>> get() = _selectedRecipeIngredients

    fun setSelectedRecipeIngredients(value: MutableList<RecipeIngredient?>) {
        _selectedRecipeIngredients.value = value
    }

    private val _selectedRecipeIngredient = MutableLiveData<RecipeIngredient>()
    val selectedRecipeIngredient: LiveData<RecipeIngredient> get() = _selectedRecipeIngredient

    fun setSelectedRecipeIngredient(value: RecipeIngredient) {
        _selectedRecipeIngredient.value = value
    }

    private val _targetRecipe = MutableLiveData<Recipe>()
    val targetRecipe: LiveData<Recipe> get() = _targetRecipe

    fun setTargetRecipe(value: Recipe) {
        _targetRecipe.value = value
    }

    private val _selectedRecipePosition = MutableLiveData<Int?>()
    val selectedRecipePosition: LiveData<Int?> get() = _selectedRecipePosition

    fun setSelectedRecipePosition(value: Int?) {
        _selectedRecipePosition.value = value
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

    private val _newRecipe = MutableLiveData<Recipe?>()
    val newRecipe: LiveData<Recipe?> get() = _newRecipe

    fun setNewRecipe(value: Recipe?) {
        _newRecipe.value = value
    }

    private val _newRecipeCookingDifficulty = MutableLiveData<CookingDifficulty?>()
    val newRecipeCookingDifficulty: LiveData<CookingDifficulty?> get() = _newRecipeCookingDifficulty

    fun setNewRecipeCookingDifficulty(value: CookingDifficulty?) {
        _newRecipeCookingDifficulty.value = value
    }

    private val _newRecipeCookingTimeHours = MutableLiveData<Int?>()
    val newRecipeCookingTimeHours: LiveData<Int?> get() = _newRecipeCookingTimeHours

    fun setNewRecipeCookingTimeHours(value: Int?) {
        _newRecipeCookingTimeHours.value = value
    }

    private val _newRecipeCookingTimeMinutes = MutableLiveData<Int?>()
    val newRecipeCookingTimeMinutes: LiveData<Int?> get() = _newRecipeCookingTimeMinutes

    fun setNewRecipeCookingTimeMinutes(value: Int?) {
        _newRecipeCookingTimeMinutes.value = value
    }

    private val _newRecipeCookingMethod = MutableLiveData<CookingMethod?>()
    val newRecipeCookingMethod: LiveData<CookingMethod?> get() = _newRecipeCookingMethod

    fun setNewRecipeCookingMethod(value: CookingMethod?) {
        _newRecipeCookingMethod.value = value
    }

    private val _newRecipeCookingCategories = MutableLiveData<ArrayList<CookingCategory>?>()
    val newRecipeCookingCategories: LiveData<ArrayList<CookingCategory>?> get() = _newRecipeCookingCategories

    fun setNewRecipeCookingCategories(value: ArrayList<CookingCategory>?) {
        _newRecipeCookingCategories.value = value
    }

    private val _newRecipeIngredients = MutableLiveData<ArrayList<RecipeIngredient?>>()
    val newRecipeIngredients: LiveData<ArrayList<RecipeIngredient?>> get() = _newRecipeIngredients

    fun setNewRecipeIngredients(value: ArrayList<RecipeIngredient?>) {
        _newRecipeIngredients.value = value
    }

    private val _newRecipeAddIngredient = MutableLiveData<Ingredient?>()
    val newRecipeAddIngredient: LiveData<Ingredient?> get() = _newRecipeAddIngredient

    fun setNewRecipeAddIngredient(value: Ingredient?) {
        _newRecipeAddIngredient.value = value
    }

    private val _newRecipeAddRecipeIngredient = MutableLiveData<RecipeIngredient?>()
    val newRecipeAddRecipeIngredient: LiveData<RecipeIngredient?> get() = _newRecipeAddRecipeIngredient

    fun setNewRecipeAddRecipeIngredient(value: RecipeIngredient?) {
        _newRecipeAddRecipeIngredient.value = value
    }

    private val _newRecipeImage = MutableLiveData<File?>()
    val newRecipeImage: LiveData<File?> get() = _newRecipeImage

    fun setNewRecipeImage(value: File?) {
        _newRecipeImage.value = value
    }

    private val _newRecipeStepImages = MutableLiveData<HashMap<Int, File>?>()
    val newRecipeStepImages: LiveData<HashMap<Int, File>?> get() = _newRecipeStepImages

    fun setNewRecipeStepImages(value: HashMap<Int, File>?) {
        _newRecipeStepImages.value = value
    }

    private val _newRecipeSteps = MutableLiveData<ArrayList<CookingStep?>>()
    val newRecipeSteps: LiveData<ArrayList<CookingStep?>> get() = _newRecipeSteps

    fun setNewRecipeSteps(value: ArrayList<CookingStep?>) {
        _newRecipeSteps.value = value
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

    private val _isRecipeCreated = MutableLiveData<Boolean?>()
    val isRecipeCreated: LiveData<Boolean?> get() = _isRecipeCreated

    fun setIsRecipeCreated(value: Boolean?) {
        _isRecipeCreated.value = value
    }

    fun recipeCreate(recipe: Recipe, callback: ResultCallback<Void>) {
        repository.recipeCreate(recipe, object : ResultCallback<Void> {
            override fun onResult(value: Void?) {
                callback.onResult(value)
            }

            override fun onFailure(value: Void?) {
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

    fun createStep(description: String, cookingStepValue: CookingStep, image: File?) {
        val cookingStep: CookingStep = cookingStepValue
        if (selectedCookingStep.value != null) {
            selectedCookingStep.value?.description = description
        } else {
            var cookingStepsAmount = newRecipe.value?.steps!!.size
            cookingStep.description = description
            cookingStep.stepNumber = ++cookingStepsAmount
            newRecipe.value?.steps!!.add(cookingStep)
        }

        image.let {
            (cookingStep.stepNumber ?: selectedCookingStep.value?.stepNumber)?.let {
                image?.let { it1 ->
                    newRecipeStepImages.value?.put(
                        it, it1
                    )
                }
            }
        }
        setSelectedCookingStep(null)
        setSelectedCookingStepPosition(null)
    }
}