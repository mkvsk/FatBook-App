package com.fatbook.fatbookapp.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.logging.Level;

import lombok.extern.java.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Log
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

        ingredientViewModel = new ViewModelProvider(requireActivity()).get(IngredientViewModel.class);

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
                Toast.makeText(binding.getRoot().getContext(), getResources().getString(R.string.ingredient_load_failed), Toast.LENGTH_SHORT).show();
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
            RetrofitFactory.apiServiceClient().addIngredient(ingredientToAdd).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    log.log(Level.INFO, "ingredient save: SUCCESS");
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    log.log(Level.INFO, "ingredient save: FAILED");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadIngredients() {
        new Thread(() -> {
            try {
                RetrofitFactory.apiServiceClient().getAllIngredients().enqueue(new Callback<List<Ingredient>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Ingredient>> call, @NonNull Response<List<Ingredient>> response) {
                        ingredientViewModel.setIngredientList(response.body());
                        ingredientViewModel.setRefreshFailed(false);
                        log.log(Level.INFO, "ingredient list load: SUCCESS");
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Ingredient>> call, @NonNull Throwable t) {
                        ingredientViewModel.setIngredientList(loadFakeData());
                        ingredientViewModel.setRefreshFailed(true);
                        log.log(Level.INFO, "ingredient list load: FAILED");
                    }
                });
            } catch (Exception e) {
                log.log(Level.INFO, "ingredient list load: FAILED " + e);
                e.printStackTrace();
            }
        }).start();
    }

    private void configureAlertDialog() {
        final EditText editTextName = new EditText(getContext());
        editTextName.setSingleLine();
        editTextName.setHintTextColor(getResources().getColor(R.color.color_blue_grey_200));
        editTextName.setTextColor(getResources().getColor(R.color.color_blue_grey_600));

        FrameLayout container = new FrameLayout(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        editTextName.setLayoutParams(params);
        container.addView(editTextName);

        View title = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_title, null);

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(container)
                .setCustomTitle(title)
                .setPositiveButton(getResources().getString(R.string.alert_dialog_btn_ok), null)
                .setNegativeButton(getResources().getString(R.string.alert_dialog_btn_cancel), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String name = editTextName.getText().toString();
                if (StringUtils.isNotEmpty(name) && name.length() >= 3) {
                    ingredientToAdd = new Ingredient();
                    ingredientToAdd.setName(name);
                    saveIngredient();
                    dialog.dismiss();
                } else {
                    editTextName.setText(StringUtils.EMPTY);
                    editTextName.setHint(getResources().getString(R.string.alert_dialog_suggest_ingredient));
                }
            });
        });
        dialog.show();
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
