package com.fatbook.fatbookapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.core.Ingredient;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.databinding.FragmentAddIngredientBinding;
import com.fatbook.fatbookapp.databinding.FragmentRecipeViewBinding;
import com.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;

import java.util.ArrayList;
import java.util.List;

public class RecipeAddIngredientFragment extends Fragment {

    private FragmentAddIngredientBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddIngredientBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private List<Ingredient> loadFakeData() {
        List<Ingredient> fakeList = new ArrayList<>();
        fillFakeList(fakeList);
        return fakeList;
    }

    private void fillFakeList(List<Ingredient> ingredients) {
        ingredients.add(new Ingredient(1L, "potato", "qwer", 256));
        ingredients.add(new Ingredient(2L, "potato2", "qweqe", 62));
        ingredients.add(new Ingredient(3L, "potato3", "qweeeer", 101));
        ingredients.add(new Ingredient(4L, "milk", "r", 60));
        ingredients.add(new Ingredient(5L, "juice", "eeeeee", 45));
        ingredients.add(new Ingredient(6L, "tea", "kkk", 1));
        ingredients.add(new Ingredient(7L, "coffee", "apb", 2));
        ingredients.add(new Ingredient(8L, "banana", "pppp", 98));
        ingredients.add(new Ingredient(9L, "tomato", "rat", 28));
        ingredients.add(new Ingredient(10L, "apple", "swift", 56));
        ingredients.add(new Ingredient(1L, "potato", "qwer", 256));
        ingredients.add(new Ingredient(2L, "potato2", "qweqe", 62));
        ingredients.add(new Ingredient(3L, "potato3", "qweeeer", 101));
        ingredients.add(new Ingredient(4L, "milk", "r", 60));
        ingredients.add(new Ingredient(5L, "juice", "eeeeee", 45));
        ingredients.add(new Ingredient(6L, "tea", "kkk", 1));
        ingredients.add(new Ingredient(7L, "coffee", "apb", 2));
        ingredients.add(new Ingredient(8L, "banana", "pppp", 98));
        ingredients.add(new Ingredient(9L, "tomato", "rat", 28));
        ingredients.add(new Ingredient(10L, "apple", "swift", 56));
        ingredients.add(new Ingredient(1L, "potato", "qwer", 256));
        ingredients.add(new Ingredient(2L, "potato2", "qweqe", 62));
        ingredients.add(new Ingredient(3L, "potato3", "qweeeer", 101));
        ingredients.add(new Ingredient(4L, "milk", "r", 60));
        ingredients.add(new Ingredient(5L, "juice", "eeeeee", 45));
        ingredients.add(new Ingredient(6L, "tea", "kkk", 1));
        ingredients.add(new Ingredient(7L, "coffee", "apb", 2));
        ingredients.add(new Ingredient(8L, "banana", "pppp", 98));
        ingredients.add(new Ingredient(9L, "tomato", "rat", 28));
        ingredients.add(new Ingredient(10L, "apple", "swift", 56));
    }
}
