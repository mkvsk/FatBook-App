package online.fatbook.fatbookapp.ui.listeners

import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject

interface OnRecipeClickListener {
    fun onRecipeClick(id: Long)
    fun onBookmarksClick(recipe: RecipeSimpleObject?, bookmark: Boolean, position: Int)
    fun onForkClicked(recipe: RecipeSimpleObject?, fork: Boolean, position: Int)
    fun onUsernameClick(username: String)
}