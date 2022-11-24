package online.fatbook.fatbookapp.core.recipe.ingredient.unit

import online.fatbook.fatbookapp.core.recipe.Locale

data class IngredientUnitLocalized(
        var locale: Locale = Locale.EN,
        var title: String? = "",
        val titleSingle: String? = "",
        val titleMultiply: String? = ""
)

