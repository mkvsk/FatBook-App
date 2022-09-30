package online.fatbook.fatbookapp.core.recipe

import java.io.Serializable

data class CookingMethod(
    var pid: Long? = null
) : Serializable, StaticDataObject() {
    override fun toString(): String {
        return "CookingMethod: (pid=$pid, title=$title)"
    }
}
