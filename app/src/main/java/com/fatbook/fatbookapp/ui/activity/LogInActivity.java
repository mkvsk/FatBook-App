package com.fatbook.fatbookapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Ingredient;
import com.fatbook.fatbookapp.core.IngredientUnit;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.RecipeIngredient;
import com.fatbook.fatbookapp.core.Role;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.ActivityLogInBinding;
import com.fatbook.fatbookapp.ui.viewmodel.LogInViewModel;
import com.fatbook.fatbookapp.util.UserUtils;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.java.Log;

@Log
public class LogInActivity extends AppCompatActivity {

    private ActivityLogInBinding binding;

    private LogInViewModel logInViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        logInViewModel = new ViewModelProvider(this).get(LogInViewModel.class);

        logInViewModel.getUser().observe(this, _user -> {
            if (_user != null) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(UserUtils.TAG_USER, _user);
                startActivity(intent);
                finishAffinity();
            } else {
                binding.editTextLogin.setBackground(AppCompatResources.getDrawable(this, R.drawable.round_corner_edittext_login_error));
                binding.editTextPassword.setBackground(AppCompatResources.getDrawable(this, R.drawable.round_corner_edittext_login_error));
            }
        });

        binding.buttonLogIn.setOnClickListener(view -> {
            validateLogin(binding.editTextLogin.getText().toString(), binding.editTextPassword.getText().toString());
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

    private void validateLogin(String login, String fat) {
        fakeValidation();
/*
       RetrofitFactory.apiServiceClient().login(login, fat).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                log.log(Level.INFO, "fat: " + response.code());
                if (response.code() == 200) {
                    logInViewModel.setUser(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                log.log(Level.INFO, "fat: FAILED " + t);
            }
        });
*/
    }

    private void fakeValidation() {
        User yourMom = new User();
        yourMom.setName("Mom");
        yourMom.setImage("https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----5.jpg");
        yourMom.setBio("I'm am your momma, I gave birth to you and I'll obliterate you. \n cya at dinner mf");
        yourMom.setLogin("your_mom1337");
        yourMom.setRole(Role.USER);
        yourMom.setRecipes(new ArrayList<>());
        yourMom.setRecipesBookmarked(new ArrayList<>());
        yourMom.setRegDate("2022-05-20");
        yourMom.setBirthday("1975-03-09");
        List<RecipeIngredient> ingredientList = new ArrayList<>();
        ingredientList.add(new RecipeIngredient(1L, null, new Ingredient(1L, "potato"), IngredientUnit.PCS, 1.0));
        ingredientList.add(new RecipeIngredient(1L, null, new Ingredient(2L, "milk"), IngredientUnit.ML, 500.0));
        ingredientList.add(new RecipeIngredient(1L, null, new Ingredient(3L, "eggs"), IngredientUnit.PCS, 2.0));
        ingredientList.add(new RecipeIngredient(1L, null, new Ingredient(4L, "bread"), IngredientUnit.PCS, 3.0));
        ingredientList.add(new RecipeIngredient(1L, null, new Ingredient(5L, "cheese"), IngredientUnit.GRAM, 250.0));

        List<Recipe> recipes = new ArrayList<>();

        recipes.add(new Recipe(2L, "Potato", getResources().getString(R.string.text_full_recipe_instruction), yourMom.getLogin(), ingredientList,
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----2.jpg", 21345));
        recipes.add(new Recipe(4L, "creamy Potato", getResources().getString(R.string.text_full_recipe_instruction), yourMom.getLogin(), ingredientList,
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----3.jpg", 8));

        yourMom.setRecipesForked(recipes);
        logInViewModel.setUser(yourMom);
    }
}
