package online.fatbook.fatbookapp.ui.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import online.fatbook.fatbookapp.ui.feed.FeedFragment
import online.fatbook.fatbookapp.ui.navigation.listeners.FragmentLifecycleListener
import online.fatbook.fatbookapp.ui.notifications.NotificationsFragment
import online.fatbook.fatbookapp.ui.recipe.edit.RecipeFirstStageFragment
import online.fatbook.fatbookapp.ui.search.ui.SearchFragment
import online.fatbook.fatbookapp.ui.user.ui.UserProfileFragment
import online.fatbook.fatbookapp.util.Constants.rootDestinations

class BaseFragment : Fragment(), FragmentLifecycleListener {

    private val defaultInt = -1
    private var layoutRes: Int = -1
    private var navHostId: Int = -1
    private val appBarConfig = AppBarConfiguration(rootDestinations)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            layoutRes = it.getInt(KEY_LAYOUT)
            navHostId = it.getInt(KEY_NAV_HOST)
            Log.d("==========BaseFragment==========", "onCreate: $arguments")
        } ?: return
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (layoutRes == defaultInt) {
            null
        } else {
            inflater.inflate(layoutRes, container, false)
        }
    }

    override fun onStart() {
        super.onStart()
        if (navHostId == defaultInt) {
            return
        }
    }

    fun onBackPressed(): Boolean {
        Log.d("BaseFragment", "onBackPressed: $navHostId, $layoutRes, $appBarConfig")
        when (val fragment = childFragmentManager.fragments[0].childFragmentManager.fragments[0]) {
            is FeedFragment -> fragment.onBackPressedBase()
            is SearchFragment -> fragment.onBackPressedBase()
            is RecipeFirstStageFragment -> {
                fragment.onBackPressedBase()
                fragment.closeFragment()
            }
            is NotificationsFragment -> fragment.onBackPressedBase()
            is UserProfileFragment -> fragment.onBackPressedBase()
        }
        return requireActivity()
            .findNavController(navHostId)
            .navigateUp(appBarConfig)
    }


    fun popToRoot() {
        val navController = requireActivity().findNavController(navHostId)
        val id = navController.currentDestination?.id
        if (id != navController.graph.startDestinationId) {
            navController.popBackStack(navController.graph.startDestinationId, false)
        }
    }

    fun redrawFragment() {
        val navController = requireActivity().findNavController(navHostId)
        val id = navController.currentDestination?.id
        id?.let {
            navController.popBackStack(it, true)
            navController.navigate(it)
        }
    }

    fun handleDeepLink(intent: Intent): Boolean {
        Log.d("==========BaseFragment==========", "handleDeepLink: $navHostId")
        return requireActivity().findNavController(navHostId).handleDeepLink(intent)
    }

    override fun scrollFragmentToTop() {
        when (val fragment = childFragmentManager.fragments[0].childFragmentManager.fragments[0]) {
            is FeedFragment -> fragment.scrollUpBase()
            is SearchFragment -> fragment.scrollUpBase()
            is RecipeFirstStageFragment -> fragment.scrollUpBase()
            is NotificationsFragment -> fragment.scrollUpBase()
            is UserProfileFragment -> fragment.scrollUpBase()
        }
    }

    override fun openFragment() {
        when (val fragment = childFragmentManager.fragments[0].childFragmentManager.fragments[0]) {
            is FeedFragment -> fragment.openFragment()
            is SearchFragment -> fragment.openFragment()
            is RecipeFirstStageFragment -> fragment.openFragment()
            is NotificationsFragment -> fragment.openFragment()
            is UserProfileFragment -> fragment.openFragment()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("==========BaseFragment==========", "onDestroy: $arguments")
    }

    companion object {

        private const val KEY_LAYOUT = "layout_key"
        private const val KEY_NAV_HOST = "nav_host_key"

        fun newInstance(layoutRes: Int, navHostId: Int) = BaseFragment().apply {
            arguments = Bundle().apply {
                putInt(KEY_LAYOUT, layoutRes)
                putInt(KEY_NAV_HOST, navHostId)
            }
        }
    }
}