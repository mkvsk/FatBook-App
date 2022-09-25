package online.fatbook.fatbookapp.ui.fragment.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.transition.AutoTransition
import androidx.transition.Scene
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_user_profile.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.Recipe
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.FragmentUserProfileBinding
import online.fatbook.fatbookapp.ui.activity.SplashActivity
import online.fatbook.fatbookapp.ui.adapters.RecipeAdapter
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.Constants
import online.fatbook.fatbookapp.util.FormatUtils
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils

class UserProfileFragment : Fragment(), OnRecipeClickListener {

    private var binding: FragmentUserProfileBinding? = null

    private var expanded: Boolean = false

    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_friends.setOnClickListener {
            //TODO remove logout
            val sharedPreferences = requireActivity().getSharedPreferences(
                Constants.SP_TAG,
                Context.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putString(Constants.SP_TAG_USERNAME, StringUtils.EMPTY)
            editor.putString(Constants.SP_TAG_PASSWORD, StringUtils.EMPTY)
            editor.apply()
            startActivity(Intent(requireActivity(), SplashActivity::class.java))
            requireActivity().finish()
        }

        progressbarLayout_userprofile.visibility = View.GONE
        if (!userViewModel.selectedUsername.value.isNullOrEmpty()) {
            loadData()
        }

        this.toolbar_userprofile.navigationIcon = null

        imageview_recipes_qtt_userprofile.setOnClickListener {
            focusOnRecipes()
        }

        imageview_friends_qtt_userprofile.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_followers_from_user_profile)
        }

        imageview_userphoto_userprofile.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_go_to_view_image_from_user_profile)
        }


//        imageview_userphoto_userprofile.setOnClickListener ({
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//            } else {
//                //TODO action go to full
//            }
//        }

//
////            val set = AnimatorInflater.loadAnimator(context, R.animator.open_animator) as AnimatorSet
////            set.setTarget(imageview_userphoto_userprofile)
////            set.start()

        nsv_userprofile.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
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
        })

        floating_button_up.setOnClickListener {
            focusOnRecipes()
        }

        imageview_ic_expand.setOnClickListener {
            TransitionManager.go(Scene(cardview_userprofile), AutoTransition())
            if (!expanded) {
                textview_bio_userprofile.maxLines = Integer.MAX_VALUE
                imageview_ic_expand.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_expand_less
                    )
                )
                expanded = true
            } else {
                textview_bio_userprofile.maxLines = 3
                imageview_ic_expand.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_expand_more
                    )
                )
                expanded = false
            }
        }

        button_messages.setOnClickListener {
            swipe_refresh_user_profile.isRefreshing = false
        }

        val list1 = listOf(
            Recipe(name = "sobaka1", forks = 255),
            Recipe(name = "sobaka2", forks = 1477, author = "Neshik"),
            Recipe(
                name = "Text text text text text text text text",
                forks = 1234567,
                author = "Timofey"
            ),
            Recipe(name = "sobaka4"),
            Recipe(name = "sobaka5"),
            Recipe(name = "sobaka6"),
            Recipe(name = "sobaka7"),
            Recipe(name = "sobaka8", forks = 1339),
            Recipe(name = "sobaka9"),
            Recipe(name = "sobaka10")
        )
        val list2 = listOf(
            Recipe(name = "kot1"),
            Recipe(name = "kot2"),
            Recipe(name = "kot3"),
            Recipe(name = "kot4"),
            Recipe(name = "kot5"),
            Recipe(name = "kot6"),
            Recipe(name = "kot7"),
            Recipe(name = "kot8"),
            Recipe(name = "kot9")
        )

        val adapter = RecipeAdapter()
        adapter.setData(list1, User())
        adapter.setClickListener(this)
        rv_user_recipe.adapter = adapter

        tabLayout_userprofile.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position == 0) {
                    adapter.setData(list1)
                } else {
                    adapter.setData(list2)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }

    private fun drawData() {

        val user = userViewModel.selectedUser.value

        user!!.username = "kit"
        toolbar_userprofile.title = user!!.username

        if (user.recipes == null) {
            textview_recipes_qtt_userprofile.text = "0"
        } else {
            textview_recipes_qtt_userprofile.text = FormatUtils.prettyCount(user.recipes?.size!!)
        }

        if (user.followersAmount == null || user.followersAmount == 0) {
            textview_friends_qtt_userprofile.text = "0"
        } else {
            textview_friends_qtt_userprofile.text = FormatUtils.prettyCount(user.followersAmount!!)
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
        }

        user.profileImage = "https://fatbook.b-cdn.net/root/upal.jpg"
        Glide
            .with(requireContext())
            .load(user.profileImage!!)
            .into(imageview_userphoto_userprofile)

        user.online = true
        if (user.online!!) {
            imageview_is_online.visibility = View.VISIBLE
        } else {
            imageview_is_online.visibility = View.INVISIBLE
        }
    }

    //TODO ШЕФ СЮДА ПАСМАТРИ АЛООООООООООООООООООООООООООООООООООООООООООООООО
    private fun loadData() {
        userViewModel.getUserByUsername(
            userViewModel.selectedUsername.value.toString(),
            object : ResultCallback<User> {
                override fun onResult(value: User?) {
                    drawData()
                    progressbarLayout_userprofile.visibility = View.GONE
                }

                override fun onFailure(value: User?) {

                }
            })
    }

    private fun focusOnRecipes() {
        nsv_userprofile.post {
            nsv_userprofile.scrollTo(
                0,
                cardview_userprofile.bottom
            )
        }
    }

    override fun onRecipeClick(position: Int) {
        Log.d("recipe click", position.toString())
    }

    override fun onBookmarksClick(recipe: Recipe?, bookmark: Boolean, position: Int) {
        Log.d("bookmark click", position.toString())
    }

    override fun onForkClicked(recipe: Recipe?, fork: Boolean, position: Int) {
        Log.d("fork click", position.toString())
    }


}
