package online.fatbook.fatbookapp.core.recipe.ingredient

import android.content.Context
import online.fatbook.fatbookapp.R

enum class IngredientUnit(
    private var singleNaming: Int,
    private var multiplyNaming: Int,
    private var displayName: Int
) {
    TABLE_SPOON(
        R.string.IngredientUnit_TABLE_SPOON_SINGLE_NAMING,
        R.string.IngredientUnit_TABLE_SPOON_MULTIPLY_NAMING,
        R.string.IngredientUnit_TABLE_SPOON_DISPLAY_NAME
    ),
    TEA_SPOON(
        R.string.IngredientUnit_TEA_SPOON_SINGLE_NAMING,
        R.string.IngredientUnit_TEA_SPOON_MULTIPLY_NAMING,
        R.string.IngredientUnit_TEA_SPOON_DISPLAY_NAME
    ),
    GRAM(
        R.string.IngredientUnit_GRAM_SINGLE_NAMING,
        R.string.IngredientUnit_GRAM_MULTIPLY_NAMING,
        R.string.IngredientUnit_GRAM_DISPLAY_NAME
    ),
    KG(
        R.string.IngredientUnit_GRAM_SINGLE_NAMING,
        R.string.IngredientUnit_GRAM_MULTIPLY_NAMING,
        R.string.IngredientUnit_GRAM_DISPLAY_NAME
    ),
    PCS(
        R.string.IngredientUnit_PCS_SINGLE_NAMING,
        R.string.IngredientUnit_PCS_MULTIPLY_NAMING,
        R.string.IngredientUnit_PCS_DISPLAY_NAME
    ),
    ML(
        R.string.IngredientUnit_ML_SINGLE_NAMING,
        R.string.IngredientUnit_ML_MULTIPLY_NAMING,
        R.string.IngredientUnit_ML_DISPLAY_NAME
    ),
    L(
        R.string.IngredientUnit_ML_SINGLE_NAMING,
        R.string.IngredientUnit_ML_MULTIPLY_NAMING,
        R.string.IngredientUnit_ML_DISPLAY_NAME
    ),
    CUP(
        R.string.IngredientUnit_ML_SINGLE_NAMING,
        R.string.IngredientUnit_ML_MULTIPLY_NAMING,
        R.string.IngredientUnit_ML_DISPLAY_NAME
    );

    fun getSingleNaming(context: Context): String {
        return context.getString(singleNaming)
    }

    fun getMultiplyNaming(context: Context): String {
        return context.getString(multiplyNaming)
    }

    fun getDisplayName(context: Context): String {
        return context.getString(displayName)
    }

    // 2.3 -> multiplyNaming
    // 1.0 -> singleNaming
    fun getProperNaming(value: Double?): String? {
        return null
    }

    // 2.3 -> 2.3
    // 2.0 -> 2
    fun cutZero(value: Double?): String? {
        return null
    }
}