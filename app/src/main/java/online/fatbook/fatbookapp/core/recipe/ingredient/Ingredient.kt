package online.fatbook.fatbookapp.core.recipe.ingredient

import online.fatbook.fatbookapp.core.recipe.StaticDataLocale
import online.fatbook.fatbookapp.core.recipe.StaticDataLocalizedObject
import online.fatbook.fatbookapp.core.recipe.StaticDataObject
import online.fatbook.fatbookapp.util.AppInfo
import org.apache.commons.lang3.StringUtils
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class Ingredient(
        val pid: Long? = null,
        val units: ArrayList<IngredientUnitRatio>? = ArrayList(),
        val locales: Map<StaticDataLocale, StaticDataLocalizedObject> = EnumMap(StaticDataLocale::class.java)
) : Serializable, StaticDataObject() {

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
        return "Ingredient: (pid=$pid, title=$title, locales=$locales)"
    }
}