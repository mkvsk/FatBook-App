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
            return if (unit!!.ordinal == 3 && ingredient!!.unitRatio!!.unit!!.ordinal == 1
                || unit!!.ordinal == 4 && ingredient!!.unitRatio!!.unit!!.ordinal == 2
            ) {
                ingredient!!.unitRatio!!.kcal!!.div(100) * (quantity!! * 1000)
            } else if (unit!! == ingredient!!.unitRatio!!.unit) {
                ingredient!!.unitRatio!!.kcal!!.div(100) * quantity!!
            } else {
                0.0
            }
        }
    val proteins: Double
        get() {
            return if (unit!!.ordinal == 3 && ingredient!!.unitRatio!!.unit!!.ordinal == 1
                || unit!!.ordinal == 4 && ingredient!!.unitRatio!!.unit!!.ordinal == 2
            ) {
                ingredient!!.unitRatio!!.proteins!!.div(100) * (quantity!! * 1000)
            } else if (unit!! == ingredient!!.unitRatio!!.unit) {
                ingredient!!.unitRatio!!.proteins!!.div(100) * quantity!!
            } else {
                0.0
            }
        }
    val fats: Double
        get() {
            return if (unit!!.ordinal == 3 && ingredient!!.unitRatio!!.unit!!.ordinal == 1
                || unit!!.ordinal == 4 && ingredient!!.unitRatio!!.unit!!.ordinal == 2
            ) {
                ingredient!!.unitRatio!!.fats!!.div(100) * (quantity!! * 1000)
            } else if (unit!! == ingredient!!.unitRatio!!.unit) {
                ingredient!!.unitRatio!!.fats!!.div(100) * quantity!!
            } else {
                0.0
            }
        }
    val carbs: Double
        get() {
            return if (unit!!.ordinal == 3 && ingredient!!.unitRatio!!.unit!!.ordinal == 1
                || unit!!.ordinal == 4 && ingredient!!.unitRatio!!.unit!!.ordinal == 2
            ) {
                ingredient!!.unitRatio!!.carbs!!.div(100) * (quantity!! * 1000)
            } else if (unit!! == ingredient!!.unitRatio!!.unit) {
                ingredient!!.unitRatio!!.carbs!!.div(100) * quantity!!
            } else {
                0.0
            }
        }
}
