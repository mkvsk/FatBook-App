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
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.fragment_user_profile.nsv_userprofile
import kotlinx.android.synthetic.main.include_progress_overlay.*
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

    private var binding: FragmentUserProfileBinding? = null
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
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding!!.root
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
            progress_overlay.visibility = View.VISIBLE
            toolbar_userprofile.visibility = View.GONE
            loadUserData()
            setupMenu(R.menu.user_profile_current_menu)
            setupSwipeRefresh()
            imageview_recipes_qtt_userprofile.setOnClickListener {
                focusOnRecipes()
            }

            imageview_friends_qtt_userprofile.setOnClickListener {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_followers_from_user_profile)
            }

            imageview_userphoto_userprofile.setOnClickListener {
                imageViewModel.image.value = userViewModel.user.value!!.profileImage
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_view_image_from_user_profile1)
            }

            nsv_userprofile.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
                showButtonUp(scrollY)
            })

            floating_button_up.setOnClickListener {
                focusOnRecipes()
            }

            imageview_ic_expand.setOnClickListener {
                animateTextExpand()
            }
        }
    }

    private fun setupSwipeRefresh() {
        swipe_refresh_user_profile.isEnabled = false
        swipe_refresh_user_profile.isRefreshing = false
        swipe_refresh_user_profile.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.theme_primary_bgr
            )
        )
        swipe_refresh_user_profile.setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(), R.color.color_pink_a200
            )
        )
        swipe_refresh_user_profile.setOnRefreshListener {
            loadUserData()
        }
    }

    private fun loadUserData() {
        if (userViewModel.selectedUsername.value.isNullOrEmpty()) {
            toolbar_userprofile.title = userViewModel.user.value!!.username
            setupViewForLoggedInUser()
        } else {
            toolbar_userprofile.title = userViewModel.selectedUsername.value!!
            setupViewForSelectedUser()
        }
    }

    private fun setupMenu(menu: Int) {
        toolbar_userprofile.inflateMenu(menu)
        toolbar_userprofile.setOnMenuItemClickListener(this::onOptionsItemSelected)
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
        TransitionManager.go(Scene(cardview_userprofile), AutoTransition())
        if (!expanded) {
            textview_bio_userprofile.maxLines = Integer.MAX_VALUE
            imageview_ic_expand.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_expand_less
                )
            )
            expanded = true
        } else {
            textview_bio_userprofile.maxLines = 3
            imageview_ic_expand.setImageDrawable(
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
                floating_button_up.visibility = View.VISIBLE
            }
            if (scrollY < 1044) {
                floating_button_up.visibility = View.GONE
            }
        } else {
            if (scrollY >= 1200) {
                floating_button_up.visibility = View.VISIBLE
            }
            if (scrollY < 1200) {
                floating_button_up.visibility = View.GONE
            }
        }
    }

    private fun setupViewForLoggedInUser() {
        ll_btns_follow_message.visibility = View.GONE
        tabLayout_userprofile.visibility = View.VISIBLE
        toolbar_userprofile.navigationIcon = null
//        if (userViewModel.user.value?.pid == null) {
        loadUser(userViewModel.user.value!!.username!!, true)
//        } else {
//            drawData(userViewModel.user.value!!)
//        }
    }

    private fun setupViewForSelectedUser() {
        ll_btns_follow_message.visibility = View.VISIBLE
        tabLayout_userprofile.visibility = View.GONE
        toolbar_userprofile.navigationIcon = context?.getDrawable(R.drawable.ic_arrow_back)
        if (userViewModel.selectedUser.value?.pid == null) {
            loadUser(userViewModel.selectedUsername.value!!, false)
        } else {
            drawData(userViewModel.selectedUser.value!!)
        }
    }

    private fun drawData(user: User) {
        Log.i("DRAW DATA", "--------------------------------------------------")
        toolbar_userprofile.title = user.username
        if (user.online) {
            imageview_is_online.visibility = View.VISIBLE
            toolbar_userprofile.subtitle = getString(R.string.subtitle_online)
        } else {
            imageview_is_online.visibility = View.INVISIBLE
            toolbar_userprofile.subtitle = getString(R.string.subtitle_offline)
        }
        textview_recipes_qtt_userprofile.text =
            user.recipeAmount?.let { FormatUtils.prettyCount(it) }
        textview_friends_qtt_userprofile.text =
            user.followersAmount?.let { FormatUtils.prettyCount(it) }
        if (user.profileImage.isNullOrEmpty()) {
            imageview_userphoto_userprofile.setImageDrawable(requireContext().getDrawable(R.drawable.ic_default_userphoto))
            imageview_userphoto_userprofile.isClickable = false
        } else {
            Glide.with(requireContext()).load(user.profileImage!!)
                .into(imageview_userphoto_userprofile)
            imageview_userphoto_userprofile.isClickable = true
        }
        if (user.title.isNullOrEmpty()) {
            textview_title_userprofile.visibility = View.GONE
        } else {
            textview_title_userprofile.text = user.title
        }

        if (user.website.isNullOrEmpty()) {
            textview_website_userprofile.visibility = View.GONE
            imageview_ic_website.visibility = View.GONE
        } else {
            textview_website_userprofile.text = user.website
        }

        if (user.bio.isNullOrEmpty()) {
            expandableLayout.visibility = View.GONE
            imageview_ic_expand.visibility = View.GONE
        } else {
            textview_bio_userprofile.text = user.bio
            if (textview_bio_userprofile.lineCount <= textview_bio_userprofile.maxLines) {
                imageview_ic_expand.visibility = View.GONE
            } else {
                imageview_ic_expand.visibility = View.VISIBLE
            }
        }
        progress_overlay.visibility = View.GONE
        toolbar_userprofile.visibility = View.VISIBLE
        swipe_refresh_user_profile.isRefreshing = false
        swipe_refresh_user_profile.isEnabled = true

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
        viewPager = vp_userprofile
        viewPager.isUserInputEnabled = false
        viewPager.adapter = fragmentAdapter
        viewPager.offscreenPageLimit = 2
        val tabLayout = tabLayout_userprofile
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
        nsv_userprofile.post {
            nsv_userprofile.smoothScrollTo(
                0, cardview_userprofile.bottom
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("UserProfileFragment", "onDestroy")
        userViewModel.selectedUsername.value = null
        userViewModel.selectedUser.value = null
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
        nsv_userprofile.smoothScrollTo(0, 0)
        appBarLayout_userprofile.setExpanded(true, false)
    }
}
