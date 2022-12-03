package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.core.recipe.*
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.repository.RecipeRepository
import java.io.File

class NewRecipeViewModel : ViewModel() {

    companion object {
        private const val TAG = "NewRecipeViewModel"
    }

    private val repository by lazy { RecipeRepository() }

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


}