package online.fatbook.fatbookapp.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import online.fatbook.fatbookapp.R;
import online.fatbook.fatbookapp.core.Ingredient;
import online.fatbook.fatbookapp.databinding.FragmentIngredientsBinding;
import online.fatbook.fatbookapp.retrofit.RetrofitFactory;
import online.fatbook.fatbookapp.ui.adapters.IngredientsAdapter;
import online.fatbook.fatbookapp.ui.viewmodel.IngredientViewModel;
import online.fatbook.fatbookapp.util.KeyboardActionUtil;

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

    private IngredientsAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        binding.fabIngredientsAdd.setOnClickListener(view1 -> {
            configureAlertDialog();
        });

        ingredientViewModel = new ViewModelProvider(requireActivity()).get(IngredientViewModel.class);

        ingredientList = new ArrayList<>();
        setupAdapter();
        if (ingredientViewModel.getIngredientList().getValue() == null) {
            loadIngredients();
        }
        setupSwipeRefresh();
        ingredientViewModel.getIngredientList().observe(getViewLifecycleOwner(), ingredients -> {
            binding.swipeRefreshBookmarks.setRefreshing(false);
            ingredientList = ingredientViewModel.getIngredientList().getValue();
            adapter.setData(ingredientList);
            adapter.notifyDataSetChanged();
        });
//
//        binding.editTextSearchIngredients.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                filter(editable.toString());
//            }
//        });
//    }
//
//    private void filter(String text) {
//        List<Ingredient> temp = new ArrayList<>();
//        for (Ingredient i : ingredientList) {
//            if (StringUtils.containsIgnoreCase(i.getName(), text)) {
//                temp.add(i);
//            }
//        }
//        adapter.updateList(temp);
    }

    private void setupAdapter() {
        RecyclerView rv = binding.rvIngredients;
        adapter = new IngredientsAdapter(binding.getRoot().getContext(), ingredientList);
        rv.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshBookmarks.setColorSchemeColors(
                getResources().getColor(R.color.color_pink_a200));

        binding.swipeRefreshBookmarks.setOnRefreshListener(this::loadIngredients);
    }

    private void saveIngredient() {
        try {
            RetrofitFactory.apiServiceClient().addIngredient(ingredientToAdd).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.code() == 201) {
                        Toast.makeText(requireContext(), "Ingredient added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Ingredient with that name already exist", Toast.LENGTH_SHORT).show();
                    }
                    log.log(Level.INFO, "ingredient save: " + response.code());
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
        RetrofitFactory.apiServiceClient().getAllIngredients().enqueue(new Callback<List<Ingredient>>() {
            @Override
            public void onResponse(@NonNull Call<List<Ingredient>> call, @NonNull Response<List<Ingredient>> response) {
                ingredientViewModel.setIngredientList(response.body());
                log.log(Level.INFO, "ingredient list load: SUCCESS");
            }

            @Override
            public void onFailure(@NonNull Call<List<Ingredient>> call, @NonNull Throwable t) {
                log.log(Level.INFO, "ingredient list load: FAILED");
                showErrorMsg();
            }
        });
    }

    private void showErrorMsg() {
        Toast.makeText(binding.getRoot().getContext(), getResources().getString(R.string.ingredient_load_failed), Toast.LENGTH_SHORT).show();
    }

    private void configureAlertDialog() {
        final EditText editTextName = new EditText(requireContext());
        editTextName.setSingleLine();
        editTextName.setHintTextColor(getResources().getColor(R.color.color_blue_grey_200));
        editTextName.setTextColor(getResources().getColor(R.color.color_blue_grey_600));

        FrameLayout container = new FrameLayout(requireContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        editTextName.setLayoutParams(params);
        container.addView(editTextName);

        View title = LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog_title_ingredient, null);

        final AlertDialog dialog = new AlertDialog.Builder(requireContext())
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
