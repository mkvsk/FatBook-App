package online.fatbook.fatbookapp.ui.listeners

import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredients

interface OnAddIngredientItemClickListener {
    fun onIngredientClick(previousItem: Int, selectedItem: Int, ingredient: Ingredients?)
}