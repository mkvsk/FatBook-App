package com.fatbook.fatbookapp.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Ingredient;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.FragmentFeedBinding;
import com.fatbook.fatbookapp.retrofit.RetrofitFactory;
import com.fatbook.fatbookapp.ui.activity.SplashActivity;
import com.fatbook.fatbookapp.ui.adapters.RecipeAdapter;
import com.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener;
import com.fatbook.fatbookapp.ui.listeners.OnRecipeRevertDeleteListener;
import com.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;
import com.fatbook.fatbookapp.ui.viewmodel.UserViewModel;
import com.fatbook.fatbookapp.util.KeyboardActionUtil;
import com.fatbook.fatbookapp.util.UserUtils;

import org.apache.commons.lang3.StringUtils;

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

        if (recipeViewModel.getSelectedRecipePosition().getValue() != null) {
            if (user != null) {
                loadUser(recipeViewModel.getSelectedRecipePosition().getValue());
            }
        }

        feedRecipeList = new ArrayList<>();

        userViewModel.getUser().observe(getViewLifecycleOwner(), userUpdated -> {
            user = userUpdated;
            if (userViewModel.getFeedRecipeList().getValue() == null) {
                loadData();
            }
        });

        binding.swipeRefreshBookmarks.setColorSchemeColors(getResources().getColor(R.color.color_pink_a200));

        binding.swipeRefreshBookmarks.setOnRefreshListener(this::loadData);

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
        setupSearch();
    }

    private void setupSearch() {
        binding.editTextFeedSearch.addTextChangedListener(new TextWatcher() {
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
            List<Recipe> temp = new ArrayList<>();
            for (Recipe r : feedRecipeList) {
                if (StringUtils.containsIgnoreCase(r.getName(), text) || StringUtils.containsIgnoreCase(r.getDescription(), text)) {
                    temp.add(r);
                }
            }
            adapter.updateList(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupAdapter() {
        RecyclerView recyclerView = binding.rvFeed;
        adapter = new RecipeAdapter(binding.getRoot().getContext(), feedRecipeList, user, this);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(adapter);
    }

    //TODO убрать костыль в запросе
    private void loadData() {
        RetrofitFactory.apiServiceClient().getFeed(0L).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                userViewModel.setFeedRecipeList(response.body());
                log.log(Level.INFO, "feed data load: SUCCESS");
                if (response.body() != null) {
                    log.log(Level.INFO, "loaded " + response.body().size() + " recipes");
                }
                loadUser(null);
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
        recipeViewModel.setSelectedRecipePosition(position);
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
                recipeViewModel.setSelectedRecipe(response.body());
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
                recipeViewModel.setSelectedRecipe(response.body());
                loadUser(position);
            }

            @Override
            public void onFailure(@NonNull Call<Recipe> call, @NonNull Throwable t) {
                log.log(Level.INFO, "fork FAILED");
            }
        });
    }

    private void loadUser(Integer position) {
        if (user == null) {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(UserUtils.APP_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(UserUtils.USER_LOGIN, StringUtils.EMPTY);
            editor.apply();
            startActivity(new Intent(requireActivity(), SplashActivity.class));
            requireActivity().finish();
        } else {
            RetrofitFactory.apiServiceClient().getUser(user.getLogin()).enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (response.code() == 200) {
                        log.log(Level.INFO, "user load SUCCESS");
                        userViewModel.setUser(response.body());
                        System.out.println(user);
                        adapter.setUser(user);
                        if (position != null) {
                            if (recipeViewModel.getSelectedRecipe().getValue() != null) {
                                feedRecipeList.get(position).setForks(recipeViewModel.getSelectedRecipe().getValue().getForks());
                                adapter.notifyItemChanged(position);
                            } else {
                                feedRecipeList.remove((int) position);
                                adapter.notifyItemRemoved(position);
                            }
                            recipeViewModel.setSelectedRecipe(null);
                            recipeViewModel.setSelectedRecipePosition(null);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        log.log(Level.INFO, "user load FAILED " + response.code());
                    }
                    if (binding != null) {
                        binding.swipeRefreshBookmarks.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    log.log(Level.INFO, "user load FAILED");
                }
            });
        }
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
