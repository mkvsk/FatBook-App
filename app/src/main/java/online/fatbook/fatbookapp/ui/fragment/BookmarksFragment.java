package online.fatbook.fatbookapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import online.fatbook.fatbookapp.R;
import online.fatbook.fatbookapp.core.Recipe;
import online.fatbook.fatbookapp.core.User;
import online.fatbook.fatbookapp.databinding.FragmentBookmarksBinding;
import online.fatbook.fatbookapp.retrofit.RetrofitFactory;
import online.fatbook.fatbookapp.ui.adapters.RecipeAdapter;
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener;
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import lombok.extern.java.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Log
public class BookmarksFragment extends Fragment implements OnRecipeClickListener {

    private FragmentBookmarksBinding binding;

    private UserViewModel userViewModel;

    private RecipeViewModel recipeViewModel;

    private List<Recipe> recipeList;

    private RecipeAdapter adapter;

    private User user;

    private boolean userUpdated = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookmarksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.swipeRefreshBookmarks.setColorSchemeColors(getResources().getColor(R.color.color_pink_a200));
        binding.swipeRefreshBookmarks.setOnRefreshListener(() -> {
            userUpdated = false;
            loadRecipes();
        });

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);

        user = userViewModel.getUser().getValue();
        recipeList = new ArrayList<>();
        userViewModel.getUser().observe(getViewLifecycleOwner(), _user -> {
            user = _user;
            if (!userUpdated) {
                loadRecipes();
            }
        });
        setupAdapter();
    }

    private void loadRecipes() {
        RetrofitFactory.apiServiceClient().getUserBookmarks(user.getLogin()).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                binding.swipeRefreshBookmarks.setRefreshing(false);
                if (response.code() == 200) {
                    log.log(Level.INFO, "bookmarks load SUCCESS");
                    if (response.body() != null) {
                        log.log(Level.INFO, response.body().toString());
                    }
                    recipeList = response.body();
                    adapter.setData(recipeList, user);
                    adapter.notifyDataSetChanged();
                } else {
                    log.log(Level.INFO, "bookmarks load FAILED");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                binding.swipeRefreshBookmarks.setRefreshing(false);
                log.log(Level.INFO, "bookmarks load FAILED");
            }
        });
    }

    private void setupAdapter() {
        RecyclerView recyclerView = binding.rvBookmarks;
        adapter = new RecipeAdapter(binding.getRoot().getContext(), recipeList, user, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRecipeClick(int position) {
        Recipe recipe = recipeList.get(position);
        recipeViewModel.setSelectedRecipe(recipe);
        recipeViewModel.setSelectedRecipePosition(position);
        NavHostFragment.findNavController(this).navigate(R.id.action_go_to_recipe_view_from_bookmarks);
    }

    @Override
    public void onBookmarksClick(Recipe recipe, boolean bookmark, int position) {
        recipeList.remove(position);
        adapter.notifyItemRemoved(position);
        user.getRecipesBookmarked().remove(recipe.getIdentifier());
        saveUser();
    }

    private void saveUser() {
        RetrofitFactory.apiServiceClient().userUpdate(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.code() == 200) {
                    log.log(Level.INFO, "user update SUCCESS");
                    if (response.body() != null) {
                        log.log(Level.INFO, response.body().toString());
                    }
                    userViewModel.setUser(response.body());
                    userUpdated = true;
                } else {
                    log.log(Level.INFO, "user update FAILED");
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                log.log(Level.INFO, "user update FAILED");
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
                if (response.code() == 200) {
                    log.log(Level.INFO, "fork SUCCESS");
                    recipeViewModel.setSelectedRecipe(response.body());
                    loadUser();
                } else {
                    log.log(Level.INFO, "fork FAILED");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Recipe> call, @NonNull Throwable t) {
                log.log(Level.INFO, "fork FAILED");
            }
        });
    }

    private void loadUser() {
        RetrofitFactory.apiServiceClient().getUser(user.getLogin()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.code() == 200) {
                    log.log(Level.INFO, "load user SUCCESS");
                    log.log(Level.INFO, "" + response.code() + " found user: " + response.body());
                    userViewModel.setUser(response.body());
                } else {
                    log.log(Level.INFO, "load user ERROR");
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                log.log(Level.INFO, "load user ERROR");
            }
        });
    }
}
