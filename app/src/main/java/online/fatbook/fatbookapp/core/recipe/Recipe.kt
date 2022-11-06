package online.fatbook.fatbookapp.core.recipe

import online.fatbook.fatbookapp.core.recipe.ingredient.IngredientUnit
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import java.io.Serializable

data class Recipe(
    val pid: Long? = null,
    var title: String? = "",
    var author: String? = null,
    var image: String? = "",
    var forks: Int? = 0,
    var createDate: String? = "",
    val identifier: Long? = null,
    var difficulty: CookingDifficulty? = null,
    var portions: Int? = null,
    var cookingTime: String? = "",
    var cookingMethod: CookingMethod? = null,
    var cookingCategories: ArrayList<CookingCategory>? = ArrayList(),
    var isPrivate: Boolean? = false,
    var ingredients: ArrayList<RecipeIngredient>? = ArrayList(),
//    var fatsPerPortion: Double? = 0.0,
//    var carbsPerPortion: Double? = 0.0,
//    var proteinsPerPortion: Double? = 0.0,
    var steps: ArrayList<CookingStep>? = ArrayList(),
    var comments: ArrayList<RecipeComment>? = ArrayList()
) : Serializable {
    val kcalPerPortion: Double?
        get() {
            return if (isAllIngredientUnitsValid) {
                var tmp = 0.0
                for (i in ingredients!!) {
                    if (i.unit == IngredientUnit.GRAM || i.unit == IngredientUnit.ML) {
                        tmp = tmp.plus(i.kcal!!)
                    }
                }
                tmp / portions.toString().toDouble()
            } else {
                null
            }
        }

    val isAllIngredientUnitsValid: Boolean
        get() {
            val tmp = ingredients!!.find {
                it.unit != IngredientUnit.GRAM && it.unit != IngredientUnit.ML
            }
            return tmp == null
        }

    val fatsPerPortion: Double?
        get() {
            return if (isAllIngredientUnitsValid) {
                var tmp = 0.0
                for (i in ingredients!!) {
                    tmp = tmp.plus(i.fats!!)
                }
                tmp / portions.toString().toDouble()
            } else {
                null
            }
        }

    val carbsPerPortion: Double?
        get() {
            return if (isAllIngredientUnitsValid) {
                var tmp = 0.0
                for (i in ingredients!!) {
                    tmp = tmp.plus(i.carbs!!)
                }
                tmp / portions.toString().toDouble()
            } else {
                null
            }
        }

    val proteinsPerPortion: Double?
        get() {
            return if (isAllIngredientUnitsValid) {
                var tmp = 0.0
                for (i in ingredients!!) {
                    tmp = tmp.plus(i.proteins!!)
                }
                tmp / portions.toString().toDouble()
            } else {
                null
            }
        }
}

