package online.fatbook.fatbookapp.core

import java.io.Serializable

class Recipe() : Serializable {

    constructor(name: String?) : this() {
        this.name = name
    }

    var pid: Long? = null
    var name: String? = ""
    var description: String? = ""
    var author: String? = null
    var ingredients: ArrayList<RecipeIngredient>? = ArrayList()
    var image: String? = ""
    var forks: Int? = 0
    var createDate: String? = ""
    var identifier: Long? = 0L
}