package com.fatbook.fatbookapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.ActivityPasswordBinding;
import com.fatbook.fatbookapp.util.UserUtils;

public class PasswordActivity extends AppCompatActivity {

    private ActivityPasswordBinding binding;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = (User) getIntent().getSerializableExtra(UserUtils.TAG_USER);

        binding.buttonPasswordNext.setOnClickListener(view -> {
            Intent intent = new Intent(this, SkipAdditionalInfoActivity.class);
            intent.putExtra(UserUtils.TAG_USER, user);
            startActivity(intent);
        });
    }
}
