package com.fatbook.fatbookapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.ActivityMainBinding;
import com.fatbook.fatbookapp.ui.activity.SplashActivity;
import com.fatbook.fatbookapp.ui.fragment.feed.FeedViewModel;
import com.fatbook.fatbookapp.util.UserUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_feed, R.id.navigation_add_recipe, R.id.navigation_user_profile)
                .build();
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        User user = (User) getIntent().getSerializableExtra(UserUtils.USER);
        if (user != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(UserUtils.USER, user);
            navController.navigate(R.id.navigation_user_profile, bundle);
        }
        
//        loadSplash();
    }

//    private void loadSplash() {
//        Intent intentSplash = new Intent(this, SplashActivity.class);
//        startActivity(intentSplash);
//    }

}