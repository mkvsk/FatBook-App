package online.fatbook.fatbookapp.core.recipe

import java.io.Serializable

data class RecipeComment(
    var pid: Long? = null,
    var username: String? = "",
    var userImage: String? = "",
    var comment: String? = "",
    var timestamp: String? = ""
) : Serializable