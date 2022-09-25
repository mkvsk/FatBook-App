package online.fatbook.fatbookapp.ui.listeners

import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient

interface OnAddIngredientItemClickListener {
    fun onIngredientClick(previousItem: Int, selectedItem: Int, ingredient: Ingredient?)
}