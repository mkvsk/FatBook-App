package online.fatbook.fatbookapp.util

import online.fatbook.fatbookapp.core.recipe.*
import online.fatbook.fatbookapp.core.recipe.Locale
import java.util.*

object SearchUtils {

    //TODO fix this hueta
    private fun getLocale(): Map<Locale, StaticDataLocalized> {
        val result: EnumMap<Locale, StaticDataLocalized> =
            EnumMap(Locale::class.java)
        result[Locale.RU] = StaticDataLocalized(Locale.RU, "Выбрать все")
        result[Locale.ENG] = StaticDataLocalized(Locale.ENG, "Select all")
        return result
    }

    fun <T : StaticDataObject> getSelectAll(clazz: Class<T>): T {
//        (clazz.declaredConstructors[1].newInstance(null, getLocale()) as T).title =
        return clazz.declaredConstructors[1].newInstance(null, getLocale()) as T
    }

}