package online.fatbook.fatbookapp.ui.listeners

import online.fatbook.fatbookapp.core.Ingredient

interface OnAddIngredientItemClickListener {
    fun onIngredientClick(previousItem: Int, selectedItem: Int, ingredient: Ingredient?)
}