package online.fatbook.fatbookapp.core.recipe.ingredient

import online.fatbook.fatbookapp.core.recipe.StaticDataLocale
import online.fatbook.fatbookapp.core.recipe.StaticDataObject
import java.io.Serializable
import java.util.*

data class IngredientUnit(
    val pid: Long? = null,
    val titleSingle: String? = "",
    val titleMultiply: String? = "",
    val locales: Map<StaticDataLocale, IngredientUnitLocalized> = EnumMap(
        StaticDataLocale::class.java
    )
) : Serializable, StaticDataObject()