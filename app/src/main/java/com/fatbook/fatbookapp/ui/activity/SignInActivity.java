package com.fatbook.fatbookapp.ui.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Ingredient;
import com.fatbook.fatbookapp.core.IngredientUnit;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.RecipeIngredient;
import com.fatbook.fatbookapp.core.Role;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.ActivitySignInBinding;
import com.fatbook.fatbookapp.retrofit.RetrofitFactory;
import com.fatbook.fatbookapp.ui.viewmodel.SignInViewModel;
import com.fatbook.fatbookapp.util.UserUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import lombok.extern.java.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Log
public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    private SignInViewModel signInViewModel;

    public boolean isKeyboardVisible = false;

    public final ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            final Rect rectangle = new Rect();
            final View contentView = binding.getRoot();
            contentView.getWindowVisibleDisplayFrame(rectangle);
            int screenHeight = contentView.getRootView().getHeight();
            int keypadHeight = screenHeight - rectangle.bottom;
            boolean isKeyboardNowVisible = keypadHeight > screenHeight * 0.15;

            if (isKeyboardVisible != isKeyboardNowVisible) {
                if(isKeyboardNowVisible) {
                    binding.textViewSignInVersion.setVisibility(View.GONE);
                    binding.textViewSignInCopyright.setVisibility(View.GONE);
                    binding.textViewSignInTagline.setVisibility(View.GONE);
                    binding.textViewSignInAppLabel.setVisibility(View.GONE);
                } else {
                    binding.textViewSignInVersion.setVisibility(View.VISIBLE);
                    binding.textViewSignInCopyright.setVisibility(View.VISIBLE);
                    binding.textViewSignInTagline.setVisibility(View.VISIBLE);
                    binding.textViewSignInAppLabel.setVisibility(View.VISIBLE);
                }
            }
            isKeyboardVisible = isKeyboardNowVisible;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        signInViewModel = new ViewModelProvider(this).get(SignInViewModel.class);

        signInViewModel.getUser().observe(this, _user -> {
            if (_user != null) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(UserUtils.TAG_USER, _user);
                startActivity(intent);
                finishAffinity();
            } else {
                binding.editTextSignInLogin.setBackground(AppCompatResources.getDrawable(this, R.drawable.round_corner_edittext_login_error));
                binding.editTextSignInPassword.setBackground(AppCompatResources.getDrawable(this, R.drawable.round_corner_edittext_login_error));
            }
        });

        binding.buttonSignIn.setOnClickListener(view -> {
            validateLogin(binding.editTextSignInLogin.getText().toString().trim(), binding.editTextSignInPassword.getText().toString());
        });

        binding.editTextSignInLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.editTextSignInLogin.setBackground(AppCompatResources.getDrawable(getBaseContext(), R.drawable.round_corner_edittext_login));
                binding.editTextSignInPassword.setBackground(AppCompatResources.getDrawable(getBaseContext(), R.drawable.round_corner_edittext_login));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.editTextSignInPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.editTextSignInLogin.setBackground(AppCompatResources.getDrawable(getBaseContext(), R.drawable.round_corner_edittext_login));
                binding.editTextSignInPassword.setBackground(AppCompatResources.getDrawable(getBaseContext(), R.drawable.round_corner_edittext_login));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void validateLogin(String login, String fat) {
       RetrofitFactory.apiServiceClient().signIn(login, fat).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                log.log(Level.INFO, "fat: " + response.code());
                if (response.code() == 200) {
                    signInViewModel.setUser(response.body());
                } else {
                    signInViewModel.setUser(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                log.log(Level.INFO, "fat: FAILED " + t);
                signInViewModel.setUser(null);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(listener);
    }
}
