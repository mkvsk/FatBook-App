package online.fatbook.fatbookapp.util

import online.fatbook.fatbookapp.core.recipe.*
import java.util.*

object SearchUtils {

    private fun getLocale(): Map<StaticDataLocale, StaticDataLocalized> {
        val result: EnumMap<StaticDataLocale, StaticDataLocalized> =
            EnumMap(StaticDataLocale::class.java)
        result[StaticDataLocale.RU] = StaticDataLocalized(StaticDataLocale.RU, "Выбрать все")
        result[StaticDataLocale.ENG] = StaticDataLocalized(StaticDataLocale.ENG, "Select all")
        return result
    }

    fun <T : StaticDataBase> getSelectAll(clazz: Class<T>): T {
        return clazz.declaredConstructors[1].newInstance(null, getLocale()) as T
    }

}