package com.fatbook.fatbookapp.ui.listeners;

import com.fatbook.fatbookapp.core.Recipe;

public interface OnRecipeClickListener {
    void onRecipeClick(int position);
    void onBookmarksClick(Recipe recipe, boolean add);
    void onForkClicked(Recipe recipe, boolean fork, int position);
}
