package online.fatbook.fatbookapp.ui.listeners

import online.fatbook.fatbookapp.core.recipe.CookingDifficulty

interface OnRecipeDifficultyClickListener {
    fun onRecipeDifficultyClick(previousItem: Int, selectedItem: Int, difficulty: CookingDifficulty?)
}