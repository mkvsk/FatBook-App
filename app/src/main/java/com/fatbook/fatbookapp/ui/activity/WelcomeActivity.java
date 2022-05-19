package com.fatbook.fatbookapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fatbook.fatbookapp.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {

    private ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonRegister.setOnClickListener(view -> {
            startActivity(new Intent(this, IntroduceActivity.class));
        });

        binding.buttonLogIn.setOnClickListener(view -> {
            startActivity(new Intent(this, LogInActivity.class));
        });
    }
}
