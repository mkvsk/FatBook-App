package online.fatbook.fatbookapp.core.recipe

import java.io.Serializable

data class CookingMethod(
    var pid: Long? = null,
    var title: String? = ""
) : Serializable
