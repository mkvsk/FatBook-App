package online.fatbook.fatbookapp.core.recipe.ingredient

import online.fatbook.fatbookapp.core.recipe.StaticDataLocale

data class IngredientUnitLocalized(
    var locale: StaticDataLocale = StaticDataLocale.ENG,
    var title: String? = "",
    val titleSingle: String? = "",
    val titleMultiply: String? = ""
)

