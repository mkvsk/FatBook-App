package online.fatbook.fatbookapp.ui.recipe.listeners

import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient

interface OnRecipeViewDeleteIngredientListener {
    fun onDeleteIngredientClick(recipeIngredient: RecipeIngredient?, position: Int)
}