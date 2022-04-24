package com.fatbook.fatbookapp.ui.activity.fill_additional_info;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.ActivityFillAdditionalInfoBinding;
import com.fatbook.fatbookapp.util.UserUtil;

import java.io.File;

public class FillAdditionalInfoActivity extends AppCompatActivity {

    private ActivityFillAdditionalInfoBinding binding;

    private ActivityResultLauncher<String> choosePhotoFromGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFillAdditionalInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FillAdditionalInfoViewModel viewModel =
                new ViewModelProvider(this).get(FillAdditionalInfoViewModel.class);

        Intent introduceIntent = getIntent();
        User user = (User) introduceIntent.getSerializableExtra(UserUtil.USER);

        binding.buttonFillAddSave.setOnClickListener(view -> {
            user.setName(binding.editTextFillAddName.getText().toString());
//            user.setBirthday();
            user.setBio(binding.editTextFillAddBio.getText().toString());
            viewModel.saveUser(binding.getRoot(), user);
        });

        choosePhotoFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    binding.imageViewFillAddUserImage.setImageURI(uri);
                    user.setPhoto(uri.getPath());
                });

        binding.imageViewFillAddUserImage.setOnClickListener(view -> {
            choosePhotoFromGallery.launch("image/*");
        });
    }
}
