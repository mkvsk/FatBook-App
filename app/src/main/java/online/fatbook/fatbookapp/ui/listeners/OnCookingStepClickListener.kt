package online.fatbook.fatbookapp.ui.listeners

import online.fatbook.fatbookapp.core.recipe.CookingStep

interface OnCookingStepClickListener {
    fun onCookingStepClick(selectedStep: Int, step: Int, value: CookingStep)
    fun onRecipeCookingStepDelete(selectedStep: Int)
}