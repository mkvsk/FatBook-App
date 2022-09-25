package online.fatbook.fatbookapp.core.recipe

import java.io.Serializable

data class CookingCategory(
    var pid: Long? = null,
    var title: String? = ""
) : Serializable