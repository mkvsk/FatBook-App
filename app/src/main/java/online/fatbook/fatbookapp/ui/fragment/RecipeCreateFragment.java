package online.fatbook.fatbookapp.ui.fragment;

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

import online.fatbook.fatbookapp.R;
import online.fatbook.fatbookapp.core.Recipe;
import online.fatbook.fatbookapp.core.RecipeIngredient;
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateBinding;
import online.fatbook.fatbookapp.retrofit.RetrofitFactory;
import online.fatbook.fatbookapp.ui.adapters.ViewRecipeIngredientAdapter;
import online.fatbook.fatbookapp.ui.listeners.OnRecipeViewDeleteIngredient;
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel;
import online.fatbook.fatbookapp.util.FileUtils;
import online.fatbook.fatbookapp.util.KeyboardActionUtil;
import online.fatbook.fatbookapp.util.RecipeUtils;

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
                    binding.imageViewRecipeCreateImage.setImageURI(selectedImageUri);
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
        save();
        NavHostFragment.findNavController(this).popBackStack();
    }

    private void save() {
        RetrofitFactory.apiServiceClient().recipeCreate(recipe).enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(@NonNull Call<Recipe> call, @NonNull Response<Recipe> response) {
                if (response.code() == 200) {
                    log.log(Level.INFO, "recipe create SUCCESS");
                    recipe = response.body();
                    uploadImage();
                } else {
                    log.log(Level.INFO, "recipe create FAILED " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Recipe> call, @NonNull Throwable t) {
                log.log(Level.INFO, "recipe create FAILED");
            }
        });
    }

    private void uploadImage() {
        if (recipePhoto != null) {
            try {
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), recipePhoto);
                String fileName = "image" + recipePhoto.getName().substring(recipePhoto.getName().indexOf('.'));
                MultipartBody.Part file = MultipartBody.Part.createFormData("file", fileName, requestFile);

                RetrofitFactory.apiServiceClient().uploadImage(file, FileUtils.TAG_RECIPE, recipe.getIdentifier()).enqueue(new Callback<Recipe>() {
                    @Override
                    public void onResponse(@NonNull Call<Recipe> call, @NonNull Response<Recipe> response) {
                        if (response.code() == 200) {
                            log.log(Level.INFO, "image add SUCCESS");
                        } else {
                            log.log(Level.INFO, "image add FAILED " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Recipe> call, @NonNull Throwable t) {
                        log.log(Level.INFO, "image add FAILED");
                    }
                });
            } catch (Exception e) {
                log.log(Level.INFO, e.toString());
                e.printStackTrace();
            }
        }
    }

    private void initRecipe() {
        recipe = new Recipe();
        recipe.setIngredients(new ArrayList<>());
        recipe.setForks(0);
        recipe.setIdentifier(0L);
    }

    private void niceCheck() {
        if (StringUtils.isNotEmpty(binding.editTextRecipeAddTitle.getText().toString())
                && StringUtils.isNotEmpty(binding.editTextRecipeAddDescription.getText().toString())
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
            binding.imageViewRecipeCreateImage.setImageURI(selectedImageUri);
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
