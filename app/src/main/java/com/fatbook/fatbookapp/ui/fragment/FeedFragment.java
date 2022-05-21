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
import com.fatbook.fatbookapp.core.Ingredient;
import com.fatbook.fatbookapp.core.IngredientUnit;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.RecipeIngredient;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.FragmentFeedBinding;
import com.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener;
import com.fatbook.fatbookapp.ui.listeners.OnRecipeRevertDeleteListener;
import com.fatbook.fatbookapp.ui.adapters.RecipeAdapter;
import com.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;
import com.fatbook.fatbookapp.ui.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.java.Log;

@Log
public class FeedFragment extends Fragment implements OnRecipeClickListener, OnRecipeRevertDeleteListener {

    private FragmentFeedBinding binding;

    private List<Recipe> feedRecipeList;

    private RecipeViewModel recipeViewModel;

    private UserViewModel userViewModel;

    private User user;

    private RecipeAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        feedRecipeList = new ArrayList<>();

        userViewModel.getUser().observe(getViewLifecycleOwner(), userUpdated -> {
            user = userUpdated;
            loadData();
        });

        binding.swipeRefreshBookmarks.setColorSchemeColors(getResources().getColor(R.color.color_pink_a200));

        binding.swipeRefreshBookmarks.setOnRefreshListener(() -> {
            loadData();
            binding.swipeRefreshBookmarks.setRefreshing(false);
        });

        userViewModel.getFeedRecipeList().observe(getViewLifecycleOwner(), recipeList -> {
            //TODO add loader
            feedRecipeList = recipeList;
            adapter.setData(feedRecipeList, user);
            adapter.notifyDataSetChanged();
        });
        setupAdapter();
    }

    private void setupAdapter() {
        RecyclerView recyclerView = binding.rvFeed;
        adapter = new RecipeAdapter(binding.getRoot().getContext(), feedRecipeList, user);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void loadData() {
        loadFakeData();
//        RetrofitFactory.apiServiceClient().getFeedRecipeList().enqueue(new Callback<List<Recipe>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
//                userViewModel.setFeedRecipeList(response.body());
//                log.log(Level.INFO, "feed data load: SUCCESS");
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
//                userViewModel.setFeedRecipeList(new ArrayList<>());
//                log.log(Level.INFO, "feed data load: FAILED");
//            }
//        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFeedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onRecipeClick(int position) {
        Recipe recipe = feedRecipeList.get(position);
        recipeViewModel.setRecipe(recipe);
        NavHostFragment.findNavController(this).navigate(R.id.action_go_to_recipe_view);
    }

    @Override
    public void onBookmarksClick(Recipe recipe, boolean add) {
        if (add) {
            user.getRecipesBookmarked().add(recipe);
        } else {
            user.getRecipesBookmarked().remove(recipe);
        }
        userViewModel.setUser(user);
        System.out.println();
        //TODO api
    }

    @Override
    public void onForkClicked(Recipe recipe, boolean fork) {
        if (fork) {
            user.getRecipesForked().add(recipe);
        } else {
            user.getRecipesForked().remove(recipe);
        }
        userViewModel.setUser(user);
        System.out.println();
        //TODO api
    }

    @Override
    public void onRecipeRevertDeleteClick(Recipe recipe) {
        //TODO revert delete recipe
        /**
         * post recipe again
         */
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadFakeData() {
        List<Recipe> list = new ArrayList<>();
        getFakeRecipeList(list);
//        getFakeRecipeList(list);
//        getFakeRecipeList(list);
//        getFakeRecipeList(list);
        userViewModel.setFeedRecipeList(list);
    }

    private void getFakeRecipeList(List<Recipe> recipes) {
        List<RecipeIngredient> ingredientList = new ArrayList<>();
        ingredientList.add(new RecipeIngredient(1L, null, new Ingredient(1L, "potato"), IngredientUnit.PCS, 1.0));
        ingredientList.add(new RecipeIngredient(1L, null, new Ingredient(2L, "milk"), IngredientUnit.ML, 500.0));
        ingredientList.add(new RecipeIngredient(1L, null, new Ingredient(3L, "eggs"), IngredientUnit.PCS, 2.0));
        ingredientList.add(new RecipeIngredient(1L, null, new Ingredient(4L, "bread"), IngredientUnit.PCS, 3.0));
        ingredientList.add(new RecipeIngredient(1L, null, new Ingredient(5L, "cheese"), IngredientUnit.GRAM, 250.0));

        User u1 = new User();
        u1.setLogin("u1");
        User u2 = new User();
        u2.setLogin("u2");
        User u3 = new User();
        u3.setLogin("u3");
        User u4 = new User();
        u4.setLogin("u4");

        recipes.add(new Recipe(1L, "PotatoChips", getResources().getString(R.string.text_full_recipe_instruction), u1.getLogin(), ingredientList,
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----5.jpg", 1339));
        recipes.add(new Recipe(2L, "Potato", getResources().getString(R.string.text_full_recipe_instruction), user.getLogin(), ingredientList,
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----2.jpg", 21345));
        recipes.add(new Recipe(3L, "fried PotatoChips", getResources().getString(R.string.text_full_recipe_instruction), u2.getLogin(), ingredientList,
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----1.jpg", 0));
        recipes.add(new Recipe(4L, "creamy Potato", getResources().getString(R.string.text_full_recipe_instruction), user.getLogin(), ingredientList,
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----3.jpg", 8));
        recipes.add(new Recipe(5L, "Potatoes with kotletki", getResources().getString(R.string.text_full_recipe_instruction), u3.getLogin(), ingredientList,
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----6.jpg", 133349));
        recipes.add(new Recipe(6L, "Potato so smetanka", getResources().getString(R.string.text_full_recipe_instruction), u4.getLogin(), ingredientList,
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----4.jpg", 324));
    }
}
