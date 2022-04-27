package com.fatbook.fatbookapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.fatbook.fatbookapp.MainActivity;
import com.fatbook.fatbookapp.databinding.ActivitySplashBinding;
import com.fatbook.fatbookapp.ui.activity.introduce.IntroduceActivity;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(new Intent(this, IntroduceActivity.class));
//            startActivity(new Intent(this, MainActivity.class));
        }, 1);
    }
}
