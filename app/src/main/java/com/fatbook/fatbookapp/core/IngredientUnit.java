package com.fatbook.fatbookapp.core;

import android.content.Context;

import com.fatbook.fatbookapp.R;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum IngredientUnit {

    ML(R.string.IngredientUnit_ML_SINGLE_NAMING, R.string.IngredientUnit_ML_MULTIPLY_NAMING, R.string.IngredientUnit_ML_DISPLAY_NAME),
    PCS(R.string.IngredientUnit_PCS_SINGLE_NAMING, R.string.IngredientUnit_PCS_MULTIPLY_NAMING, R.string.IngredientUnit_PCS_DISPLAY_NAME),
    GRAM(R.string.IngredientUnit_GRAM_SINGLE_NAMING, R.string.IngredientUnit_GRAM_MULTIPLY_NAMING, R.string.IngredientUnit_GRAM_DISPLAY_NAME),
    TEA_SPOON(R.string.IngredientUnit_TEA_SPOON_SINGLE_NAMING, R.string.IngredientUnit_TEA_SPOON_MULTIPLY_NAMING, R.string.IngredientUnit_TEA_SPOON_DISPLAY_NAME),
    TABLE_SPOON(R.string.IngredientUnit_TABLE_SPOON_SINGLE_NAMING, R.string.IngredientUnit_TABLE_SPOON_MULTIPLY_NAMING, R.string.IngredientUnit_TABLE_SPOON_DISPLAY_NAME);

    private final int singleNaming;
    private final int multiplyNaming;
    private final int displayName;

    public String getSingleNaming(Context context) {
        return context.getString(singleNaming);
    }

    public String getMultiplyNaming(Context context) {
        return context.getString(multiplyNaming);
    }

    public String getDisplayName(Context context) {
        return context.getString(displayName);
    }

    // 2.3 -> multiplyNaming
    // 1.0 -> singleNaming
    public String getProperNaming(Double value) {
        return null;
    }

    // 2.3 -> 2.3
    // 2.0 -> 2
    public String cutZero(Double value) {
        return null;
    }
}
