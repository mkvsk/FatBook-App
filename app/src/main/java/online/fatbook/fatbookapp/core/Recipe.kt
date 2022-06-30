package online.fatbook.fatbookapp.core

import java.io.Serializable

class Recipe : Serializable {
    val pid: Long? = null
    val name: String? = null
    val description: String? = null
    val author: String? = null
    val ingredients: List<RecipeIngredient>? = null
    val image: String? = null
    val forks: Int? = null
    val createDate: String? = null
    val identifier: Long? = null
}