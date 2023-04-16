package online.fatbook.fatbookapp.ui.base

import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject
import online.fatbook.fatbookapp.ui.feed.adapters.RecipeAdapter

interface OnRecipeClickListener {
    fun onRecipeClick(id: Long)
    fun onBookmarksClick(recipe: RecipeSimpleObject?, bookmark: Boolean, position: Int)
    fun onForkClicked(recipe: RecipeSimpleObject?, fork: Boolean, position: Int)
    fun onForkClicked(
        recipe: RecipeSimpleObject?,
        fork: Boolean,
        position: Int,
        recipePreviewItemViewHolder: RecipeAdapter.RecipePreviewItemViewHolder
    )
    fun onUsernameClick(username: String)

}