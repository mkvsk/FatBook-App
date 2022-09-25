package online.fatbook.fatbookapp.core.recipe

import java.io.Serializable

data class CookingStep (
    var pid: Long? = null,
    var stepNumber: Int? = null,
    var description: String? = "",
    var image: String? = ""
) : Serializable
