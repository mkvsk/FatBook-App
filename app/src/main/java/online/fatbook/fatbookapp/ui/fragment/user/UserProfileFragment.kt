package online.fatbook.fatbookapp.ui.fragment.user

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.transition.AutoTransition
import androidx.transition.Scene
import androidx.transition.TransitionManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.FragmentUserProfileBinding
import online.fatbook.fatbookapp.ui.activity.SplashActivity
import online.fatbook.fatbookapp.ui.adapters.UserProfileRecipesAdapter
import online.fatbook.fatbookapp.ui.listeners.BaseFragmentActionsListener
import online.fatbook.fatbookapp.ui.viewmodel.ImageViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.*
import online.fatbook.fatbookapp.util.alert_dialog.FBAlertDialogBuilder
import online.fatbook.fatbookapp.util.alert_dialog.FBAlertDialogListener
import org.apache.commons.lang3.StringUtils


class UserProfileFragment : Fragment(), BaseFragmentActionsListener {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }
    private val imageViewModel by lazy { obtainViewModel(ImageViewModel::class.java) }

    private var expanded: Boolean = false
    private lateinit var viewPager: ViewPager2
    private lateinit var fragmentAdapter: UserProfileRecipesAdapter

    companion object {
        private const val TAG = "UserProfileFragment"
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
            setupMenu(R.menu.user_profile_current_menu)
            setupSwipeRefresh()
            setupViewPager()
            binding.ovalRecipesQtt.setOnClickListener {
                focusOnRecipes()
            }

            binding.ovalFollowersQtt.setOnClickListener {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_followers_from_user_profile)
            }

            binding.userPhoto.setOnClickListener {
                imageViewModel.image.value = userViewModel.user.value!!.profileImage
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_view_image_from_user_profile1)
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

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.isEnabled = false
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
            loadUserData()
        }
    }

    private fun loadUserData() {
        if (userViewModel.selectedUsername.value.isNullOrEmpty()) {
            binding.toolbar.title = userViewModel.user.value!!.username
            setupViewForLoggedInUser()
        } else {
            binding.toolbar.title = userViewModel.selectedUsername.value!!
            setupViewForSelectedUser()
        }
    }

    private fun setupMenu(menu: Int) {
        binding.toolbar.inflateMenu(menu)
        binding.toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected)
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

    private fun setupViewForLoggedInUser() {
        binding.llBtnsFollowMessage.visibility = View.GONE
        binding.tabLayout.visibility = View.VISIBLE
        binding.toolbar.navigationIcon = null
//        if (userViewModel.user.value?.pid == null) {
        loadUser(userViewModel.user.value!!.username!!, true)
//        } else {
//            drawData(userViewModel.user.value!!)
//        }
    }

    private fun setupViewForSelectedUser() {
        binding.llBtnsFollowMessage.visibility = View.VISIBLE
        binding.tabLayout.visibility = View.GONE
        binding.toolbar.navigationIcon = context?.getDrawable(R.drawable.ic_arrow_back)
        if (userViewModel.selectedUser.value?.pid == null) {
            loadUser(userViewModel.selectedUsername.value!!, false)
        } else {
            drawData(userViewModel.selectedUser.value!!)
        }
    }

    private fun drawData(user: User) {
        Log.i("DRAW DATA", "--------------------------------------------------")
        binding.toolbar.title = user.username
        if (user.online) {
            binding.isOnlineIndicator.visibility = View.VISIBLE
            binding.toolbar.subtitle = getString(R.string.subtitle_online)
        } else {
            binding.isOnlineIndicator.visibility = View.INVISIBLE
            binding.toolbar.subtitle = getString(R.string.subtitle_offline)
        }
        binding.tvRecipesQtt.text =
            user.recipeAmount?.let { FormatUtils.prettyCount(it) }
        binding.tvFollowersQtt.text =
            user.followersAmount?.let { FormatUtils.prettyCount(it) }
        if (user.profileImage.isNullOrEmpty()) {
            binding.userPhoto.setImageDrawable(requireContext().getDrawable(R.drawable.ic_default_userphoto))
            binding.userPhoto.isClickable = false
        } else {
            Glide.with(requireContext()).load(user.profileImage!!)
                .into(binding.userPhoto)
            binding.userPhoto.isClickable = true
        }
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
        binding.loader.progressOverlay.visibility = View.GONE
        binding.toolbar.visibility = View.VISIBLE
        binding.swipeRefresh.isRefreshing = false
        binding.swipeRefresh.isEnabled = true
//        UserRecipesPageFragment().setData()
//        FavouritesRecipesPageFragment().setData()


//        userViewModel.user.value!!.recipesFavourites?.let {
//            if (it.isNotEmpty()) {
//                Log.d(TAG, "favs: ${userViewModel.user.value!!.recipesFavourites}")
////                fragmentAdapter.setFavs()
//            }
//        }
//        userViewModel.user.value!!.recipes?.let {
//            if (it.isNotEmpty()) {
//                Log.d(TAG, "recipies: ${userViewModel.user.value!!.recipes}")
////                fragmentAdapter.setRecipes()
//            }
//        }

        userViewModel.user.value.let {
            setupViewPager()
        }
    }

    private fun setupViewPager() {
        fragmentAdapter = UserProfileRecipesAdapter(this)
        println("UserProfileRecipesAdapter init")
        viewPager = binding.vpUserprofile
        viewPager.isUserInputEnabled = false
        viewPager.adapter = fragmentAdapter
        viewPager.offscreenPageLimit = 2
        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
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

    private fun loadUser(username: String, updateCurrentUser: Boolean) {
        userViewModel.getUserByUsername(username, object : ResultCallback<User> {
            override fun onResult(value: User?) {
                if (value == null) {
                    loadUser(username, updateCurrentUser)
                } else {
                    if (updateCurrentUser) {
                        userViewModel.user.value = value
                        drawData(userViewModel.user.value!!)
                    } else {
                        userViewModel.selectedUser.value = value
                        drawData(userViewModel.selectedUser.value!!)
                    }
                }
            }

            override fun onFailure(value: User?) {
                loadUser(username, updateCurrentUser)
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
        Log.d("UserProfileFragment", "onDestroy")
        userViewModel.selectedUsername.value = null
        userViewModel.selectedUser.value = null

        _binding = null
    }

    override fun onPause() {
        super.onPause()
        Log.i("UserProfileFragment", "onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.i("UserProfileFragment", "onResume")
    }

    override fun onStart() {
        super.onStart()
        Log.i("UserProfileFragment", "onStart")
    }

    override fun onBackPressedBase(): Boolean {
        return false
    }

    override fun scrollUpBase() {
        binding.nsv.smoothScrollTo(0, 0)
        binding.appBarLayout.setExpanded(true, false)
    }
}
