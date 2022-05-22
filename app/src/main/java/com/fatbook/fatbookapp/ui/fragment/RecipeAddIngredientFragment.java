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

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Ingredient;
import com.fatbook.fatbookapp.core.IngredientUnit;
import com.fatbook.fatbookapp.core.RecipeIngredient;
import com.fatbook.fatbookapp.databinding.FragmentAddIngredientBinding;
import com.fatbook.fatbookapp.ui.adapters.RecipeAddIngredientAdapter;
import com.fatbook.fatbookapp.ui.listeners.OnAddIngredientItemClickListener;
import com.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RecipeAddIngredientFragment extends Fragment implements OnAddIngredientItemClickListener {

    private FragmentAddIngredientBinding binding;

    private RecipeAddIngredientAdapter adapter;

    private RecipeViewModel recipeViewModel;

    private Ingredient selectedIngredient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddIngredientBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);

        binding.btnAddIngredientToRecipe.setEnabled(false);
        binding.textViewSelectedIngredient.setTextColor(getResources().getColor(R.color.color_blue_grey_200));

        binding.toolbarAddIngredientToRecipe.setNavigationOnClickListener(view1 -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

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

        setupAdapter();
        setupUnitPicker();
    }

    private void setupUnitPicker() {
        String[] unitData = new String[]{
                IngredientUnit.TABLE_SPOON.getMultiplyNaming(),
                IngredientUnit.PCS.getMultiplyNaming(),
                IngredientUnit.GRAM.getMultiplyNaming(),
                IngredientUnit.ML.getMultiplyNaming(),
                IngredientUnit.TEA_SPOON.getMultiplyNaming()
        };
        binding.pickerIngredientUnit.setMinValue(0);
        binding.pickerIngredientUnit.setMaxValue(unitData.length - 1);
        binding.pickerIngredientUnit.setDisplayedValues(unitData);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupAdapter() {
        RecyclerView rv = binding.rvAddIngredientToRecipe;
        adapter = new RecipeAddIngredientAdapter(binding.getRoot().getContext(), loadFakeData());
        adapter.setClickListener(this);
        rv.setAdapter(adapter);
    }

    private List<Ingredient> loadFakeData() {
        List<Ingredient> fakeList = new ArrayList<>();
        fillFakeList(fakeList);
        return fakeList;
    }

    private void fillFakeList(List<Ingredient> ingredients) {
        ingredients.add(new Ingredient(1L, "potato"));
        ingredients.add(new Ingredient(2L, "potato2"));
        ingredients.add(new Ingredient(3L, "potato3"));
        ingredients.add(new Ingredient(4L, "milk"));
        ingredients.add(new Ingredient(5L, "juice"));
        ingredients.add(new Ingredient(6L, "tea"));
        ingredients.add(new Ingredient(7L, "coffee"));
        ingredients.add(new Ingredient(8L, "banana"));
        ingredients.add(new Ingredient(9L, "tomato"));
        ingredients.add(new Ingredient(10L, "apple"));
        ingredients.add(new Ingredient(1L, "potato"));
        ingredients.add(new Ingredient(2L, "potato2"));
        ingredients.add(new Ingredient(3L, "potato3"));
        ingredients.add(new Ingredient(4L, "milk"));
        ingredients.add(new Ingredient(5L, "juice"));
        ingredients.add(new Ingredient(6L, "tea"));
        ingredients.add(new Ingredient(7L, "coffee"));
        ingredients.add(new Ingredient(8L, "banana"));
        ingredients.add(new Ingredient(9L, "tomato"));
        ingredients.add(new Ingredient(10L, "apple"));
        ingredients.add(new Ingredient(1L, "potato"));
        ingredients.add(new Ingredient(2L, "potato2"));
        ingredients.add(new Ingredient(3L, "potato3"));
        ingredients.add(new Ingredient(4L, "milk"));
        ingredients.add(new Ingredient(5L, "juice"));
        ingredients.add(new Ingredient(6L, "tea"));
        ingredients.add(new Ingredient(7L, "coffee"));
        ingredients.add(new Ingredient(8L, "banana"));
        ingredients.add(new Ingredient(9L, "tomato"));
        ingredients.add(new Ingredient(10L, "apple"));
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
}
