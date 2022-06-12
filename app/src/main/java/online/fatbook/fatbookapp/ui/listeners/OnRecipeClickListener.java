package online.fatbook.fatbookapp.ui.listeners;

import online.fatbook.fatbookapp.core.Recipe;

public interface OnRecipeClickListener {
    void onRecipeClick(int position);
    void onBookmarksClick(Recipe recipe, boolean bookmark, int position);
    void onForkClicked(Recipe recipe, boolean fork, int position);
}
