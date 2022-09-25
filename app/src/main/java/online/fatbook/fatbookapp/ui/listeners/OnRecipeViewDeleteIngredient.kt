package online.fatbook.fatbookapp.ui.listeners

import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient

interface OnRecipeViewDeleteIngredient {
    fun onDeleteIngredientClick(recipeIngredient: RecipeIngredient?, position: Int)
}