package online.fatbook.fatbookapp.core.recipe.ingredient.unit

import online.fatbook.fatbookapp.core.recipe.StaticDataLocale
import online.fatbook.fatbookapp.util.AppInfo
import org.apache.commons.lang3.StringUtils
import java.io.Serializable
import java.util.*

data class IngredientUnit(
    val pid: Long? = null,
    val position: Int? = 0,
    val locales: Map<StaticDataLocale, IngredientUnitLocalized> = EnumMap(
        StaticDataLocale::class.java
    )
) : Serializable, IngredientUnitBase() {

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

    override val titleSingle: String?
        get() = if (StringUtils.equalsIgnoreCase(AppInfo.locale.language, StaticDataLocale.RU.name)) {
            if (locales[StaticDataLocale.RU] != null) {
                locales[StaticDataLocale.RU]!!.titleSingle
            } else {
                super.titleSingle
            }
        } else {
            super.titleSingle
        }

    override val titleMultiply: String?
        get() = if (StringUtils.equalsIgnoreCase(AppInfo.locale.language, StaticDataLocale.RU.name)) {
            if (locales[StaticDataLocale.RU] != null) {
                locales[StaticDataLocale.RU]!!.titleMultiply
            } else {
                super.titleMultiply
            }
        } else {
            super.titleMultiply
        }

}