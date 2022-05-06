package com.fatbook.fatbookapp.ui.fragment.ingredients;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Ingredient;
import com.fatbook.fatbookapp.databinding.FragmentIngredientsBinding;
import com.fatbook.fatbookapp.ui.adapters.IngredientAdapter;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class IngredientsFragment extends Fragment {

    private FragmentIngredientsBinding binding;

    private Ingredient ingredientToAdd;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        IngredientsViewModel viewModel = new ViewModelProvider(this).get(IngredientsViewModel.class);

        binding = FragmentIngredientsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fabIngredientsAdd.setOnClickListener(view1 -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Add ingredient");

            final EditText editTextName = new EditText(getContext());
            editTextName.setSingleLine();

            FrameLayout container = new FrameLayout(getContext());
            FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            params.rightMargin= getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            editTextName.setLayoutParams(params);
            container.addView(editTextName);

            alert.setView(container);

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String name = editTextName.getText().toString();
                    if (StringUtils.isNotEmpty(name)) {
                        ingredientToAdd = new Ingredient();
                        ingredientToAdd.setName(name);
                        //TODO call api POST new ingredient
                    }
                    Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
                }
            });
            alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            alert.show();
        });

        List<Ingredient> ingredients = new ArrayList<>();

        getIngredientList(ingredients);
        getIngredientList(ingredients);
        getIngredientList(ingredients);
        getIngredientList(ingredients);

        RecyclerView rv = binding.rvIngredients;

        IngredientAdapter adapter = new IngredientAdapter(binding.getRoot().getContext(), ingredients);

        rv.setAdapter(adapter);
    }

    private void getIngredientList(List<Ingredient> ingredients) {
        ingredients.add(new Ingredient(1L, "potato", "qwer", 256));
        ingredients.add(new Ingredient(2L, "potato2", "qweqe", 62));
        ingredients.add(new Ingredient(3L, "potato3", "qweeeer", 101));
        ingredients.add(new Ingredient(4L, "milk", "r", 60));
        ingredients.add(new Ingredient(5L, "juice", "eeeeee", 45));
        ingredients.add(new Ingredient(6L, "tea", "kkk", 1));
        ingredients.add(new Ingredient(7L, "coffee", "apb", 2));
        ingredients.add(new Ingredient(8L, "banana", "pppp", 98));
        ingredients.add(new Ingredient(9L, "tomato", "rat", 28));
        ingredients.add(new Ingredient(10L, "apple", "swift", 56));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
