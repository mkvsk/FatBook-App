package online.fatbook.fatbookapp.core.recipe.ingredient

import java.io.Serializable

data class Ingredient(
    var pid: Long? = null,
    var name: String? = "",
    var units: ArrayList<IngredientUnitRatio>? = ArrayList()
) : Serializable