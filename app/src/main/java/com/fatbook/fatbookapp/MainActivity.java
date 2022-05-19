package com.fatbook.fatbookapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.ActivityMainBinding;
import com.fatbook.fatbookapp.ui.viewmodel.IngredientViewModel;
import com.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;
import com.fatbook.fatbookapp.ui.viewmodel.UserViewModel;
import com.fatbook.fatbookapp.util.UserUtils;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private RecipeViewModel recipeViewModel;

    private UserViewModel userViewModel;

    private IngredientViewModel ingredientViewModel;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        ingredientViewModel = new ViewModelProvider(this).get(IngredientViewModel.class);

        User user = (User) getIntent().getSerializableExtra(UserUtils.TAG_USER);
        userViewModel.setUser(user);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_feed, R.id.navigation_ingredients, R.id.navigation_recipe_create, R.id.navigation_bookmarks, R.id.navigation_user_profile)
//                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

//        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
//            @Override
//            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
//                switch (navDestination.getLabel().toString()) {
//                    case "Feed":
//                        navController.navigate(R.id.action_go_to_feed);
//                        break;
//                    case "Ingredients":
//                        navController.navigate(R.id.action_go_to_ingredients);
//                        break;
//                    case "Create recipe":
//                        navController.navigate(R.id.action_go_to_recipe_create);
//                        break;
//                    case "Bookmarks":
//                        navController.navigate(R.id.action_go_to_bookmarks);
//                        break;
//                    case "Profile":
//                        navController.navigate(R.id.action_go_to_profile);
//                        break;
//                }
//            }
//        });

        binding.navView.setOnItemSelectedListener(item -> {
            System.out.println();
            return false;
        });

        binding.navView.setOnClickListener(view -> {
            System.out.println();
        });

//        binding.navView.setOnItemSelectedListener(item -> {
//            System.out.println();
//            return false;
//        });

//        NavGraph inflate = navController.getNavInflater().inflate(R.navigation.mobile_navigation);
//        navController.setGraph(inflate);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        NavigationUI.setupWithNavController(binding.navView, navController);

        boolean fillAdditional = getIntent().getBooleanExtra(UserUtils.FILL_ADDITIONAL_INFO, false);
        if (fillAdditional) {
            navController.navigate(R.id.navigation_user_profile);
        }
//        loadSplash();
    }

    //    private void loadSplash() {
//        Intent intentSplash = new Intent(this, SplashActivity.class);
//        startActivity(intentSplash);
//    }

}