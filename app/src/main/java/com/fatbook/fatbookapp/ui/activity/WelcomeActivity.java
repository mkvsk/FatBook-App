package com.fatbook.fatbookapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fatbook.fatbookapp.databinding.ActivityWelcomeBinding;
import com.fatbook.fatbookapp.util.UserUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.java.Log;

@Log
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
