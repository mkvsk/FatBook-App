package online.fatbook.fatbookapp.core.recipe.ingredient.unit

import java.io.Serializable

data class IngredientUnitRatio(
    var pid: Long? = null,
    var kcal: Double? = 0.0,
    var proteins: Double? = 0.0,
    var fats: Double? = 0.0,
    var carbs: Double? = 0.0,
    var amount: Int? = 0,
    var unit: IngredientUnit? = null
) : Serializable