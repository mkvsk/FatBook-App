package com.fatbook.fatbookapp.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.fatbook.fatbookapp.ui.OnRecipeRevertDeleteListener;
import com.fatbook.fatbookapp.ui.adapters.ViewRecipeIngredientAdapter;
import com.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;
import com.fatbook.fatbookapp.ui.viewmodel.UserViewModel;
import com.fatbook.fatbookapp.util.RecipeUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class RecipeViewFragment extends Fragment {

    private FragmentRecipeViewBinding binding;

    private Recipe recipe;

    private User user;

    private RecipeViewModel recipeViewModel;

    private UserViewModel userViewModel;

    private boolean isEditModEnabled;

    private ViewRecipeIngredientAdapter adapter;

    private OnRecipeRevertDeleteListener onRecipeRevertDeleteListener;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                            break;
                        case RecipeUtils.TAG_FORK_UNCHECKED:
                            toggleForks(true);
                            user.getRecipesForked().add(recipe);
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
        setupMenu();
    }

    private void showDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("A")
                .setMessage("B")
                .setPositiveButton(getResources().getString(R.string.alert_dialog_btn_ok), (dialogInterface, i) -> {
                    RecipeUtils.deleteRecipe(recipe);
                    onConfirmDeleteRecipeClick();
                })
                .setNegativeButton(getResources().getString(R.string.alert_dialog_btn_cancel), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void onConfirmDeleteRecipeClick() {
        Snackbar.make(requireActivity().findViewById(R.id.container), "Recipe deleted", Snackbar.LENGTH_SHORT)
                .setAnchorView(requireActivity().findViewById(R.id.bottom_navigation))
                .setAction(R.string.undo_string, view -> {
                    onRecipeRevertDeleteListener.onRecipeRevertDeleteClick(recipe);
                })
                .show();
        closeFragment();
    }

    private void closeFragment() {
        NavHostFragment.findNavController(this).popBackStack();
    }

    private void setupMenu() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.setSupportActionBar(binding.toolbarViewRecipe);
        binding.toolbarViewRecipe.setNavigationOnClickListener(view -> {
            NavHostFragment.findNavController(this).popBackStack();
        });
        if (recipe.getAuthor().equals(userViewModel.getUser().getValue())) {
            setHasOptionsMenu(true);
            binding.kuzyaRecipeView.setVisibility(View.GONE);
        } else {
            setHasOptionsMenu(false);
            binding.kuzyaRecipeView.setVisibility(View.VISIBLE);
        }
        binding.editTextFullRecipeName.setEnabled(false);
        binding.editTextFullRecipeDescription.setEnabled(false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.recipe_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_recipe_edit:
                toggleEditMode(true);
                return true;
            case R.id.menu_recipe_save:
                toggleEditMode(false);
                confirmEdit();
                return true;
            case R.id.menu_recipe_cancel:
                toggleEditMode(false);
                cancelEdit();
                return true;
            case R.id.menu_recipe_delete:
                showDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toggleEditMode(boolean allow) {
        isEditModEnabled = allow;
        binding.editTextFullRecipeName.setEnabled(allow);
        binding.editTextFullRecipeDescription.setEnabled(allow);
        changeMenuItemsVisibility(!allow, allow, allow, allow);
        adapter.setEditMode(allow);
        adapter.notifyDataSetChanged();

        if (allow) {
            binding.editTextFullRecipeName.setBackgroundResource(R.drawable.edit_mode_bgr);
            binding.editTextFullRecipeDescription.setBackgroundResource(R.drawable.edit_mode_bgr);
        } else {
            binding.editTextFullRecipeName.setBackgroundResource(R.drawable.round_corner_rect_white);
            binding.editTextFullRecipeDescription.setBackgroundResource(R.drawable.round_corner_rect_white);
        }
    }

    private void confirmEdit() {
        recipe.setName(binding.editTextFullRecipeName.getText().toString());
        recipe.setDescription(binding.editTextFullRecipeDescription.getText().toString());
        //TODO ingredient list save
        recipeViewModel.setRecipe(recipe);
        saveRecipe();
    }

    private void saveRecipe() {
        RecipeUtils.saveRecipe(recipe);
    }

    private void cancelEdit() {
        toggleEditMode(false);
        showData();
    }

    private void changeMenuItemsVisibility(boolean edit, boolean save, boolean cancel, boolean delete) {
        binding.toolbarViewRecipe.getMenu().findItem(R.id.menu_recipe_edit).setVisible(edit);
        binding.toolbarViewRecipe.getMenu().findItem(R.id.menu_recipe_save).setVisible(save);
        binding.toolbarViewRecipe.getMenu().findItem(R.id.menu_recipe_cancel).setVisible(cancel);
        binding.toolbarViewRecipe.getMenu().findItem(R.id.menu_recipe_delete).setVisible(delete);
    }

    private void loadData() {
        recipe = recipeViewModel.getSelectedRecipe().getValue();
        user = userViewModel.getUser().getValue();
        if (recipe == null) {
            recipe = new Recipe();
        }
    }

    private void showData() {
        binding.editTextFullRecipeName.setText(recipe.getName());
        Glide
                .with(requireContext())
                .load(recipe.getImage())
                .into(binding.imageViewFullRecipeImage);
        binding.textViewFullRecipeUsername.setText(recipe.getAuthor().getLogin());
        String forks = Integer.toString(recipe.getForks());
        binding.textViewFullRecipeForksQuantity.setText(forks);
        binding.editTextFullRecipeDescription.setText(recipe.getDescription());
        toggleBookmarks(user.getRecipesBookmarked().contains(recipe));
        toggleForks(user.getRecipesForked().contains(recipe));
        setupAdapter(recipe.getIngredients());
    }

    private void setupAdapter(List<RecipeIngredient> ingredients) {
        RecyclerView recyclerView = binding.rvRecipeViewIngredients;
        adapter = new ViewRecipeIngredientAdapter(binding.getRoot().getContext(), ingredients);
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
