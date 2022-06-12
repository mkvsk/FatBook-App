package online.fatbook.fatbookapp.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import online.fatbook.fatbookapp.R;
import online.fatbook.fatbookapp.core.User;
import online.fatbook.fatbookapp.databinding.ActivityMainBinding;
import online.fatbook.fatbookapp.ui.viewmodel.IngredientViewModel;
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel;
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel;
import online.fatbook.fatbookapp.util.UserUtils;

import org.apache.commons.lang3.StringUtils;

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

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();

//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_feed, R.id.navigation_ingredients, R.id.navigation_recipe_create, R.id.navigation_bookmarks, R.id.navigation_user_profile)
//                .build();


        boolean fillAdditional = getIntent().getBooleanExtra(UserUtils.FILL_ADDITIONAL_INFO, false);
        if (fillAdditional) {
            navController.navigate(R.id.action_go_to_profile);
        }
        loadData();
    }

    private void loadData() {
        User user = (User) getIntent().getSerializableExtra(UserUtils.TAG_USER);
        SharedPreferences sharedPreferences = getSharedPreferences(UserUtils.APP_PREFS, Context.MODE_PRIVATE);
        if (StringUtils.isEmpty(sharedPreferences.getString(UserUtils.USER_LOGIN, StringUtils.EMPTY))) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(UserUtils.USER_LOGIN, user.getLogin());
            editor.apply();
        }
        userViewModel.setUser(user);
    }

    private void setupNavigation() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getTitle().toString()) {
                case "Feed":
                    navController.navigate(R.id.action_go_to_feed);
                    break;
                case "Ingredients":
                    navController.navigate(R.id.action_go_to_ingredients);
                    break;
                case "Create recipe":
                    navController.navigate(R.id.action_go_to_recipe_create);
                    break;
                case "Bookmarks":
                    navController.navigate(R.id.action_go_to_bookmarks);
                    break;
                case "Profile":
                    navController.navigate(R.id.action_go_to_profile);
                    break;
            }
            return false;
        });
    }
}
