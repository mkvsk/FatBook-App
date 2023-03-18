package online.fatbook.fatbookapp.ui.recipe.listeners

import online.fatbook.fatbookapp.core.recipe.Recipe

interface OnRecipeRevertDeleteListener {
    fun onRecipeRevertDeleteClick(recipe: Recipe?)
}