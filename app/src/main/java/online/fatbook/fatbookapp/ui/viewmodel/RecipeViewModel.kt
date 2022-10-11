package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient

class RecipeViewModel : ViewModel() {
    var recipe = MutableLiveData<Recipe?>()
    var selectedRecipeIngredients = MutableLiveData<MutableList<RecipeIngredient?>>()
    var selectedRecipeIngredient = MutableLiveData<RecipeIngredient>()
    var targetRecipe = MutableLiveData<Recipe>()
    var selectedRecipePosition = MutableLiveData<Int?>()
    var selectedRecipe = MutableLiveData<Recipe?>()
    var newRecipe = MutableLiveData<Recipe?>()

    var newRecipeIngredients = MutableLiveData<ArrayList<RecipeIngredient?>>()
    var newRecipeCookingMethod = MutableLiveData<CookingMethod?>()
    var newRecipeCookingCategories = MutableLiveData<ArrayList<CookingCategory?>>()

    var newRecipeAddIngredient = MutableLiveData<Ingredient?>()
    var newRecipeAddRecipeIngredient = MutableLiveData<RecipeIngredient?>()
}