package com.fatbook.fatbookapp.ui.activity.fill_additional_info;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.ActivityFillAdditionalInfoBinding;
import com.fatbook.fatbookapp.util.FileUtils;
import com.fatbook.fatbookapp.util.UserUtils;

import java.io.File;

public class FillAdditionalInfoActivity extends AppCompatActivity {

    private ActivityFillAdditionalInfoBinding binding;

    private ActivityResultLauncher<String> choosePhotoFromGallery;

    private User user;
    private File userProfilePhoto;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFillAdditionalInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FillAdditionalInfoViewModel viewModel =
                new ViewModelProvider(this).get(FillAdditionalInfoViewModel.class);

        Intent introduceIntent = getIntent();
        user = (User) introduceIntent.getSerializableExtra(UserUtils.USER);

        binding.buttonFillAddSave.setOnClickListener(view -> {
            user.setName(binding.editTextFillAddName.getText().toString());
//            user.setBirthday();
            user.setBio(binding.editTextFillAddBio.getText().toString());
            viewModel.saveUser(binding.getRoot(), user, userProfilePhoto);
        });

        choosePhotoFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    verifyStoragePermissions(this);
                    String path = FileUtils.getPath(this, uri);
                    userProfilePhoto = new File(path);
                    binding.imagebuttonFillAddPhoto.setImageURI(uri);
                });

        binding.imagebuttonFillAddPhoto.setOnClickListener(view -> {
            verifyStoragePermissions(this);
            choosePhotoFromGallery.launch("image/*");
        });
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
