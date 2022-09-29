package online.fatbook.fatbookapp.core.recipe.ingredient

import java.io.Serializable

data class RecipeIngredient(
    var pid: Long? = null,
    var ingredient: Ingredients? = null,
    var unit: IngredientUnit? = null,
    var quantity: Double? = 0.0
) : Serializable
