package online.fatbook.fatbookapp.core.recipe

import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import java.io.Serializable

data class Recipe(
    var pid: Long? = null,
    var title: String? = "",
    var author: String? = null,
    var image: String? = "",
    var forks: Int? = 0,
    var createDate: String? = "",
    var identifier: Long? = 0L,
    var difficulty: Difficulty? = Difficulty.NORMAL,
    var portions: Int? = 0,
    var cookingTime: String? = "",
    var cookingMethod: CookingMethod? = null,
    var cookingCategories: ArrayList<CookingCategory>? = ArrayList(),
    var isPrivate: Boolean? = false,
    var ingredients: ArrayList<RecipeIngredient>? = ArrayList(),
    var kcalPerPortion: Double? = 0.0,
    var fatsPerPortion: Double? = 0.0,
    var carbsPerPortion: Double? = 0.0,
    var steps: ArrayList<CookingStep>? = ArrayList(),
    var comments: ArrayList<RecipeComment>? = ArrayList()
) : Serializable

enum class Difficulty {
    EASY,
    NORMAL,
    HARD
}