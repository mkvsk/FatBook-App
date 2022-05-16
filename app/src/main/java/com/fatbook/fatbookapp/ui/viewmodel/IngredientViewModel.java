package com.fatbook.fatbookapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fatbook.fatbookapp.core.Ingredient;

import java.util.List;

public class IngredientViewModel extends ViewModel {

    private final MutableLiveData<List<Ingredient>> ingredientList = new MutableLiveData<>();

    private final MutableLiveData<Boolean> refreshFailed = new MutableLiveData<>();

    public void setIngredientList(List<Ingredient> ingredientList) {
        this.ingredientList.setValue(ingredientList);
    }

    public LiveData<List<Ingredient>> getIngredientList() {
        return ingredientList;
    }

    public void setRefreshFailed(boolean value) {
        refreshFailed.setValue(value);
    }

    public MutableLiveData<Boolean> getRefreshFailed() {
        return refreshFailed;
    }
}
