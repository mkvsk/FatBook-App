package online.fatbook.fatbookapp.core

import java.io.Serializable

class User : Serializable {
    var pid: Long? = null
    var name: String? = null
    var login: String? = null
    var birthday: String? = null
    var bio: String? = null
    var role: Role? = null
    var image: String? = null
    var regDate: String? = null
    var recipes: List<Recipe>? = null
    var recipesForked: List<Long>? = null
    var recipesBookmarked: ArrayList<Long>? = null
}