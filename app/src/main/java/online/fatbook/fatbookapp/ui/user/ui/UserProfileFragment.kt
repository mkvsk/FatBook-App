package online.fatbook.fatbookapp.ui.user.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.transition.AutoTransition
import androidx.transition.Scene
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.SplashActivity
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.FragmentUserProfileBinding
import online.fatbook.fatbookapp.network.callback.ResultCallback
import online.fatbook.fatbookapp.ui.image.viewmodel.ImageViewModel
import online.fatbook.fatbookapp.ui.navigation.listeners.BaseFragmentActionsListener
import online.fatbook.fatbookapp.ui.user.adapters.UserProfileRecipesAdapter
import online.fatbook.fatbookapp.ui.user.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.Constants
import online.fatbook.fatbookapp.util.FormatUtils
import online.fatbook.fatbookapp.util.alert_dialog.FBAlertDialogBuilder
import online.fatbook.fatbookapp.util.alert_dialog.FBAlertDialogListener
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils

class UserProfileFragment : Fragment(), BaseFragmentActionsListener {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }
    private val imageViewModel by lazy { obtainViewModel(ImageViewModel::class.java) }

    private var expanded: Boolean = false
    private lateinit var viewPager: ViewPager2
    private var fragmentAdapter: UserProfileRecipesAdapter? = null

    private var isDataRefreshed = false

    companion object {
        private const val TAG = "UserProfileFragment"
    }

    private var user: User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("UserProfileFragment", "onViewCreated")
        val sharedPreferences =
            requireActivity().getSharedPreferences(Constants.SP_TAG, AppCompatActivity.MODE_PRIVATE)
        if (sharedPreferences.getBoolean(Constants.SP_TAG_DARK_MODE_CHANGED, false)) {
            sharedPreferences.edit().putBoolean(Constants.SP_TAG_DARK_MODE_CHANGED, false).apply()
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_app_settings_from_user_profile)
        } else {
            binding.loader.progressOverlay.visibility = View.VISIBLE
            binding.toolbar.visibility = View.GONE
            loadUserData()
            setupNavMenu()
            setupSwipeRefresh()
            binding.ovalRecipesQty.setOnClickListener {
                focusOnRecipes()
            }

            binding.ovalFollowersQty.setOnClickListener {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_followers_from_user_profile)
            }

            binding.userPhoto.setOnClickListener {
                imageViewModel.setImage(user.profileImage)
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_view_image_from_user_profile)
            }

            binding.nsv.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
                showButtonUp(scrollY)
            })

            binding.buttonUp.setOnClickListener {
                focusOnRecipes()
            }

            binding.icExpand.setOnClickListener {
                animateTextExpand()
            }
        }
    }

    private fun initObservers() {

    }

    private fun setupNavMenu() {
        if (findNavController().previousBackStackEntry?.destination?.id == null) {
            binding.toolbar.navigationIcon = null
        } else {
            binding.toolbar.navigationIcon =
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_arrow_back)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.isEnabled = true
        binding.swipeRefresh.isRefreshing = false
        binding.swipeRefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.theme_primary_bgr
            )
        )
        binding.swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(), R.color.color_pink_a200
            )
        )
        binding.swipeRefresh.setOnRefreshListener {
            isDataRefreshed = true
            loadUserData()
        }
    }

    private fun loadUserData() {
        if (StringUtils.isEmpty(user.username)) {
            if (userViewModel.selectedUsername.value.isNullOrEmpty()
                || userViewModel.selectedUsername.value == userViewModel.user.value!!.username!!
            ) {
                loadCurrentUser(userViewModel.user.value!!.username!!)
            } else {
                loadUser(userViewModel.selectedUsername.value!!)
            }
        } else {
            if (user.username == userViewModel.user.value!!.username!!) {
                loadCurrentUser(user.username!!)
            } else {
                loadUser(user.username!!)
            }
        }
    }

    private fun setupMenu(menu: Int) {
        binding.toolbar.menu.clear()
        binding.toolbar.inflateMenu(menu)
        binding.toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_user_profile_edit_profile -> {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_edit_profile_from_user_profile)
                true
            }
            R.id.menu_user_profile_badges -> {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_badges_from_user_profile)
                true
            }
            R.id.menu_user_profile_app_settings -> {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_app_settings_from_user_profile)
                true
            }
            R.id.menu_user_profile_app_info -> {
                showAppInfoDialog()
                true
            }
            R.id.menu_user_profile_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAppInfoDialog() {
        FBAlertDialogBuilder.getAppInfoDialog(object : FBAlertDialogListener {
            override fun onClick(dialogInterface: DialogInterface) {
                dialogInterface.dismiss()
            }
        }).show()
    }

    private fun logout() {
        val sharedPreferences = requireActivity().getSharedPreferences(
            Constants.SP_TAG, Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(Constants.SP_TAG_USERNAME, StringUtils.EMPTY)
        editor.putString(Constants.SP_TAG_PASSWORD, StringUtils.EMPTY)
        editor.apply()
        startActivity(Intent(requireActivity(), SplashActivity::class.java))
        requireActivity().finish()
    }

    private fun animateTextExpand() {
        TransitionManager.go(Scene(binding.cardviewUserprofile), AutoTransition())
        if (!expanded) {
            binding.profileBio.maxLines = Integer.MAX_VALUE
            binding.icExpand.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_expand_less
                )
            )
            expanded = true
        } else {
            binding.profileBio.maxLines = 3
            binding.icExpand.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_expand_more
                )
            )
            expanded = false
        }
    }

    private fun showButtonUp(scrollY: Int) {
        if (!expanded) {
            if (scrollY >= 1044) {
                binding.buttonUp.visibility = View.VISIBLE
            }
            if (scrollY < 1044) {
                binding.buttonUp.visibility = View.GONE
            }
        } else {
            if (scrollY >= 1200) {
                binding.buttonUp.visibility = View.VISIBLE
            }
            if (scrollY < 1200) {
                binding.buttonUp.visibility = View.GONE
            }
        }
    }

    private fun setupViewForCurrentUser() {
        setupMenu(R.menu.user_profile_current_menu)
        binding.llBtnsFollowMessage.visibility = View.GONE
        binding.tabLayout.visibility = View.VISIBLE
    }

    private fun setupViewForSelectedUser() {
        setupMenu(R.menu.user_profile_selected_menu)
        binding.llBtnsFollowMessage.visibility = View.VISIBLE
        binding.tabLayout.visibility = View.GONE
    }

    private fun drawData() {
        Log.i("DRAW DATA", "----------for ${user.username}--------------------------------")
        binding.toolbar.title = user.username
        if (user.online) {
            binding.isOnlineIndicator.visibility = View.VISIBLE
            binding.toolbar.subtitle = getString(R.string.subtitle_online)
        } else {
            binding.isOnlineIndicator.visibility = View.INVISIBLE
            binding.toolbar.subtitle = getString(R.string.subtitle_offline)
        }
        binding.tvRecipesQty.text = user.recipeAmount?.let { FormatUtils.prettyCount(it) }
        binding.tvFollowersQty.text = user.followersAmount?.let { FormatUtils.prettyCount(it) }
        binding.userPhoto.isEnabled = !user.profileImage.isNullOrEmpty()
        Glide
            .with(requireContext())
            .load(user.profileImage)
            .placeholder(requireContext().getDrawable(R.drawable.ic_default_userphoto))
            .into(binding.userPhoto)
        if (user.title.isNullOrEmpty()) {
            binding.profileTitle.visibility = View.GONE
        } else {
            binding.profileTitle.text = user.title
        }

        if (user.website.isNullOrEmpty()) {
            binding.profileWebsite.visibility = View.GONE
            binding.icProfileWebsite.visibility = View.GONE
        } else {
            binding.profileWebsite.text = user.website
        }

        if (user.bio.isNullOrEmpty()) {
            binding.expandableLayout.visibility = View.GONE
            binding.icExpand.visibility = View.GONE
        } else {
            binding.profileBio.text = user.bio
            if (binding.profileBio.lineCount <= binding.profileBio.maxLines) {
                binding.icExpand.visibility = View.GONE
            } else {
                binding.icExpand.visibility = View.VISIBLE
            }
        }
        binding.swipeRefresh.isRefreshing = false
        //TODO fix data refresh
//        if (isDataRefreshed) {
//            isDataRefreshed = false
//            fragmentAdapter!!.setData(user)
//        } else {
        setupViewPager(Bundle())
//        }
        binding.loader.progressOverlay.visibility = View.GONE
        binding.toolbar.visibility = View.VISIBLE
    }

    private fun setupViewPager(savedInstanceState: Bundle) {
        fragmentAdapter = UserProfileRecipesAdapter(this, user)
        viewPager = binding.vpUserprofile
        viewPager.isUserInputEnabled = false
        viewPager.adapter = fragmentAdapter
        viewPager.offscreenPageLimit = 2
        TabLayoutMediator(binding.tabLayout, viewPager) { tab, position ->
            tab.text =
                if (position == 0) {
                    resources.getString(R.string.title_recipes_profile)
                } else {
                    resources.getString(R.string.title_favourites_profile)
                }
        }.attach()

        //TODO fix
//        ViewPager2ViewHeightAnimator().viewPager2 = viewPager
    }

    private fun loadUser(username: String) {
        setupViewForSelectedUser()
        userViewModel.getUserByUsername(username, object : ResultCallback<User> {
            override fun onResult(value: User?) {
                if (value != null) {
                    user = value
                }
                drawData()
            }

            override fun onFailure(value: User?) {
                binding.swipeRefresh.isRefreshing = false
            }
        })
    }

    private fun loadCurrentUser(username: String) {
        setupViewForCurrentUser()
        userViewModel.loadCurrentUser(username, object : ResultCallback<User> {
            override fun onResult(value: User?) {
                if (value != null) {
                    user = value
                }
                drawData()
            }

            override fun onFailure(value: User?) {
                binding.swipeRefresh.isRefreshing = false
            }
        })
    }

    private fun focusOnRecipes() {
        binding.nsv.post {
            binding.nsv.smoothScrollTo(
                0, binding.cardviewUserprofile.bottom
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        userViewModel.setSelectedUsername("")
        _binding = null
    }

    override fun onBackPressedBase(): Boolean {
        return false
    }

    override fun scrollUpBase() {
        binding.nsv.smoothScrollTo(0, 0)
        binding.appBarLayout.setExpanded(true, false)
    }

}
