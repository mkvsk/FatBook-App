package online.fatbook.fatbookapp.ui.recipe.listeners

import online.fatbook.fatbookapp.core.recipe.CookingDifficulty

interface OnRecipeDifficultyClickListener {
    fun onRecipeDifficultyClick(previousItem: Int, selectedItem: Int, difficulty: CookingDifficulty?)
}