package online.fatbook.fatbookapp.core

import java.io.Serializable

class Recipe : Serializable {
    var pid: Long? = null
    var name: String? = null
    var description: String? = null
    var author: String? = null
    var ingredients: ArrayList<RecipeIngredient>? = null
    var image: String? = null
    var forks: Int? = null
    var createDate: String? = null
    var identifier: Long? = null
}