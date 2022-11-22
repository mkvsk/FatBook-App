package online.fatbook.fatbookapp.core.recipe

import online.fatbook.fatbookapp.util.AppInfo
import java.io.Serializable
import java.util.*

data class CookingCategory(
        val pid: Long? = null,
        val locales: Map<Locale, StaticDataLocalized> = EnumMap(Locale::class.java)
) : Serializable, StaticDataObject() {

    override val title: String?
        get() = when (AppInfo.locale.language) {
            Locale.RU.name -> if (locales[Locale.RU] == null) super.title ?: "" else locales[Locale.RU]!!.title
            else -> super.title ?: ""
        }

    override fun toString(): String {
        return "CookingCategory: (pid=$pid, title=$title, locales=$locales)"
    }
}