package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient

class IngredientViewModel : ViewModel() {
    val ingredientList = MutableLiveData<List<Ingredient>>()
}