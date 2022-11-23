package online.fatbook.fatbookapp.ui.listeners

import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject

interface OnRecipeClickListener {
    fun onRecipeClick(position: Int)
    fun onBookmarksClick(recipe: RecipeSimpleObject?, bookmark: Boolean, position: Int)
    fun onForkClicked(recipe: RecipeSimpleObject?, fork: Boolean, position: Int)
    //TODO
//    onUsernameClick
}