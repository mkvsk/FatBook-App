package online.fatbook.fatbookapp.util

import online.fatbook.fatbookapp.core.recipe.*
import java.util.*

object SearchUtils {

    var DEVICE_HEIGHT = 0f
    var DEVICE_WIDTH = 0f

    private fun getLocale(): Map<StaticDataLocale, StaticDataLocalizedObject> {
        val result: EnumMap<StaticDataLocale, StaticDataLocalizedObject> =
            EnumMap(StaticDataLocale::class.java)
        result[StaticDataLocale.RU] = StaticDataLocalizedObject(StaticDataLocale.RU, "Выбрать все")
        result[StaticDataLocale.ENG] = StaticDataLocalizedObject(StaticDataLocale.ENG, "Select all")
        return result
    }

    fun <T : StaticDataObject> getSelectAll(clazz: Class<T>): T {
        return clazz.declaredConstructors[1].newInstance(null, getLocale()) as T
    }

}