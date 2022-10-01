package online.fatbook.fatbookapp.core.recipe

import java.io.Serializable

data class CookingCategory(
    var pid: Long? = null
) : Serializable, StaticDataObject() {
    override fun toString(): String {
        return title.toString()
    }
}