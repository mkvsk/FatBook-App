package online.fatbook.fatbookapp.ui.listeners

import online.fatbook.fatbookapp.core.Recipe

interface OnRecipeRevertDeleteListener {
    fun onRecipeRevertDeleteClick(recipe: Recipe?)
}