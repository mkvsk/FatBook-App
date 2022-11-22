package online.fatbook.fatbookapp.core.recipe.ingredient.unit

import online.fatbook.fatbookapp.core.recipe.Locale
import online.fatbook.fatbookapp.util.AppInfo
import java.io.Serializable
import java.util.*

data class IngredientUnit(
    val pid: Long? = null,
    val position: Int? = 0,
    val locales: Map<Locale, IngredientUnitLocalized> = EnumMap(
        Locale::class.java
    )
) : Serializable, IngredientUnitObject() {

    override val title: String?
        get() = when (AppInfo.locale.language) {
            Locale.RU.name -> if (locales[Locale.RU] == null) super.title ?: "" else locales[Locale.RU]!!.title
            else -> super.title ?: ""
        }

    override val titleSingle: String?
        get() = when (AppInfo.locale.language) {
            Locale.RU.name -> if (locales[Locale.RU] == null) super.titleSingle ?: "" else locales[Locale.RU]!!.titleSingle
            else -> super.titleSingle ?: ""
        }

    override val titleMultiply: String?
        get() = when (AppInfo.locale.language) {
            Locale.RU.name -> if (locales[Locale.RU] == null) super.titleMultiply ?: "" else locales[Locale.RU]!!.titleMultiply
            else -> super.titleMultiply ?: ""
        }
}
