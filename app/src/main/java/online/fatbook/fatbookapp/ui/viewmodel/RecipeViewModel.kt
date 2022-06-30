package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.core.Recipe
import online.fatbook.fatbookapp.core.RecipeIngredient

class RecipeViewModel : ViewModel() {
    var recipe = MutableLiveData<Recipe?>()
    var selectedRecipeIngredients = MutableLiveData<MutableList<RecipeIngredient?>>()
    var selectedRecipeIngredient = MutableLiveData<RecipeIngredient>()
    var targetRecipe = MutableLiveData<Recipe>()
    var selectedRecipePosition = MutableLiveData<Int?>()
    var selectedRecipe = MutableLiveData<Recipe?>()
}