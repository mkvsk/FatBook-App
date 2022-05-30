package com.fatbook.fatbookapp.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.Resource;
import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.RecipeIngredient;
import com.fatbook.fatbookapp.databinding.FragmentRecipeCreateBinding;
import com.fatbook.fatbookapp.retrofit.RetrofitFactory;
import com.fatbook.fatbookapp.ui.adapters.ViewRecipeIngredientAdapter;
import com.fatbook.fatbookapp.ui.listeners.OnRecipeViewDeleteIngredient;
import com.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;
import com.fatbook.fatbookapp.ui.viewmodel.UserViewModel;
import com.fatbook.fatbookapp.util.KeyboardActionUtil;
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
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

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

        binding.editTextRecipeAddTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                niceCheck();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.editTextRecipeAddDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                niceCheck();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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

    private void niceCheck() {
        if (StringUtils.isNotEmpty(binding.editTextRecipeAddTitle.toString())
                && StringUtils.isNotEmpty(binding.editTextRecipeAddDescription.toString())
                && !recipe.getIngredients().isEmpty()) {
            binding.buttonRecipeAddSave.setEnabled(true);
            binding.buttonRecipeAddSave.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.color_pink_a200));
        } else {
            binding.buttonRecipeAddSave.setEnabled(false);
            binding.buttonRecipeAddSave.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.color_blue_grey_200));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardActionUtil(binding.getRoot(), requireActivity(), binding.viewRecipeCreate).listenerForAdjustPan);
        if (recipeViewModel.getSelectedRecipeIngredient().getValue() != null) {
            recipe.getIngredients().add(recipeViewModel.getSelectedRecipeIngredient().getValue());
            adapter.setData(recipe.getIngredients());
            adapter.notifyDataSetChanged();
            recipeViewModel.setSelectedRecipeIngredient(null);
        }
        niceCheck();
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
        niceCheck();
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

    @Override
    public void onPause() {
        super.onPause();
        binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(new KeyboardActionUtil(binding.getRoot(), requireActivity(), binding.viewRecipeCreate).listenerForAdjustPan);
    }
}
