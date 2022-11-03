package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.core.recipe.*
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import java.io.File

class RecipeViewModel : ViewModel() {
    var recipe = MutableLiveData<Recipe?>()
    var selectedRecipeIngredients = MutableLiveData<MutableList<RecipeIngredient?>>()
    var selectedRecipeIngredient = MutableLiveData<RecipeIngredient>()
    var targetRecipe = MutableLiveData<Recipe>()
    var selectedRecipePosition = MutableLiveData<Int?>()
    var selectedRecipe = MutableLiveData<Recipe?>()

    var newRecipe = MutableLiveData<Recipe?>()
    var newRecipeCookingDifficulty = MutableLiveData<CookingDifficulty?>()
    var newRecipeCookingMethod = MutableLiveData<CookingMethod?>()
    var newRecipeCookingCategories = MutableLiveData<ArrayList<CookingCategory?>>()

    var newRecipeIngredients = MutableLiveData<ArrayList<RecipeIngredient?>>()

    var newRecipeAddIngredient = MutableLiveData<Ingredient?>()
    var newRecipeAddRecipeIngredient = MutableLiveData<RecipeIngredient?>()

    var newRecipeImage = MutableLiveData<File?>()
    var newRecipeStepImages = MutableLiveData<Map<Int, File>?>()

    var newRecipeSteps = MutableLiveData<ArrayList<CookingStep?>>()

    var selectedCookingStep = MutableLiveData<CookingStep?>()
    var selectedCookingStepPosition = MutableLiveData<Int?>()
}