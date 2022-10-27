package online.fatbook.fatbookapp.core.recipe

import online.fatbook.fatbookapp.util.AppInfo
import org.apache.commons.lang3.StringUtils
import java.io.Serializable
import java.util.*

data class CookingCategory(
        val pid: Long? = null,
        val locales: Map<StaticDataLocale, StaticDataLocalizedObject> = EnumMap(StaticDataLocale::class.java)
) : Serializable, StaticDataObject() {

    override val title: String?
        get() = if (StringUtils.equalsIgnoreCase(AppInfo.locale.language, StaticDataLocale.RU.name)) {
            if (locales[StaticDataLocale.RU] != null) {
                locales[StaticDataLocale.RU]!!.title
            } else {
                if (super.title.isNullOrEmpty()) {
                    locales[StaticDataLocale.ENG]!!.title
                } else {
                    super.title
                }
            }
        } else {
            if (super.title.isNullOrEmpty()) {
                locales[StaticDataLocale.ENG]!!.title
            } else {
                super.title
            }
        }

    override fun toString(): String {
        return "CookingCategory: (pid=$pid, title=$title, locales=$locales)"
    }
}