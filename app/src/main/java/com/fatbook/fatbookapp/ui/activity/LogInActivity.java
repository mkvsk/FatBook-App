package com.fatbook.fatbookapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.fatbook.fatbookapp.MainActivity;
import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Role;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.ActivityLogInBinding;
import com.fatbook.fatbookapp.util.UserUtils;

import java.util.ArrayList;

public class LogInActivity extends AppCompatActivity {

    private ActivityLogInBinding binding;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonLogIn.setOnClickListener(view -> {
            String login = binding.editTextLogin.getText().toString();
            String pass = binding.editTextPassword.getText().toString();
            if (loginValid(login, pass)) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(UserUtils.TAG_USER, user);
                startActivity(intent);
                finishAffinity();
            } else {
                binding.editTextLogin.setBackground(AppCompatResources.getDrawable(this, R.drawable.round_corner_edittext_login_error));
                binding.editTextPassword.setBackground(AppCompatResources.getDrawable(this, R.drawable.round_corner_edittext_login_error));
            }
        });

        binding.editTextLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.editTextLogin.setBackground(AppCompatResources.getDrawable(getBaseContext(), R.drawable.round_corner_edittext_login));
                binding.editTextPassword.setBackground(AppCompatResources.getDrawable(getBaseContext(), R.drawable.round_corner_edittext_login));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private boolean loginValid(String login, String pass) {
        user = new User();
//        user.setLogin(login);
        user.setRole(Role.USER);
        user.setRecipes(new ArrayList<>());
        user.setRecipesForked(new ArrayList<>());
        user.setRecipesBookmarked(new ArrayList<>());
//        user.setRegDate(new Date());
        //TODO api validation
        return true;
    }
}
