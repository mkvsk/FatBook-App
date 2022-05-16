package com.fatbook.fatbookapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.RecipeIngredient;

import java.util.List;

public class RecipeViewModel extends ViewModel {

    private final MutableLiveData<Recipe> selectedItem = new MutableLiveData<Recipe>();

    private final MutableLiveData<List<RecipeIngredient>> ingredients = new MutableLiveData<>();

    public void selectRecipe(Recipe recipe) {
        selectedItem.setValue(recipe);
    }

    public LiveData<Recipe> getSelectedRecipe() {
        return selectedItem;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients.setValue(ingredients);
    }

    public LiveData<List<RecipeIngredient>> getIngredients() {
        return ingredients;
    }

    public void addIngredient(RecipeIngredient ingredient) {
        ingredients.getValue().add(ingredient);
    }
}
