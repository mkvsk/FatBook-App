package online.fatbook.fatbookapp.ui.listeners

import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject
import online.fatbook.fatbookapp.ui.adapters.RecipeAdapter

interface OnRecipeClickListener {
    fun onRecipeClick(id: Long)
    fun onBookmarksClick(recipe: RecipeSimpleObject?, bookmark: Boolean, position: Int)
    fun onForkClicked(recipe: RecipeSimpleObject?, fork: Boolean, position: Int)
    fun onForkClicked(
        recipe: RecipeSimpleObject?,
        fork: Boolean,
        position: Int,
        viewHolder: RecipeAdapter.ViewHolder
    )
    fun onUsernameClick(username: String)

}