package com.fatbook.fatbookapp.ui.fragment.ingredients;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Ingredient;
import com.fatbook.fatbookapp.databinding.FragmentBookmarksBinding;
import com.fatbook.fatbookapp.databinding.FragmentIngredientsBinding;
import com.fatbook.fatbookapp.ui.fragment.bookmarks.BookmarksViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class IngredientsFragment extends Fragment {

    private FragmentIngredientsBinding binding;

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
            Toast.makeText(getContext(), "Add new ingredient", Toast.LENGTH_SHORT).show();
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
        ingredients.add(new Ingredient(1L, "potato", "qwer" ,256 ));
        ingredients.add(new Ingredient(2L, "potato2", "qweqe" ,62 ));
        ingredients.add(new Ingredient(3L, "potato3", "qweeeer" ,101 ));
        ingredients.add(new Ingredient(4L, "milk", "r" ,60 ));
        ingredients.add(new Ingredient(5L, "juice", "eeeeee" ,45 ));
        ingredients.add(new Ingredient(6L, "tea", "kkk" ,1 ));
        ingredients.add(new Ingredient(7L, "coffee", "apb" ,2 ));
        ingredients.add(new Ingredient(8L, "banana", "pppp" ,98 ));
        ingredients.add(new Ingredient(9L, "tomato", "rat" ,28 ));
        ingredients.add(new Ingredient(10L, "apple", "swift" ,56 ));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
