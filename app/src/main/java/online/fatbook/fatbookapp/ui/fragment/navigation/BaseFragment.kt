package online.fatbook.fatbookapp.ui.fragment.navigation

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
import online.fatbook.fatbookapp.ui.fragment.feed.FeedFragment
import online.fatbook.fatbookapp.ui.fragment.notifications.NotificationsFragment
import online.fatbook.fatbookapp.ui.fragment.recipe.RecipeFirstStageFragment
import online.fatbook.fatbookapp.ui.fragment.search.SearchFragment
import online.fatbook.fatbookapp.ui.fragment.user.UserProfileFragment
import online.fatbook.fatbookapp.util.Constants.rootDestinations
import online.fatbook.fatbookapp.util.FragmentLifecycle

class BaseFragment : Fragment(), FragmentLifecycle {

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
            is FeedFragment -> fragment.scrollUp()
            is SearchFragment -> fragment.scrollUp()
            is RecipeFirstStageFragment -> fragment.scrollUp()
            is NotificationsFragment -> fragment.scrollUp()
            is UserProfileFragment -> fragment.scrollUp()
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