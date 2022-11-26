package online.fatbook.fatbookapp.ui.listeners

import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient

interface OnRecipeViewDeleteIngredientListener {
    fun onDeleteIngredientClick(recipeIngredient: RecipeIngredient?, position: Int)
}