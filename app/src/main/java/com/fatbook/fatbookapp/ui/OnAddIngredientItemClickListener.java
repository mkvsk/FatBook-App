package com.fatbook.fatbookapp.ui;

import com.fatbook.fatbookapp.core.Ingredient;

public interface OnAddIngredientItemClickListener {
    void onIngredientClick(int previousItem, int selectedItem, Ingredient ingredient);
}
