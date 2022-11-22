package online.fatbook.fatbookapp.util

import online.fatbook.fatbookapp.core.recipe.*
import online.fatbook.fatbookapp.core.recipe.Locale
import java.util.*

object SearchUtils {

    private fun getLocale(): Map<Locale, StaticDataLocalized> {
        val result: EnumMap<Locale, StaticDataLocalized> =
            EnumMap(Locale::class.java)
        result[Locale.RU] = StaticDataLocalized(Locale.RU, "Выбрать все")
        result[Locale.ENG] = StaticDataLocalized(Locale.ENG, "Select all")
        return result
    }

    fun <T : StaticDataBase> getSelectAll(clazz: Class<T>): T {
        return clazz.declaredConstructors[1].newInstance(null, getLocale()) as T
    }

}