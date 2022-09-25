package online.fatbook.fatbookapp.ui.listeners

import online.fatbook.fatbookapp.core.recipe.Recipe

interface OnRecipeRevertDeleteListener {
    fun onRecipeRevertDeleteClick(recipe: Recipe?)
}