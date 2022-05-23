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

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.RecipeIngredient;
import com.fatbook.fatbookapp.databinding.FragmentRecipeCreateBinding;
import com.fatbook.fatbookapp.retrofit.RetrofitFactory;
import com.fatbook.fatbookapp.ui.adapters.ViewRecipeIngredientAdapter;
import com.fatbook.fatbookapp.ui.listeners.OnRecipeViewDeleteIngredient;
import com.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;
import com.fatbook.fatbookapp.ui.viewmodel.UserViewModel;
import com.fatbook.fatbookapp.util.RecipeUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import lombok.extern.java.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Log
public class RecipeCreateFragment extends Fragment implements OnRecipeViewDeleteIngredient {

    private FragmentRecipeCreateBinding binding;

    private UserViewModel userViewModel;

    private RecipeViewModel recipeViewModel;

    private Recipe recipe;

    private ViewRecipeIngredientAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);

        recipeViewModel.setSelectedRecipeIngredients(new ArrayList<>());

        binding.buttonRecipeAddSave.setOnClickListener(_view -> {
            saveRecipe();
        });

        binding.buttonRecipeAddIngredientAdd.setOnClickListener(_view -> {
            NavHostFragment.findNavController(this).navigate(R.id.navigation_add_ingredient);
        });

        setupAdapter();
    }

    private void saveRecipe() {
        recipe.setName(binding.editTextRecipeAddTitle.getText().toString());
        recipe.setDescription(binding.editTextRecipeAddDescription.getText().toString());
        recipe.setImage(StringUtils.EMPTY);
        recipe.setAuthor(userViewModel.getUser().getValue().getLogin());
        recipe.setCreateDate(RecipeUtils.regDateFormat.format(new Date()));

        RetrofitFactory.apiServiceClient().recipeCreate(recipe).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                log.log(Level.INFO, "recipe create SUCCESS");
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                log.log(Level.INFO, "recipe create FAILED");
            }
        });

        NavHostFragment.findNavController(this).popBackStack();
    }

    private void initRecipe() {
        recipe = new Recipe();
        recipe.setIngredients(new ArrayList<>());
        recipe.setForks(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recipeViewModel.getSelectedRecipeIngredient().getValue() != null) {
            recipe.getIngredients().add(recipeViewModel.getSelectedRecipeIngredient().getValue());
            adapter.setData(recipe.getIngredients());
            adapter.notifyDataSetChanged();
            recipeViewModel.setSelectedRecipeIngredient(null);
        }
    }

    private void setupAdapter() {
        if (recipe == null) {
            initRecipe();
        }
        RecyclerView recyclerView = binding.rvRecipeAddIngredients;
        adapter = new ViewRecipeIngredientAdapter(binding.getRoot().getContext(), recipe.getIngredients(), this);
        adapter.setEditMode(true);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onDeleteIngredientClick(RecipeIngredient recipeIngredient, int position) {
        recipe.getIngredients().remove(recipeIngredient);
        adapter.setData(recipe.getIngredients());
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipeCreateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
