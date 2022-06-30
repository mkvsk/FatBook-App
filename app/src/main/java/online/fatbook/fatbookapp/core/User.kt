package online.fatbook.fatbookapp.core

import java.io.Serializable

class User : Serializable {
    val pid: Long? = null
    val name: String? = null
    val login: String? = null
    val birthday: String? = null
    val bio: String? = null
    val role: Role? = null
    val image: String? = null
    val regDate: String? = null
    val recipes: List<Recipe>? = null
    val recipesForked: List<Long>? = null
    val recipesBookmarked: ArrayList<Long>? = null
}