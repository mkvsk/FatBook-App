package online.fatbook.fatbookapp.ui.recipe.listeners

import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient

interface OnIngredientItemClickListener {
    fun onIngredientClick(previousItem: Int, selectedItem: Int, ingredient: Ingredient?)
}