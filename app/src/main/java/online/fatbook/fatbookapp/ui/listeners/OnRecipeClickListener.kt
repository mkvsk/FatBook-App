package online.fatbook.fatbookapp.ui.listeners

import online.fatbook.fatbookapp.core.recipe.Recipe

interface OnRecipeClickListener {
    fun onRecipeClick(position: Int)
    fun onBookmarksClick(recipe: Recipe?, bookmark: Boolean, position: Int)
    fun onForkClicked(recipe: Recipe?, fork: Boolean, position: Int)
}