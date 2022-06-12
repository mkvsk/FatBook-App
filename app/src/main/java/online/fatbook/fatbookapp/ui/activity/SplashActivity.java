package online.fatbook.fatbookapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import online.fatbook.fatbookapp.R;
import online.fatbook.fatbookapp.core.User;
import online.fatbook.fatbookapp.databinding.ActivitySplashBinding;
import online.fatbook.fatbookapp.retrofit.RetrofitFactory;
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel;
import online.fatbook.fatbookapp.util.UserUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.logging.Level;

import lombok.extern.java.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Log
public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    private UserViewModel userViewModel;

    private String userLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        SharedPreferences sharedPreferences = getSharedPreferences(UserUtils.APP_PREFS, Context.MODE_PRIVATE);
        userLogin = sharedPreferences.getString(UserUtils.USER_LOGIN, StringUtils.EMPTY);
        if (StringUtils.isEmpty(userLogin)) {
            userViewModel.setUser(new User());
        } else {
            loadUserData(userLogin);
        }

        userViewModel.getUser().observe(this, user -> {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Intent intent;
                if (StringUtils.isEmpty(userLogin)) {
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
            loadUserData(userLogin);
        });
    }

    private void loadUserData(String login) {
        RetrofitFactory.apiServiceClient().getUser(login).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                log.log(Level.INFO, "" + response.code() + " found user: " + response.body());
                userViewModel.setUser(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                log.log(Level.INFO, "load user ERROR");
                if (t.toString().contains("ConnectException")) {
                    binding.textViewSplashError.setText(getString(R.string.splash_no_connection_error_client));
                } else {
                    binding.textViewSplashError.setText(getString(R.string.splash_no_connection_error_api));
                }
                binding.textViewSplashError.setVisibility(View.VISIBLE);
                binding.buttonSplashRetry.setVisibility(View.VISIBLE);
            }
        });
    }
}
