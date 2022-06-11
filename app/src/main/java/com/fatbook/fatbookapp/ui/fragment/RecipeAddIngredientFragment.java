package com.fatbook.fatbookapp.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Ingredient;
import com.fatbook.fatbookapp.core.IngredientUnit;
import com.fatbook.fatbookapp.core.RecipeIngredient;
import com.fatbook.fatbookapp.databinding.FragmentAddIngredientBinding;
import com.fatbook.fatbookapp.retrofit.RetrofitFactory;
import com.fatbook.fatbookapp.ui.adapters.AddIngredientToRecipeAdapter;
import com.fatbook.fatbookapp.ui.listeners.OnAddIngredientItemClickListener;
import com.fatbook.fatbookapp.ui.viewmodel.IngredientViewModel;
import com.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;
import com.fatbook.fatbookapp.util.KeyboardActionUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import lombok.extern.java.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Log
public class RecipeAddIngredientFragment extends Fragment implements OnAddIngredientItemClickListener {

    private FragmentAddIngredientBinding binding;

    private AddIngredientToRecipeAdapter adapter;

    private RecipeViewModel recipeViewModel;

    private IngredientViewModel ingredientViewModel;

    private Ingredient selectedIngredient;

    private List<Ingredient> ingredientList;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        ingredientViewModel = new ViewModelProvider(requireActivity()).get(IngredientViewModel.class);

        binding.btnAddIngredientToRecipe.setEnabled(false);
        binding.textViewSelectedIngredient.setTextColor(getResources().getColor(R.color.color_blue_grey_200));

        binding.toolbarAddIngredientToRecipe.setNavigationOnClickListener(view1 -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        binding.btnAddIngredientToRecipe.setOnClickListener(view1 -> {
            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.setIngredient(selectedIngredient);
            recipeIngredient.setQuantity(Double.parseDouble(binding.editTextIngredientQuantity.getText().toString()));
            recipeIngredient.setUnit(IngredientUnit.values()[binding.pickerIngredientUnit.getValue()]);
            recipeViewModel.setSelectedRecipeIngredient(recipeIngredient);
            NavHostFragment.findNavController(this).popBackStack();
        });

        binding.editTextIngredientQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                activateButtonSave();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        ingredientViewModel.getIngredientList().observe(getViewLifecycleOwner(), ingredients -> {
            ingredientList = ingredients;
            adapter.setData(ingredients);
            adapter.notifyDataSetChanged();
        });

        setupAdapter();
//        if (ingredientViewModel.getIngredientList().getValue() == null) {
            loadIngredients();
//        }
        setupUnitPicker();

        binding.editTextAddIngredientSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }

    private void filter(String text) {
        try {
            List<Ingredient> temp = new ArrayList<>();
            for (Ingredient i : ingredientList) {
                if (StringUtils.containsIgnoreCase(i.getName(), text)) {
                    temp.add(i);
                }
            }
            adapter.updateList(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadIngredients() {
        RetrofitFactory.apiServiceClient().getAllIngredients().enqueue(new Callback<List<Ingredient>>() {
            @Override
            public void onResponse(@NonNull Call<List<Ingredient>> call, @NonNull Response<List<Ingredient>> response) {
                ingredientViewModel.setIngredientList(response.body());
                log.log(Level.INFO, "ingredient list load: SUCCESS");
            }

            @Override
            public void onFailure(@NonNull Call<List<Ingredient>> call, @NonNull Throwable t) {
                log.log(Level.INFO, "ingredient list load: FAILED");
            }
        });
    }

    private void setupUnitPicker() {
        String[] unitData = new String[]{
                IngredientUnit.ML.getMultiplyNaming(requireContext()),
                IngredientUnit.PCS.getMultiplyNaming(requireContext()),
                IngredientUnit.GRAM.getMultiplyNaming(requireContext()),
                IngredientUnit.TEA_SPOON.getMultiplyNaming(requireContext()),
                IngredientUnit.TABLE_SPOON.getMultiplyNaming(requireContext())
        };
        binding.pickerIngredientUnit.setMinValue(0);
        binding.pickerIngredientUnit.setMaxValue(unitData.length - 1);
        binding.pickerIngredientUnit.setDisplayedValues(unitData);
    }

    private void setupAdapter() {
        RecyclerView rv = binding.rvAddIngredientToRecipe;
        adapter = new AddIngredientToRecipeAdapter(binding.getRoot().getContext(), new ArrayList<>());
        adapter.setClickListener(this);
        rv.setAdapter(adapter);
    }

    @Override
    public void onIngredientClick(int previousItem, int selectedItem, Ingredient ingredient) {
        selectedIngredient = ingredient;
        binding.textViewSelectedIngredient.setTextColor(getResources().getColor(R.color.color_pink_a200));
        binding.textViewSelectedIngredient.setText(ingredient.getName());
        adapter.notifyItemChanged(previousItem);
        adapter.notifyItemChanged(selectedItem);
        activateButtonSave();
    }

    private void activateButtonSave() {
        if (StringUtils.isEmpty(binding.editTextIngredientQuantity.getText().toString()) || selectedIngredient == null) {
            binding.btnAddIngredientToRecipe.setEnabled(false);
            binding.btnAddIngredientToRecipe.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.color_blue_grey_200));
        } else {
            binding.btnAddIngredientToRecipe.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.color_pink_a200));
            binding.btnAddIngredientToRecipe.setEnabled(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddIngredientBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardActionUtil(binding.getRoot(), requireActivity()).listenerForAdjustResize);
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(new KeyboardActionUtil(binding.getRoot(), requireActivity()).listenerForAdjustResize);
    }
}
