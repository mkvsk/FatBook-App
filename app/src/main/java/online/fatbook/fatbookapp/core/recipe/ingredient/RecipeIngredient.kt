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
            return when (unit!!.ordinal) {
                1, 2 -> {
                    ingredient!!.unitRatio!!.kcal!!.div(100) * quantity!!
                }
                3, 4 -> {
                    ingredient!!.unitRatio!!.kcal!!.div(100) * (quantity!! * 1000)
                }
                else -> {
                    0.0
                }
            }
        }
    val proteins: Double
        get() {
            return when (unit!!.ordinal) {
                1, 2 -> {
                    ingredient!!.unitRatio!!.proteins!!.div(100) * quantity!!
                }
                3, 4 -> {
                    ingredient!!.unitRatio!!.proteins!!.div(100) * (quantity!! * 1000)
                }
                else -> {
                    0.0
                }
            }
        }
    val fats: Double
        get() {
            return when (unit!!.ordinal) {
                1, 2 -> {
                    ingredient!!.unitRatio!!.fats!!.div(100) * quantity!!
                }
                3, 4 -> {
                    ingredient!!.unitRatio!!.fats!!.div(100) * (quantity!! * 1000)
                }
                else -> {
                    0.0
                }
            }
        }
    val carbs: Double
        get() {
            return when (unit!!.ordinal) {
                1, 2 -> {
                    ingredient!!.unitRatio!!.carbs!!.div(100) * quantity!!
                }
                3, 4 -> {
                    ingredient!!.unitRatio!!.carbs!!.div(100) * (quantity!! * 1000)
                }
                else -> {
                    0.0
                }
            }
        }
}
