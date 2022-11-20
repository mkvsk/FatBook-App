package online.fatbook.fatbookapp.core.recipe.ingredient

import online.fatbook.fatbookapp.core.recipe.ingredient.unit.IngredientUnit
import java.io.Serializable

data class RecipeIngredient(
    var pid: Long? = null,
    var ingredient: Ingredient? = null,
    var unit: IngredientUnit? = null,
    var quantity: Double? = 0.0
) : Serializable {

    val kcal: Double
        get() {
            return ingredient!!.unitRatio!!.kcal!!.div(100) * quantity!!
        }
    val proteins: Double
        get() {
            return ingredient!!.unitRatio!!.proteins!!.div(100) * quantity!!
        }
    val fats: Double
        get() {
            return ingredient!!.unitRatio!!.fats!!.div(100) * quantity!!
        }
    val carbs: Double
        get() {
            return ingredient!!.unitRatio!!.carbs!!.div(100) * quantity!!
        }
}
