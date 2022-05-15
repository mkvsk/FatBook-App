package com.fatbook.fatbookapp;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.ActivityMainBinding;
import com.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;
import com.fatbook.fatbookapp.ui.viewmodel.UserViewModel;
import com.fatbook.fatbookapp.util.UserUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private RecipeViewModel recipeViewModel;

    private UserViewModel userViewModel;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_feed, R.id.navigation_ingredients, R.id.navigation_add_recipe, R.id.navigation_bookmarks, R.id.navigation_user_profile)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        NavigationUI.setupWithNavController(binding.navView, navController);

        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        loadUser();
//        loadSplash();
    }

    private void loadUser() {
        boolean fillAdditional = getIntent().getBooleanExtra(UserUtils.FILL_ADDITIONAL_INFO, false);
        User user = (User) getIntent().getSerializableExtra(UserUtils.USER);
        userViewModel.setUser(user);

        if (fillAdditional) {
            navController.navigate(R.id.navigation_user_profile);
        }
    }

    //    private void loadSplash() {
//        Intent intentSplash = new Intent(this, SplashActivity.class);
//        startActivity(intentSplash);
//    }

}