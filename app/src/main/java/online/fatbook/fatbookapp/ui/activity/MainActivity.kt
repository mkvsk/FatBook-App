package online.fatbook.fatbookapp.ui.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationBarView
import kotlinx.android.synthetic.main.activity_main.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.ActivityMainBinding
import online.fatbook.fatbookapp.ui.fragment.navigation.BaseFragment
import online.fatbook.fatbookapp.ui.fragment.recipe_create.RecipeCreateFirstStageFragment
import online.fatbook.fatbookapp.ui.viewmodel.*
import online.fatbook.fatbookapp.util.FragmentLifecycle
import online.fatbook.fatbookapp.util.RecipeUtils
import java.util.*

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener,
    NavigationBarView.OnItemReselectedListener, ViewPager.OnPageChangeListener {

    private var binding: ActivityMainBinding? = null

    private var recipeViewModel: RecipeViewModel? = null
    private var userViewModel: UserViewModel? = null
    private var staticDataViewModel: StaticDataViewModel? = null
    private var authViewModel: AuthenticationViewModel? = null
    private var searchViewModel: SearchViewModel? = null
    private var imageViewModel: ImageViewModel? = null

    private val backStack = Stack<Int>()
    private var adapter: ViewPagerAdapter? = null
    private var currentItemPosition = 0

    private val fragments = listOf(
        BaseFragment.newInstance(
            R.layout.content_feed_base,
            R.id.nav_host_feed
        ),
        BaseFragment.newInstance(
            R.layout.content_search_base,
            R.id.nav_host_search
        ),
        BaseFragment.newInstance(
            R.layout.content_recipe_create_base,
            R.id.nav_host_recipe_create
        ),
        BaseFragment.newInstance(
            R.layout.content_notifications_base,
            R.id.nav_host_notifications
        ),
        BaseFragment.newInstance(
            R.layout.content_user_profile_base,
            R.id.nav_host_user_profile
        ),
    )

    private val indexToPage = mapOf(
        0 to R.id.navigation_feed,
        1 to R.id.navigation_search,
        2 to R.id.navigation_recipe_create,
        3 to R.id.navigation_notifications,
        4 to R.id.navigation_user_profile
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instantiateViewModels()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
//        ProgressBarUtil.set(this)

        RecipeUtils.setContext(this)

        main_pager.addOnPageChangeListener(this)
        adapter = ViewPagerAdapter()
        main_pager.adapter = adapter
        main_pager.post(this::checkDeepLink)
        main_pager.offscreenPageLimit = fragments.size

        bottom_navigation.visibility = View.VISIBLE
        bottom_navigation.setOnItemSelectedListener(this)
        bottom_navigation.setOnItemReselectedListener(this)

        if (backStack.empty()) {
            backStack.push(0)
        }

        //TODO remove
//        if (intent.getBooleanExtra(FEED_TAG, false)) {
//            val sharedPreferences = getSharedPreferences(SP_TAG, MODE_PRIVATE)
        authViewModel!!.username.value =
            "hewix"
//                sharedPreferences.getString(SP_TAG_USERNAME, StringUtils.EMPTY)
        authViewModel!!.password.value =
            "root1339"
//                sharedPreferences.getString(SP_TAG_PASSWORD, StringUtils.EMPTY)
        userViewModel!!.user.value = User()
        userViewModel!!.user.value!!.username =
            "hewix"
//                sharedPreferences.getString(SP_TAG_USERNAME, StringUtils.EMPTY)
//            navController!!.navigate(R.id.action_go_to_feed_from_welcome)
//        }
    }

    private fun instantiateViewModels() {
        recipeViewModel = ViewModelProvider(this)[RecipeViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        staticDataViewModel = ViewModelProvider(this)[StaticDataViewModel::class.java]
        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]
        imageViewModel = ViewModelProvider(this)[ImageViewModel::class.java]
    }

    inner class ViewPagerAdapter : FragmentPagerAdapter(supportFragmentManager) {

        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.size

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val position = indexToPage.values.indexOf(item.itemId)
        if (main_pager.currentItem != position) {
            setItem(position)
        }
        return true
    }

    private fun setItem(position: Int) {
        main_pager.currentItem = position
        backStack.push(position)
    }

    override fun onNavigationItemReselected(item: MenuItem) {
        val position = indexToPage.values.indexOf(item.itemId)
        val fragment = fragments[position]
        (adapter!!.getItem(position) as FragmentLifecycle).scrollFragmentToTop()
        fragment.popToRoot()
    }

    override fun onBackPressed() {
        val fragment = fragments[main_pager.currentItem]
        val hadNestedFragments = fragment.onBackPressed()
        if (!hadNestedFragments) {
            if (backStack.size > 1) {
                backStack.pop()
                main_pager.currentItem = backStack.peek()

            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        val itemId = indexToPage[position] ?: R.id.navigation_feed
        if (bottom_navigation.selectedItemId != itemId) {
            bottom_navigation.selectedItemId = itemId
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    private fun checkDeepLink() {
        fragments.forEachIndexed { index, fragment ->
            val hasDeepLink = fragment.handleDeepLink(intent)
            if (hasDeepLink) {
                setItem(index)
            }
        }
    }

    fun redrawFragment(menuItemPosition: Int) {
        fragments[menuItemPosition].redrawFragment()
    }
}