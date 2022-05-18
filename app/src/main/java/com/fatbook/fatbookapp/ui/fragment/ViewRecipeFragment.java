package com.fatbook.fatbookapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.RecipeIngredient;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.FragmentRecipeViewBinding;
import com.fatbook.fatbookapp.ui.adapters.ViewRecipeIngredientAdapter;
import com.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;
import com.fatbook.fatbookapp.ui.viewmodel.UserViewModel;
import com.fatbook.fatbookapp.util.RecipeUtils;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class ViewRecipeFragment extends Fragment {

    private FragmentRecipeViewBinding binding;

    private Recipe recipe;

    private User user;

    private RecipeViewModel recipeViewModel;

    private UserViewModel userViewModel;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_view_recipe);
        toolbar.setNavigationOnClickListener(view1 -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        recipeViewModel.getSelectedRecipe().observe(getViewLifecycleOwner(), recipe -> {
            this.recipe = recipe;
        });

        loadData();
        showData();

        binding.imageViewFullRecipeFork.setOnClickListener(view1 -> {
                    String tag = (String) binding.imageViewFullRecipeFork.getTag();
                    switch (tag) {
                        case RecipeUtils.TAG_FORK_CHECKED:
                            toggleForks(false);
                            user.getRecipesForked().remove(recipe);
//                            listener.onForkClicked(position, false);
                            break;
                        case RecipeUtils.TAG_FORK_UNCHECKED:
                            toggleForks(true);
                            user.getRecipesForked().add(recipe);
//                            listener.onForkClicked(position, true);
                            break;
                    }
                }
        );

        binding.imageViewFullRecipeIconBookmarks.setOnClickListener(view1 -> {
                    String tag = (String) binding.imageViewFullRecipeIconBookmarks.getTag();
                    switch (tag) {
                        case RecipeUtils.TAG_BOOKMARKS_CHECKED:
                            toggleBookmarks(false);
                            user.getRecipesBookmarked().remove(recipe);
                            break;
                        case RecipeUtils.TAB_BOOKMARKS_UNCHECKED:
                            toggleBookmarks(true);
                            user.getRecipesBookmarked().add(recipe);
                            break;
                    }
                }
        );
    }

    private void loadData() {
        recipe = recipeViewModel.getSelectedRecipe().getValue();
        user = userViewModel.getUser().getValue();
        if (recipe == null) {
            recipe = new Recipe();
        }
    }

    private void showData() {
        binding.textViewFullRecipeName.setText(recipe.getName());
        Glide
                .with(requireContext())
                .load(recipe.getImage())
                .into(binding.imageViewFullRecipeImage);
        binding.textViewFullRecipeUsername.setText(recipe.getAuthor().getLogin());
        String forks = Integer.toString(recipe.getForks());
        binding.textViewFullRecipeForksQuantity.setText(forks);
        binding.textViewFullRecipeDescription.setText(recipe.getDescription());
        toggleBookmarks(user.getRecipesBookmarked().contains(recipe));
        toggleForks(user.getRecipesForked().contains(recipe));
        setupAdapter(recipe.getIngredients());
    }

    private void setupAdapter(List<RecipeIngredient> ingredients) {
        RecyclerView recyclerView = binding.rvRecipeViewIngredients;
        ViewRecipeIngredientAdapter adapter = new ViewRecipeIngredientAdapter(binding.getRoot().getContext(), ingredients);
        recyclerView.setAdapter(adapter);
    }

    private void toggleBookmarks(boolean check) {
        if (check) {
            binding.imageViewFullRecipeIconBookmarks.setImageResource(R.drawable.icon_bookmarks_checked);
            binding.imageViewFullRecipeIconBookmarks.setTag(RecipeUtils.TAG_BOOKMARKS_CHECKED);
        } else {
            binding.imageViewFullRecipeIconBookmarks.setImageResource(R.drawable.icon_bookmarks_unchecked);
            binding.imageViewFullRecipeIconBookmarks.setTag(RecipeUtils.TAB_BOOKMARKS_UNCHECKED);
        }
    }

    private void toggleForks(boolean selected) {
        if (selected) {
            binding.imageViewFullRecipeFork.setImageResource(R.drawable.icon_fork_checked);
            binding.imageViewFullRecipeFork.setTag(RecipeUtils.TAG_FORK_CHECKED);
        } else {
            binding.imageViewFullRecipeFork.setImageResource(R.drawable.icon_fork_unchecked);
            binding.imageViewFullRecipeFork.setTag(RecipeUtils.TAG_FORK_UNCHECKED);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipeViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
