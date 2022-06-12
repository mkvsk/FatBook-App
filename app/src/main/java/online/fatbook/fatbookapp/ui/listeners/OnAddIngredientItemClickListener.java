package online.fatbook.fatbookapp.ui.listeners;

import online.fatbook.fatbookapp.core.Ingredient;

public interface OnAddIngredientItemClickListener {
    void onIngredientClick(int previousItem, int selectedItem, Ingredient ingredient);
}
