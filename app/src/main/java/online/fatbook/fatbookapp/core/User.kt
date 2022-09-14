package online.fatbook.fatbookapp.core

import java.io.Serializable

class User : Serializable {
    var pid: Long? = null
    var name: String? = ""
    var login: String? = ""
    var birthday: String? = ""
    var bio: String? = ""
    var role: Role? = null
    var image: String? = ""
    var regDate: String? = ""
    var recipes: List<Recipe>? = ArrayList()
    var recipesForked: List<Long>? = ArrayList()
    var recipesBookmarked: ArrayList<Long>? = ArrayList()

    var website: String = ""
    var followers: Int? = null
    var online: Boolean? = false

}
