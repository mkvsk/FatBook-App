package online.fatbook.fatbookapp.ui.fragment.navigation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.material.bottomnavigation.BottomNavigationView
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.util.Constants.rootDestinations

class BaseFragment : Fragment() {

    private val defaultInt = -1
    private var layoutRes: Int = -1
    private var navHostId: Int = -1
    private val appBarConfig = AppBarConfiguration(rootDestinations)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            layoutRes = it.getInt(KEY_LAYOUT)
            navHostId = it.getInt(KEY_NAV_HOST)

        } ?: return
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return if (layoutRes == defaultInt) {
            null
        } else {
            inflater.inflate(layoutRes, container, false)
        }
    }

    override fun onStart() {
        super.onStart()
        // return early if no arguments were parsed
        if (navHostId == defaultInt) {
            return
        }

        val navController = requireActivity().findNavController(navHostId)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_welcome -> bottomNav.visibility = View.GONE
                R.id.navigation_register_email -> bottomNav.visibility = View.GONE
                R.id.navigation_verification_code -> bottomNav.visibility = View.GONE
                R.id.navigation_register_password -> bottomNav.visibility = View.GONE
                R.id.navigation_register_username -> bottomNav.visibility = View.GONE
                R.id.navigation_login -> bottomNav.visibility = View.GONE
                R.id.navigation_new_pass -> bottomNav.visibility = View.GONE
                R.id.navigation_account_created -> bottomNav.visibility = View.GONE
                R.id.image_view_dest -> bottomNav.visibility = View.GONE
                R.id.navigation_login_recover_pass -> bottomNav.visibility = View.GONE
                R.id.navigation_login_recover_pass_vcode -> bottomNav.visibility = View.GONE

                else -> bottomNav.visibility = View.VISIBLE
            }
        }

        // setup navigation with toolbar
//        val toolbar = requireActivity().findViewById<Toolbar>(toolbarId)
//        toolbar.title = ""
//        toolbar.subtitle = ""
//

//        NavigationUI.setupWithNavController(toolbar, navController, appBarConfig)
    }

    fun onBackPressed(): Boolean {
        return requireActivity()
            .findNavController(navHostId)
            .navigateUp(appBarConfig)
    }


    fun popToRoot() {
        val navController = requireActivity().findNavController(navHostId)
        val id = navController.currentDestination?.id
        if (id == navController.graph.startDestinationId) {
            navController.popBackStack(id, true)
            navController.navigate(id)
        } else {
            navController.popBackStack(navController.graph.startDestinationId, false)
        }
    }

    fun handleDeepLink(intent: Intent) =
        requireActivity().findNavController(navHostId).handleDeepLink(intent)

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