package online.fatbook.fatbookapp.core.recipe.ingredient

import online.fatbook.fatbookapp.core.recipe.StaticDataLocale
import online.fatbook.fatbookapp.core.recipe.StaticDataLocalized
import online.fatbook.fatbookapp.core.recipe.StaticDataBase
import online.fatbook.fatbookapp.core.recipe.ingredient.unit.IngredientUnitRatio
import online.fatbook.fatbookapp.util.AppInfo
import org.apache.commons.lang3.StringUtils
import java.io.Serializable
import java.util.*

data class Ingredient(
    val pid: Long? = null,
    val unitRatio: IngredientUnitRatio? = null,
    val locales: Map<StaticDataLocale, StaticDataLocalized> = EnumMap(StaticDataLocale::class.java)
) : Serializable, StaticDataBase() {

    override val title: String?
        get() = if (StringUtils.equalsIgnoreCase(AppInfo.locale.language, StaticDataLocale.RU.name)) {
            if (locales[StaticDataLocale.RU] != null) {
                locales[StaticDataLocale.RU]!!.title
            } else {
                super.title
            }
        } else {
            super.title
        }

    override fun toString(): String {
        return "Ingredient: (pid=$pid, title=$title, units=$unitRatio, locales=$locales)"
    }
}