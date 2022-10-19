package online.fatbook.fatbookapp.ui.listeners

import online.fatbook.fatbookapp.core.recipe.CookingStep

interface OnCookingStepClickListener {
    fun onCookingStepClick(value: CookingStep, itemPosition: Int)
    fun onRecipeCookingStepDelete(itemPosition: Int)
}