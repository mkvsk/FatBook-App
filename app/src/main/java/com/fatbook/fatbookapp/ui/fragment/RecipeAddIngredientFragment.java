package com.fatbook.fatbookapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Ingredient;
import com.fatbook.fatbookapp.databinding.FragmentAddIngredientBinding;
import com.fatbook.fatbookapp.ui.adapters.IngredientAdapter;
import com.fatbook.fatbookapp.ui.adapters.RecipeAddIngredientAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class RecipeAddIngredientFragment extends Fragment {

    private FragmentAddIngredientBinding binding;

    private RecipeAddIngredientAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddIngredientBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_add_ingredient_to_recipe);
        toolbar.setNavigationOnClickListener(view1 -> {
            NavHostFragment.findNavController(this).navigateUp();
        });
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        binding.btnAddIngredientToRecipe.setOnClickListener(view1 -> {
            NavHostFragment.findNavController(this).navigateUp();
        });
        setupAdapter();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupAdapter() {
        RecyclerView rv = binding.rvAddIngredientToRecipe;
        adapter = new RecipeAddIngredientAdapter(binding.getRoot().getContext(), loadFakeData());
        rv.setAdapter(adapter);
    }

    private List<Ingredient> loadFakeData() {
        List<Ingredient> fakeList = new ArrayList<>();
        fillFakeList(fakeList);
        return fakeList;
    }

    private void fillFakeList(List<Ingredient> ingredients) {
        ingredients.add(new Ingredient(1L, "potato", "qwer", 256));
        ingredients.add(new Ingredient(2L, "butter", "qweqe", 62));
        ingredients.add(new Ingredient(3L, "cheese", "qweeeer", 101));
        ingredients.add(new Ingredient(4L, "condensed milk", "r", 60));
        ingredients.add(new Ingredient(5L, "juice", "eeeeee", 45));
        ingredients.add(new Ingredient(6L, "tea", "kkk", 1));
        ingredients.add(new Ingredient(7L, "coffee", "apb", 2));
        ingredients.add(new Ingredient(8L, "banana", "pppp", 98));
        ingredients.add(new Ingredient(9L, "tomato", "rat", 28));
        ingredients.add(new Ingredient(10L, "apple", "swift", 56));
        ingredients.add(new Ingredient(1L, "cottage cheese", "qwer", 256));
        ingredients.add(new Ingredient(2L, "eggs", "qweqe", 62));
        ingredients.add(new Ingredient(3L, "milk", "qweeeer", 101));
        ingredients.add(new Ingredient(4L, "yogurt", "r", 60));
        ingredients.add(new Ingredient(5L, "bacon", "eeeeee", 45));
        ingredients.add(new Ingredient(6L, "beef", "kkk", 1));
        ingredients.add(new Ingredient(7L, "chicken", "apb", 2));
        ingredients.add(new Ingredient(8L, "duck", "pppp", 98));
        ingredients.add(new Ingredient(9L, "ham", "rat", 28));
        ingredients.add(new Ingredient(10L, "lamb", "swift", 56));
        ingredients.add(new Ingredient(1L, "liver", "qwer", 256));
        ingredients.add(new Ingredient(2L, "meat", "qweqe", 62));
        ingredients.add(new Ingredient(3L, "pork", "qweeeer", 101));
        ingredients.add(new Ingredient(4L, "sausage", "r", 60));
        ingredients.add(new Ingredient(5L, "turkey", "eeeeee", 45));
        ingredients.add(new Ingredient(6L, "asparagus", "kkk", 1));
        ingredients.add(new Ingredient(7L, "avocado", "apb", 2));
        ingredients.add(new Ingredient(8L, "beans", "pppp", 98));
        ingredients.add(new Ingredient(9L, "broccoli", "rat", 28));
        ingredients.add(new Ingredient(10L, "cabbage", "swift", 56));
    }
}
