package online.fatbook.fatbookapp.ui.activity

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.ActivityMainBinding
import online.fatbook.fatbookapp.ui.viewmodel.IngredientViewModel
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.Constants
import online.fatbook.fatbookapp.util.Constants.FEED_TAG
import online.fatbook.fatbookapp.util.Constants.SP_TAG
import online.fatbook.fatbookapp.util.Constants.SP_TAG_PASSWORD
import online.fatbook.fatbookapp.util.Constants.SP_TAG_USERNAME
import online.fatbook.fatbookapp.util.ProgressBarUtil
import online.fatbook.fatbookapp.util.UserUtils
import org.apache.commons.lang3.StringUtils

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var recipeViewModel: RecipeViewModel? = null
    private var userViewModel: UserViewModel? = null
    private var ingredientViewModel: IngredientViewModel? = null
    private var authViewModel: AuthenticationViewModel? = null
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipeViewModel = ViewModelProvider(this)[RecipeViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        ingredientViewModel = ViewModelProvider(this)[IngredientViewModel::class.java]

        authViewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        ProgressBarUtil.set(this)
        setupNavigation()
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_feed, R.id.navigation_ingredients, R.id.navigation_recipe_create, R.id.navigation_bookmarks, R.id.navigation_user_profile)
//                .build();
        if (intent.getBooleanExtra(FEED_TAG, false)) {
            val sharedPreferences = getSharedPreferences(SP_TAG, MODE_PRIVATE)
            authViewModel!!.username.value = sharedPreferences.getString(SP_TAG_USERNAME, StringUtils.EMPTY)
            authViewModel!!.password.value = sharedPreferences.getString(SP_TAG_PASSWORD, StringUtils.EMPTY)
            navController!!.navigate(R.id.action_go_to_feed_from_welcome)
        }
    }

    private fun setupNavigation() {
        navController = findNavController(this, R.id.nav_host_fragment_activity_main)
        setupWithNavController(bottom_navigation, navController!!)
        bottom_navigation.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.navigation_feed -> navController!!.navigate(R.id.action_go_to_feed)
                R.id.navigation_search -> navController!!.navigate(R.id.action_go_to_search)
                R.id.navigation_recipe_create -> navController!!.navigate(R.id.action_go_to_recipe_create)
                R.id.navigation_notifications -> navController!!.navigate(R.id.action_go_to_notifications)
                R.id.navigation_user_profile -> navController!!.navigate(R.id.action_go_to_profile)
            }
            true
        }

        navController!!.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_welcome -> bottom_navigation.visibility = View.GONE
                R.id.navigation_register_email -> bottom_navigation.visibility = View.GONE
                R.id.navigation_verification_code -> bottom_navigation.visibility = View.GONE
                R.id.navigation_register_password -> bottom_navigation.visibility = View.GONE
                R.id.navigation_register_username -> bottom_navigation.visibility = View.GONE
                R.id.navigation_login -> bottom_navigation.visibility = View.GONE
                R.id.navigation_login_new_pass -> bottom_navigation.visibility = View.GONE
                R.id.navigation_account_created -> bottom_navigation.visibility = View.GONE
                R.id.navigation_view_image -> bottom_navigation.visibility = View.GONE

                else -> bottom_navigation.visibility = View.VISIBLE
            }
        }
    }
}