package com.fatbook.fatbookapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.FragmentFeedBinding;
import com.fatbook.fatbookapp.retrofit.RetrofitFactory;
import com.fatbook.fatbookapp.ui.adapters.RecipeAdapter;
import com.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener;
import com.fatbook.fatbookapp.ui.listeners.OnRecipeRevertDeleteListener;
import com.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;
import com.fatbook.fatbookapp.ui.viewmodel.UserViewModel;
import com.fatbook.fatbookapp.util.KeyboardActionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import lombok.extern.java.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        feedRecipeList = new ArrayList<>();

        userViewModel.getUser().observe(getViewLifecycleOwner(), userUpdated -> {
            user = userUpdated;
            if (userViewModel.getFeedRecipeList().getValue() == null) {
                loadData();
            }
        });

        binding.swipeRefreshBookmarks.setColorSchemeColors(getResources().getColor(R.color.color_pink_a200));

        binding.swipeRefreshBookmarks.setOnRefreshListener(() -> {
            loadData();
            binding.swipeRefreshBookmarks.setRefreshing(false);
        });

        userViewModel.getFeedRecipeList().observe(getViewLifecycleOwner(), recipeList -> {
            feedRecipeList = recipeList;
            if (feedRecipeList == null) {
                feedRecipeList = new ArrayList<>();
            }
            adapter.setData(recipeList, user);
            adapter.notifyDataSetChanged();
        });
        setupAdapter();
        feedRecipeList = userViewModel.getFeedRecipeList().getValue();
    }

    private void setupAdapter() {
        RecyclerView recyclerView = binding.rvFeed;
        adapter = new RecipeAdapter(binding.getRoot().getContext(), feedRecipeList, user, this);
        recyclerView.setAdapter(adapter);
    }

    //TODO убрать костыль в api запросе
    private void loadData() {
        RetrofitFactory.apiServiceClient().getFeed(0L).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                userViewModel.setFeedRecipeList(response.body());
                log.log(Level.INFO, "feed data load: SUCCESS");
                if (response.body() != null) {
                    log.log(Level.INFO, "loaded " + response.body().size() + " recipes");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                userViewModel.setFeedRecipeList(new ArrayList<>());
                log.log(Level.INFO, "feed data load: FAILED");
            }
        });
    }

    @Override
    public void onRecipeClick(int position) {
        Recipe recipe = feedRecipeList.get(position);
        recipeViewModel.setSelectedRecipe(recipe);
        NavHostFragment.findNavController(this).navigate(R.id.action_go_to_recipe_view);
    }

    @Override
    public void onBookmarksClick(Recipe recipe, boolean bookmark, int position) {
        recipeBookmarked(recipe, bookmark, position);
    }

    private void recipeBookmarked(Recipe recipe, boolean bookmark, int position) {
        RetrofitFactory.apiServiceClient().recipeBookmarked(user.getPid(), recipe.getPid(), bookmark).enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(@NonNull Call<Recipe> call, @NonNull Response<Recipe> response) {
                log.log(Level.INFO, "bookmark SUCCESS");
                recipeViewModel.setTargetRecipe(response.body());
                loadUser(position);
            }

            @Override
            public void onFailure(@NonNull Call<Recipe> call, @NonNull Throwable t) {
                log.log(Level.INFO, "bookmark FAILED");
            }
        });
    }

    @Override
    public void onForkClicked(Recipe recipe, boolean fork, int position) {
        recipeForked(recipe, fork, position);
    }

    private void recipeForked(Recipe recipe, boolean fork, int position) {
        RetrofitFactory.apiServiceClient().recipeForked(user.getPid(), recipe.getPid(), fork).enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(@NonNull Call<Recipe> call, @NonNull Response<Recipe> response) {
                log.log(Level.INFO, "fork SUCCESS");
                recipeViewModel.setTargetRecipe(response.body());
                loadUser(position);
            }

            @Override
            public void onFailure(@NonNull Call<Recipe> call, @NonNull Throwable t) {
                log.log(Level.INFO, "fork FAILED");
            }
        });
    }

    private void loadUser(int position) {
        RetrofitFactory.apiServiceClient().getUser(user.getLogin()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                user = response.body();
                adapter.setUser(user);
                feedRecipeList.get(position).setForks(recipeViewModel.getTargetRecipe().getValue().getForks());
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {

            }
        });
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater, container, false);
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
