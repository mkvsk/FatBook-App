package com.fatbook.fatbookapp.ui;

public interface OnRecipeClickListener {
    void onRecipeClick(int position);
    void onBookmarksClick(int position, boolean add);
    void onForkClicked(int position, boolean fork);
}
