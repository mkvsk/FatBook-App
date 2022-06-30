package online.fatbook.fatbookapp.core

import java.io.Serializable

class RecipeIngredient : Serializable {
    var pid: Long? = null
    var ingredient: Ingredient? = null
    var unit: IngredientUnit? = null
    var quantity = 0.0
}