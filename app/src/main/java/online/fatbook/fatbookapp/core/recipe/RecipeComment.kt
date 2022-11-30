package online.fatbook.fatbookapp.core.recipe

import online.fatbook.fatbookapp.core.user.UserSimpleObject
import java.io.Serializable

data class RecipeComment(
    var pid: Long? = null,
    var user: UserSimpleObject? = null,
    var comment: String? = "",
    var timestamp: String? = ""
) : Serializable