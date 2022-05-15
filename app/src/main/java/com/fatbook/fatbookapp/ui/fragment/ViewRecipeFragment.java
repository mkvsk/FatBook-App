package com.fatbook.fatbookapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.databinding.FragmentRecipeViewBinding;
import com.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;

public class ViewRecipeFragment extends Fragment {

    private FragmentRecipeViewBinding binding;

    private Recipe recipe;

    private RecipeViewModel recipeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRecipeViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recipeViewModel = new ViewModelProvider(getActivity()).get(RecipeViewModel.class);

        recipeViewModel.getSelectedRecipe().observe(getViewLifecycleOwner(), recipe -> {
            this.recipe = recipe;
        });

//        recipe = feedViewModel.getSelectedRecipe().getValue();

//        Serializable recipe = getArguments().getSerializable("recipe");

        return root;
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
}
