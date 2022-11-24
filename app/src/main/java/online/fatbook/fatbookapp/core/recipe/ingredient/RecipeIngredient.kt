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
            if (unit!!.title == "kg" && ingredient!!.unitRatio!!.unit!!.title == "g"
                || unit!!.title == "l" && ingredient!!.unitRatio!!.unit!!.title == "ml"
            ) {
                return ingredient!!.unitRatio!!.kcal!!.div(100) * (quantity!! * 1000)
            } else if (unit!! == ingredient!!.unitRatio!!.unit) {
                return ingredient!!.unitRatio!!.kcal!!.div(100) * quantity!!
            } else {
                return 0.0
            }
        }
    val proteins: Double
        get() {
            if (unit!!.title == "kg" && ingredient!!.unitRatio!!.unit!!.title == "g"
                || unit!!.title == "l" && ingredient!!.unitRatio!!.unit!!.title == "ml"
            ) {
                return ingredient!!.unitRatio!!.proteins!!.div(100) * (quantity!! * 1000)
            } else if (unit!! == ingredient!!.unitRatio!!.unit) {
                return ingredient!!.unitRatio!!.proteins!!.div(100) * quantity!!
            } else {
                return 0.0
            }
        }
    val fats: Double
        get() {
            if (unit!!.title == "kg" && ingredient!!.unitRatio!!.unit!!.title == "g"
                || unit!!.title == "l" && ingredient!!.unitRatio!!.unit!!.title == "ml"
            ) {
                return ingredient!!.unitRatio!!.fats!!.div(100) * (quantity!! * 1000)
            } else if (unit!! == ingredient!!.unitRatio!!.unit) {
                return ingredient!!.unitRatio!!.fats!!.div(100) * quantity!!
            } else {
                return 0.0
            }
        }
    val carbs: Double
        get() {
            if (unit!!.title == "kg" && ingredient!!.unitRatio!!.unit!!.title == "g"
                || unit!!.title == "l" && ingredient!!.unitRatio!!.unit!!.title == "ml"
            ) {
                return ingredient!!.unitRatio!!.carbs!!.div(100) * (quantity!! * 1000)
            } else if (unit!! == ingredient!!.unitRatio!!.unit) {
                return ingredient!!.unitRatio!!.carbs!!.div(100) * quantity!!
            } else {
                return 0.0
            }
        }
}
