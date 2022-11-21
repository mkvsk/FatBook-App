package online.fatbook.fatbookapp.ui.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import online.fatbook.fatbookapp.ui.viewmodel.*
import online.fatbook.fatbookapp.util.Constants.SP_TAG
import online.fatbook.fatbookapp.util.Constants.SP_TAG_BACK_STACK
import online.fatbook.fatbookapp.util.Constants.SP_TAG_DARK_MODE_CHANGED
import online.fatbook.fatbookapp.util.Constants.SP_TAG_PASSWORD
import online.fatbook.fatbookapp.util.Constants.SP_TAG_USERNAME
import online.fatbook.fatbookapp.ui.listeners.FragmentLifecycle
import online.fatbook.fatbookapp.util.*
import online.fatbook.fatbookapp.util.alert_dialog.FBAlertDialogBuilder
import org.apache.commons.lang3.StringUtils
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

    private var backStack = Stack<Int>()
    private var adapter: ViewPagerAdapter? = null
    private var currentItemPosition = 0

    private lateinit var sharedPreferences: SharedPreferences

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

        ProgressBarUtil.set(this)
        RecipeUtils.setContext(this)
        Utils.setContext(this)
        FBAlertDialogBuilder.set(this, layoutInflater)

        main_pager.addOnPageChangeListener(this)
        adapter = ViewPagerAdapter()
        main_pager.adapter = adapter
        sharedPreferences = getSharedPreferences(SP_TAG, MODE_PRIVATE)
        if (sharedPreferences.getBoolean(SP_TAG_DARK_MODE_CHANGED, false)) {
            backStack = getBackStack(sharedPreferences.getString(SP_TAG_BACK_STACK, ""))
            main_pager.currentItem = 4
        }
        main_pager.post(this::checkDeepLink)
        main_pager.offscreenPageLimit = fragments.size
        bottom_navigation.visibility = View.VISIBLE
        bottom_navigation.setOnItemSelectedListener(this)
        bottom_navigation.setOnItemReselectedListener(this)

        if (backStack.empty()) {
            backStack.push(0)
        }

        val sharedPreferences = getSharedPreferences(SP_TAG, MODE_PRIVATE)
        authViewModel!!.username.value =
                sharedPreferences.getString(SP_TAG_USERNAME, StringUtils.EMPTY)
        authViewModel!!.password.value =
                sharedPreferences.getString(SP_TAG_PASSWORD, StringUtils.EMPTY)
        userViewModel!!.user.value = User()
        userViewModel!!.user.value!!.username =
                sharedPreferences.getString(SP_TAG_USERNAME, StringUtils.EMPTY)
    }

    private fun getBackStack(string: String?): Stack<Int> {
        val stack: Stack<Int> = Stack()
        string?.let {
            val list: ArrayList<Int> = ArrayList()
            string.filter { !it.isWhitespace() }.removeSurrounding("[", "]").split(',').forEach {
                list.add(it.toInt())
            }
            list.toIntArray().forEach {
                stack.push(it)
            }
            return stack
        }
        return stack
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
        currentItemPosition = position
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

    override fun onPause() {
        super.onPause()
        Log.d("MAINACTIVITY", "onPause: state saved $backStack")
        sharedPreferences.edit().putString(SP_TAG_BACK_STACK, backStack.toString()).apply()
    }

    override fun onStop() {
        super.onStop()
        Log.d("MAINACTIVITY", "onStop: state saved $backStack")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MAINACTIVITY", "onDestroy: state saved $backStack")
    }
}