package online.fatbook.fatbookapp.core.recipe.ingredient

import java.io.Serializable

data class RecipeIngredient(
    var pid: Long? = null,
    var ingredient: Ingredient? = null,
    var unit: IngredientUnit? = null,
    var quantity: Double? = 0.0
) : Serializable {

    val kcal: Double?
        get() {
            val find = ingredient!!.units!!.find { it.unit == unit }
            return if (find!!.unit == IngredientUnit.GRAM || find.unit == IngredientUnit.ML) {
                find.kcal!! / 100 * quantity!!
            } else {
                null
            }
        }

    val proteins: Double?
        get() {
            val find = ingredient!!.units!!.find { it.unit == unit }
            return if (find!!.unit == IngredientUnit.GRAM || find.unit == IngredientUnit.ML) {
                find.proteins!! / 100 * quantity!!
            } else {
                null
            }
        }
    val fats: Double?
        get() {
            val find = ingredient!!.units!!.find { it.unit == unit }
            return if (find!!.unit == IngredientUnit.GRAM || find.unit == IngredientUnit.ML) {
                find.fats!! / 100 * quantity!!
            } else {
                null
            }
        }
    val carbs: Double?
        get() {
            val find = ingredient!!.units!!.find { it.unit == unit }
            return if (find!!.unit == IngredientUnit.GRAM || find.unit == IngredientUnit.ML) {
                find.carbs!! / 100 * quantity!!
            } else {
                null
            }
        }
}
