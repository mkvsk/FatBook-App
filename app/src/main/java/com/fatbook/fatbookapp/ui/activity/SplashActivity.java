package com.fatbook.fatbookapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.ActivitySplashBinding;
import com.fatbook.fatbookapp.retrofit.RetrofitFactory;
import com.fatbook.fatbookapp.ui.viewmodel.UserViewModel;
import com.fatbook.fatbookapp.util.UserUtils;

import java.util.logging.Level;

import lombok.extern.java.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Log
public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    private UserViewModel userViewModel;

    private long userPid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        SharedPreferences sharedPreferences = getSharedPreferences(UserUtils.APP_PREFS, Context.MODE_PRIVATE);
        userPid = sharedPreferences.getLong(UserUtils.USER_PID, 0L);
        if (userPid == 0) {
            userViewModel.setUser(new User());
        } else {
            loadUserData(userPid);
        }

        userViewModel.getUser().observe(this, user -> {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Intent intent;
                if (userPid == 0) {
                    intent = new Intent(this, WelcomeActivity.class);
                } else {
                    intent = new Intent(this, MainActivity.class);
                }
                intent.putExtra(UserUtils.TAG_USER, user);
                startActivity(intent);
                finish();
            }, 1);
        });

        binding.buttonSplashRetry.setOnClickListener(view -> {
            binding.textViewSplashError.setVisibility(View.GONE);
            binding.buttonSplashRetry.setVisibility(View.GONE);
            loadUserData(userPid);
        });
    }

    private void loadUserData(long userPid) {
        RetrofitFactory.apiServiceClient().getUser(userPid).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                log.log(Level.INFO, "" + response.code() + " found user: " + response.body());
                userViewModel.setUser(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                log.log(Level.INFO, "load user ERROR");
                if (t.toString().contains("Timeout")) {
                    binding.textViewSplashError.setVisibility(View.VISIBLE);
                    binding.textViewSplashError.setText(getString(R.string.splash_no_connection_error_api));
                    binding.buttonSplashRetry.setVisibility(View.VISIBLE);
                }
                if (t.toString().contains("ConnectException")) {
                    binding.textViewSplashError.setVisibility(View.VISIBLE);
                    binding.textViewSplashError.setText(getString(R.string.splash_no_connection_error_client));
                    binding.buttonSplashRetry.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
