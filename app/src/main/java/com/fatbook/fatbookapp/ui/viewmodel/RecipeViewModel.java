package com.fatbook.fatbookapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fatbook.fatbookapp.core.Recipe;

public class RecipeViewModel extends ViewModel {

    private final MutableLiveData<Recipe> selectedItem = new MutableLiveData<Recipe>();

    public void selectRecipe(Recipe recipe) {
        selectedItem.setValue(recipe);
    }

    public LiveData<Recipe> getSelectedRecipe() {
        return selectedItem;
    }
}
