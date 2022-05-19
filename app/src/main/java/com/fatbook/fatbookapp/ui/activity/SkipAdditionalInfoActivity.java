package com.fatbook.fatbookapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fatbook.fatbookapp.MainActivity;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.ActivitySkipAdditionalInfoBinding;
import com.fatbook.fatbookapp.util.UserUtils;

public class SkipAdditionalInfoActivity extends AppCompatActivity {

    private ActivitySkipAdditionalInfoBinding binding;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySkipAdditionalInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = (User) getIntent().getSerializableExtra(UserUtils.TAG_USER);

        binding.buttonSkip.setOnClickListener(view -> {
            navigateToMainActivity(false);
        });

        binding.buttonFill.setOnClickListener(view -> {
           navigateToMainActivity(true);
        });
    }

    private void navigateToMainActivity(boolean navigate) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(UserUtils.TAG_USER, user);
        intent.putExtra(UserUtils.FILL_ADDITIONAL_INFO, navigate);
        startActivity(intent);
        finishAffinity();
    }
}
