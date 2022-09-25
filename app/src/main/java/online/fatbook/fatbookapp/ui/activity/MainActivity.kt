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
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.ActivityMainBinding
import online.fatbook.fatbookapp.ui.viewmodel.IngredientViewModel
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.Constants.FEED_TAG
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
        val launchFeed = intent.getBooleanExtra(FEED_TAG, false)
        if (launchFeed) {
            navController!!.navigate(R.id.action_go_to_feed_from_welcome)
        }
//        loadData()
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
                getString(R.string.nav_feed) -> navController!!.navigate(R.id.action_go_to_feed)
                getString(R.string.nav_search) -> navController!!.navigate(R.id.action_go_to_search)
                getString(R.string.nav_recipe_create) -> navController!!.navigate(R.id.action_go_to_recipe_create)
                getString(R.string.nav_notifications) -> navController!!.navigate(R.id.action_go_to_notifications)
                getString(R.string.nav_user_profile) -> navController!!.navigate(R.id.action_go_to_profile)
            }
            true
        }

        navController!!.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_welcome -> bottom_navigation.visibility = View.GONE
                R.id.navigation_register_email -> bottom_navigation.visibility = View.GONE
                R.id.navigation_verification_code -> bottom_navigation.visibility = View.GONE
                R.id.navigation_register_password -> bottom_navigation.visibility = View.GONE
                R.id.navigation_sregister_username -> bottom_navigation.visibility = View.GONE
                R.id.navigation_login -> bottom_navigation.visibility = View.GONE
                R.id.navigation_login_new_pass -> bottom_navigation.visibility = View.GONE
                R.id.navigation_account_created -> bottom_navigation.visibility = View.GONE
                R.id.navigation_view_image -> bottom_navigation.visibility = View.GONE

                else -> bottom_navigation.visibility = View.VISIBLE
            }
        }
    }
}