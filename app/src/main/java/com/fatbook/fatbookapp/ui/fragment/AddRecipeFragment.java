package com.fatbook.fatbookapp.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.IngredientUnit;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.RecipeIngredient;
import com.fatbook.fatbookapp.databinding.FragmentRecipeAddBinding;
import com.fatbook.fatbookapp.ui.adapters.AddRecipeAdapter;
import com.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;
import com.fatbook.fatbookapp.ui.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddRecipeFragment extends Fragment {

    private FragmentRecipeAddBinding binding;

    private UserViewModel userViewModel;

    private RecipeViewModel recipeViewModel;

    private Recipe recipe;

    private List<RecipeIngredient> ingredients;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRecipeAddBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recipe = new Recipe();
        ingredients = new ArrayList<>();

        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        recipeViewModel = new ViewModelProvider(getActivity()).get(RecipeViewModel.class);

        recipeViewModel.setIngredients(new ArrayList<>());

        binding.buttonRecipeAddSave.setOnClickListener(_view -> {

            recipe.setName(binding.editTextRecipeAddTitle.getText().toString());
            recipe.setDescription(binding.editTextRecipeAddDescription.getText().toString());
            recipe.setAuthor(userViewModel.getUser().getValue());
            recipe.setIngredients(Collections.emptyList());
            recipe.setImage("");
            recipe.setForks(0);
        });

        binding.buttonRecipeAddIngredientAdd.setOnClickListener(_view -> {
//            recipeViewModel.addIngredient();
        });

        setupAdapter();
    }

    private void setupAdapter() {
        RecyclerView recyclerView = binding.rvRecipeAddIngredients;
        AddRecipeAdapter adapter = new AddRecipeAdapter(binding.getRoot().getContext(), ingredients);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
