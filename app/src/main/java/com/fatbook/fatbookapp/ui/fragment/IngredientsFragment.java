package com.fatbook.fatbookapp.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Ingredient;
import com.fatbook.fatbookapp.databinding.FragmentIngredientsBinding;
import com.fatbook.fatbookapp.retrofit.RetrofitFactory;
import com.fatbook.fatbookapp.ui.adapters.IngredientAdapter;
import com.fatbook.fatbookapp.ui.viewmodel.IngredientViewModel;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IngredientsFragment extends Fragment {

    private FragmentIngredientsBinding binding;

    private Ingredient ingredientToAdd;

    private List<Ingredient> ingredientList;

    private IngredientViewModel ingredientViewModel;

    private IngredientAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fabIngredientsAdd.setOnClickListener(view1 -> {
            configureAlertDialog();
        });

        ingredientViewModel = new ViewModelProvider(getActivity()).get(IngredientViewModel.class);

        ingredientList = new ArrayList<>();
        setupAdapter();
        loadIngredients();
        setupSwipeRefresh();
        setupObservers();
    }

    private void setupObservers() {
        ingredientViewModel.getIngredientList().observe(getViewLifecycleOwner(), ingredients -> {
            binding.swipeRefreshBookmarks.setRefreshing(false);
            ingredientList = ingredientViewModel.getIngredientList().getValue();
            adapter.setData(ingredientList);
            adapter.notifyDataSetChanged();
        });

        ingredientViewModel.getRefreshFailed().observe(getViewLifecycleOwner(), bool -> {
            if (bool) {
                Toast.makeText(binding.getRoot().getContext(), "failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdapter() {
        RecyclerView rv = binding.rvIngredients;
        adapter = new IngredientAdapter(binding.getRoot().getContext(), ingredientList);
        rv.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshBookmarks.setColorSchemeColors(
                getResources().getColor(R.color.color_pink_a200));

        binding.swipeRefreshBookmarks.setOnRefreshListener(() -> {
            loadIngredients();
        });
    }

    private void saveIngredient() {
        try {
            RetrofitFactory.infoServiceClient().addIngredient(ingredientToAdd).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.code() == HttpsURLConnection.HTTP_CREATED) {
                        Toast.makeText(getContext(), "Created", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadIngredients() {
        new Thread(() -> {
            try {
                RetrofitFactory.infoServiceClient().getAllIngredients().enqueue(new Callback<List<Ingredient>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Ingredient>> call, @NonNull Response<List<Ingredient>> response) {
                        ingredientViewModel.setIngredientList(response.body());
                        ingredientViewModel.setRefreshFailed(false);
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Ingredient>> call, @NonNull Throwable t) {
                        ingredientViewModel.setIngredientList(loadFakeData());
                        ingredientViewModel.setRefreshFailed(true);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void configureAlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Add ingredient");

        final EditText editTextName = new EditText(getContext());
        editTextName.setSingleLine();

        FrameLayout container = new FrameLayout(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        editTextName.setLayoutParams(params);
        container.addView(editTextName);

        alert.setView(container);

        alert.setPositiveButton("OK", (dialogInterface, i) -> {
            String name = editTextName.getText().toString();
            if (StringUtils.isNotEmpty(name)) {
                ingredientToAdd = new Ingredient();
                ingredientToAdd.setName(name);
                saveIngredient();
            } else {
                //TODO продумать логику если ничего не введено
            }
        });
        alert.setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.cancel());
        alert.show();
    }

    private List<Ingredient> loadFakeData() {
        List<Ingredient> fakeList = new ArrayList<>();
        fillFakeList(fakeList);
        return fakeList;
    }

    private void fillFakeList(List<Ingredient> ingredients) {
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentIngredientsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
