package com.fatbook.fatbookapp.ui.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fatbook.fatbookapp.MainActivity;
import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Ingredient;
import com.fatbook.fatbookapp.core.IngredientUnit;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.RecipeIngredient;
import com.fatbook.fatbookapp.core.Role;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.FragmentFeedBinding;
import com.fatbook.fatbookapp.ui.OnRecipeClickListener;
import com.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;
import com.fatbook.fatbookapp.ui.adapters.RecipeAdapter;
import com.fatbook.fatbookapp.ui.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedFragment extends Fragment implements OnRecipeClickListener {

    private FragmentFeedBinding binding;

    private List<Recipe> recipes;

    private RecipeViewModel recipeViewModel;

    private UserViewModel userViewModel;

    private User user;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        user = userViewModel.getUser().getValue();

        binding.swipeRefreshBookmarks.setColorSchemeColors(
                getResources().getColor(R.color.color_pink_a200));

        binding.swipeRefreshBookmarks.setOnRefreshListener(() -> {
            Toast.makeText(binding.getRoot().getContext(), "refreshed", Toast.LENGTH_SHORT).show();
            binding.swipeRefreshBookmarks.setRefreshing(false);
        });

        recipes = new ArrayList<>();

        getRecipeList(recipes);
        getRecipeList(recipes);
        getRecipeList(recipes);
        getRecipeList(recipes);

        RecyclerView recyclerView = binding.rvFeed;

        RecipeAdapter adapter = new RecipeAdapter(binding.getRoot().getContext(), recipes);
        adapter.setClickListener(this);

        recyclerView.setAdapter(adapter);
    }

    private void getRecipeList(List<Recipe> recipes) {
        List<RecipeIngredient> ingredientList = new ArrayList<>();
        ingredientList.add(new RecipeIngredient(1L, null, new Ingredient(1L, "potato"), IngredientUnit.PCS, 1.0));
        ingredientList.add(new RecipeIngredient(1L, null, new Ingredient(2L, "milk"), IngredientUnit.ML, 500.0));
        ingredientList.add(new RecipeIngredient(1L, null, new Ingredient(3L, "eggs"), IngredientUnit.PCS, 2.0));
        ingredientList.add(new RecipeIngredient(1L, null, new Ingredient(4L, "bread"), IngredientUnit.PCS, 3.0));
        ingredientList.add(new RecipeIngredient(1L, null, new Ingredient(5L, "cheese"), IngredientUnit.GRAM, 250.0));

        recipes.add(new Recipe(1L, "PotatoChips", getResources().getString(R.string.text_full_recipe_instruction), user, ingredientList,
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----5.jpg", 1339));
        recipes.add(new Recipe(2L, "Potato", getResources().getString(R.string.text_full_recipe_instruction), user, ingredientList,
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----2.jpg", 21345));
        recipes.add(new Recipe(3L, "fried PotatoChips", getResources().getString(R.string.text_full_recipe_instruction), user, ingredientList,
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----1.jpg", 0));
        recipes.add(new Recipe(4L, "creamy Potato", getResources().getString(R.string.text_full_recipe_instruction), user, ingredientList,
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----3.jpg", 8));
        recipes.add(new Recipe(5L, "Potatoes with kotletki", getResources().getString(R.string.text_full_recipe_instruction), user, ingredientList,
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----6.jpg", 133349));
        recipes.add(new Recipe(6L, "Potato so smetanka", getResources().getString(R.string.text_full_recipe_instruction), user, ingredientList,
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----4.jpg", 324));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFeedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRecipeClick(int position) {
        Recipe recipe = recipes.get(position);
        recipeViewModel.selectRecipe(recipe);
        NavHostFragment.findNavController(this).navigate(R.id.navigation_view_recipe);
    }

    @Override
    public void onBookmarksClick(int position, boolean add) {
        //TODO api
    }

    @Override
    public void onForkClicked(int position, boolean fork) {
        //TODO api
    }
}
