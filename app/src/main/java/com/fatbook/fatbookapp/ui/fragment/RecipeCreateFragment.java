package com.fatbook.fatbookapp.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.RecipeIngredient;
import com.fatbook.fatbookapp.databinding.FragmentRecipeCreateBinding;
import com.fatbook.fatbookapp.retrofit.RetrofitFactory;
import com.fatbook.fatbookapp.ui.adapters.ViewRecipeIngredientAdapter;
import com.fatbook.fatbookapp.ui.listeners.OnRecipeViewDeleteIngredient;
import com.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;
import com.fatbook.fatbookapp.ui.viewmodel.UserViewModel;
import com.fatbook.fatbookapp.util.FileUtils;
import com.fatbook.fatbookapp.util.KeyboardActionUtil;
import com.fatbook.fatbookapp.util.RecipeUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import lombok.extern.java.Log;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Log
public class RecipeCreateFragment extends Fragment implements OnRecipeViewDeleteIngredient {

    private FragmentRecipeCreateBinding binding;

    private UserViewModel userViewModel;

    private RecipeViewModel recipeViewModel;

    private Recipe recipe;

    private ViewRecipeIngredientAdapter adapter;

    private ActivityResultLauncher<String> choosePhotoFromGallery;

    private File recipePhoto;

    private Uri selectedImageUri;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);

        recipeViewModel.setSelectedRecipeIngredients(new ArrayList<>());

        try {
            choosePhotoFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                verifyStoragePermissions(requireActivity());
                if (uri != null) {
                    selectedImageUri = uri;
                    String path = FileUtils.getPath(requireContext(), selectedImageUri);
                    recipePhoto = new File(path);
                    binding.buttonRecipeCreateImage.setImageURI(selectedImageUri);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.buttonRecipeCreateImage.setOnClickListener(_view -> {
            verifyStoragePermissions(requireActivity());
            choosePhotoFromGallery.launch("image/*");
        });

        binding.buttonRecipeAddSave.setOnClickListener(_view -> {
            saveRecipe();
        });

        binding.buttonRecipeAddIngredientAdd.setOnClickListener(_view -> {
            NavHostFragment.findNavController(this).navigate(R.id.navigation_add_ingredient);
        });

        setupAdapter();

        binding.editTextRecipeAddTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                niceCheck();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.editTextRecipeAddDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                niceCheck();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void saveRecipe() {
        recipe.setName(binding.editTextRecipeAddTitle.getText().toString());
        recipe.setDescription(binding.editTextRecipeAddDescription.getText().toString());
        recipe.setImage(StringUtils.EMPTY);
        recipe.setAuthor(userViewModel.getUser().getValue().getLogin());
        recipe.setCreateDate(RecipeUtils.regDateFormat.format(new Date()));
        save(false);
//        uploadImage();
        NavHostFragment.findNavController(this).popBackStack();
    }

    private void save(boolean imageUploaded) {
        RetrofitFactory.apiServiceClient().recipeCreate(recipe).enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(@NonNull Call<Recipe> call, @NonNull Response<Recipe> response) {
                log.log(Level.INFO, "recipe create SUCCESS");
                recipe = response.body();
                if (!imageUploaded) {
                    uploadImage();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Recipe> call, @NonNull Throwable t) {
                log.log(Level.INFO, "recipe create FAILED");
            }
        });
    }

    private void update() {
        RetrofitFactory.apiServiceClient().recipeUpdate(recipe).enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(@NonNull Call<Recipe> call, @NonNull Response<Recipe> response) {
                log.log(Level.INFO, "recipe update SUCCESS");
                recipe = response.body();
            }

            @Override
            public void onFailure(@NonNull Call<Recipe> call, @NonNull Throwable t) {
                log.log(Level.INFO, "recipe update FAILED");
            }
        });
    }

    private void uploadImage() {

        if (recipePhoto != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), recipePhoto);

            MultipartBody.Part file = MultipartBody.Part.createFormData("file", recipePhoto.getName(), requestFile);


            RetrofitFactory.apiServiceClient().uploadImage(file, FileUtils.TAG_RECIPE, recipe.getId()).enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    String replace = response.body().replace("-", "/");
                    String url = RetrofitFactory.URL + replace;
                    log.log(Level.INFO, "image upload SUCCESS " + url);
                    recipe.setImage(url);
                    update();
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    log.log(Level.INFO, "image upload FAILED");
                }
            });
        }
    }

    private void initRecipe() {
        recipe = new Recipe();
        recipe.setIngredients(new ArrayList<>());
        recipe.setForks(0);
    }

    private void niceCheck() {
        if (StringUtils.isNotEmpty(binding.editTextRecipeAddTitle.toString())
                && StringUtils.isNotEmpty(binding.editTextRecipeAddDescription.toString())
                && !recipe.getIngredients().isEmpty()) {
            binding.buttonRecipeAddSave.setEnabled(true);
            binding.buttonRecipeAddSave.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.color_pink_a200));
        } else {
            binding.buttonRecipeAddSave.setEnabled(false);
            binding.buttonRecipeAddSave.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.color_blue_grey_200));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardActionUtil(binding.getRoot(), requireActivity(), binding.viewRecipeCreate).listenerForAdjustPan);
        if (recipeViewModel.getSelectedRecipeIngredient().getValue() != null) {
            recipe.getIngredients().add(recipeViewModel.getSelectedRecipeIngredient().getValue());
            adapter.setData(recipe.getIngredients());
            adapter.notifyDataSetChanged();
            recipeViewModel.setSelectedRecipeIngredient(null);
        }
        if (selectedImageUri != null) {
            binding.buttonRecipeCreateImage.setImageURI(selectedImageUri);
        }
        niceCheck();
    }

    private void setupAdapter() {
        if (recipe == null) {
            initRecipe();
        }
        RecyclerView recyclerView = binding.rvRecipeAddIngredients;
        adapter = new ViewRecipeIngredientAdapter(binding.getRoot().getContext(), recipe.getIngredients(), this);
        adapter.setEditMode(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDeleteIngredientClick(RecipeIngredient recipeIngredient, int position) {
        recipe.getIngredients().remove(recipeIngredient);
        adapter.setData(recipe.getIngredients());
        adapter.notifyItemRemoved(position);
        niceCheck();
    }

    private static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipeCreateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(new KeyboardActionUtil(binding.getRoot(), requireActivity(), binding.viewRecipeCreate).listenerForAdjustPan);
    }
}
