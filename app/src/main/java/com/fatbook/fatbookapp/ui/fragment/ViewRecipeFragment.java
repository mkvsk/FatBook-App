package com.fatbook.fatbookapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.databinding.FragmentRecipeViewBinding;
import com.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class ViewRecipeFragment extends Fragment {

    private FragmentRecipeViewBinding binding;

    private Recipe recipe;

    private RecipeViewModel recipeViewModel;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recipeViewModel = new ViewModelProvider(getActivity()).get(RecipeViewModel.class);

        recipeViewModel.getSelectedRecipe().observe(getViewLifecycleOwner(), recipe -> {
            this.recipe = recipe;
        });

        recipe = recipeViewModel.getSelectedRecipe().getValue();

        binding.textViewFullRecipeName.setText(recipe.getName());
        Glide
                .with(getContext())
                .load(recipe.getImage())
                .into(binding.imageViewFullRecipeImage);
        binding.textViewFullRecipeUsername.setText(recipe.getAuthor().getLogin());
        String forks = Integer.toString(recipe.getForks());
        binding.textViewFullRecipeForksQuantity.setText(forks);
        binding.textViewFullRecipeDescription.setText(recipe.getDescription());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipeViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        MaterialToolbar toolbar = root.findViewById(R.id.toolbar_view_recipe);
        toolbar.setNavigationOnClickListener(view -> {
            NavHostFragment.findNavController(this).navigateUp();
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
