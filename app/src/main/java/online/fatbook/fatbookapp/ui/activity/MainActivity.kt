package online.fatbook.fatbookapp.ui.activity

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.User
import online.fatbook.fatbookapp.databinding.ActivityMainBinding
import online.fatbook.fatbookapp.ui.viewmodel.IngredientViewModel
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.UserUtils
import org.apache.commons.lang3.StringUtils

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var recipeViewModel: RecipeViewModel? = null
    private var userViewModel: UserViewModel? = null
    private var ingredientViewModel: IngredientViewModel? = null
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipeViewModel = ViewModelProvider(this)[RecipeViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        ingredientViewModel = ViewModelProvider(this)[IngredientViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setupNavigation()

//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_feed, R.id.navigation_ingredients, R.id.navigation_recipe_create, R.id.navigation_bookmarks, R.id.navigation_user_profile)
//                .build();
        val fillAdditional = intent.getBooleanExtra(UserUtils.FILL_ADDITIONAL_INFO, false)
        if (fillAdditional) {
            navController!!.navigate(R.id.action_go_to_profile)
        }
        loadData()
    }

    private fun loadData() {
        val user = intent.getSerializableExtra(UserUtils.TAG_USER) as User?
        val sharedPreferences = getSharedPreferences(UserUtils.APP_PREFS, MODE_PRIVATE)
        if (StringUtils.isEmpty(
                sharedPreferences.getString(
                    UserUtils.USER_LOGIN,
                    StringUtils.EMPTY
                )
            )
        ) {
    //        val editor = sharedPreferences.edit()
//            editor.putString(UserUtils.USER_LOGIN, user!!.login)
  //          editor.apply()
        }
        userViewModel!!.user.value = user
    }

    private fun setupNavigation() {
        navController = findNavController(this, R.id.nav_host_fragment_activity_main)
        setupWithNavController(binding!!.bottomNavigation, navController!!)
        binding!!.bottomNavigation.setOnItemSelectedListener { item: MenuItem ->
            when (item.title.toString()) {
                "Feed" -> navController!!.navigate(R.id.action_go_to_feed)
                "Ingredients" -> navController!!.navigate(R.id.action_go_to_ingredients)
                "Create recipe" -> navController!!.navigate(R.id.action_go_to_recipe_create)
                "Bookmarks" -> navController!!.navigate(R.id.action_go_to_bookmarks)
                "Profile" -> navController!!.navigate(R.id.action_go_to_profile)
            }
            false
        }
    }
}