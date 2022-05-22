package com.fatbook.fatbookapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.RecipeIngredient;

import java.util.List;

public class RecipeViewModel extends ViewModel {

    private final MutableLiveData<Recipe> recipe = new MutableLiveData<Recipe>();

    private final MutableLiveData<List<RecipeIngredient>> selectedRecipeIngredients = new MutableLiveData<>();

    private final MutableLiveData<RecipeIngredient> selectedRecipeIngredient = new MutableLiveData<>();

    public void setRecipe(Recipe recipe) {
        this.recipe.setValue(recipe);
    }

    public LiveData<Recipe> getSelectedRecipe() {
        return recipe;
    }

    public void setSelectedRecipeIngredients(List<RecipeIngredient> selectedRecipeIngredients) {
        this.selectedRecipeIngredients.setValue(selectedRecipeIngredients);
    }

    public LiveData<List<RecipeIngredient>> getSelectedRecipeIngredients() {
        return selectedRecipeIngredients;
    }

    public void addIngredient(RecipeIngredient ingredient) {
        selectedRecipeIngredients.getValue().add(ingredient);
    }

    public void setSelectedRecipeIngredient(RecipeIngredient ingredient) {
        this.selectedRecipeIngredient.setValue(ingredient);
    }

    public LiveData<RecipeIngredient> getSelectedRecipeIngredient() {
        return selectedRecipeIngredient;
    }
}
