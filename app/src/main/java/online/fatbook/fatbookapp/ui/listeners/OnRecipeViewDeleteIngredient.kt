package online.fatbook.fatbookapp.ui.listeners

import online.fatbook.fatbookapp.core.RecipeIngredient

interface OnRecipeViewDeleteIngredient {
    fun onDeleteIngredientClick(recipeIngredient: RecipeIngredient?, position: Int)
}