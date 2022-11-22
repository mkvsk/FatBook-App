package online.fatbook.fatbookapp.core.recipe.ingredient

import online.fatbook.fatbookapp.core.recipe.Locale
import online.fatbook.fatbookapp.core.recipe.StaticDataLocalized
import online.fatbook.fatbookapp.core.recipe.StaticDataObject
import online.fatbook.fatbookapp.core.recipe.ingredient.unit.IngredientUnitRatio
import online.fatbook.fatbookapp.util.AppInfo
import java.io.Serializable
import java.util.*

data class Ingredient(
    val pid: Long? = null,
    val unitRatio: IngredientUnitRatio? = null,
    val locales: Map<Locale, StaticDataLocalized> = EnumMap(Locale::class.java)
) : Serializable, StaticDataObject() {

    override val title: String?
        get() = when (AppInfo.locale.language) {
            Locale.RU.name -> if (locales[Locale.RU] == null) super.title ?: "" else locales[Locale.RU]!!.title
            else -> super.title ?: ""
        }

    override fun toString(): String {
        return "Ingredient: (pid=$pid, title=$title, units=$unitRatio, locales=$locales)"
    }
}