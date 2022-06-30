package online.fatbook.fatbookapp.core

import java.io.Serializable

class RecipeIngredient : Serializable {
    val pid: Long? = null
    val ingredient: Ingredient? = null
    val unit: IngredientUnit? = null
    val quantity = 0.0
}