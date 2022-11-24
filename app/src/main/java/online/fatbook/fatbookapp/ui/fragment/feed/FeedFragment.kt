package online.fatbook.fatbookapp.ui.fragment.feed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.include_progress_overlay.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.authentication.LoginResponse
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.FragmentFeedBinding
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import online.fatbook.fatbookapp.ui.activity.SplashActivity
import online.fatbook.fatbookapp.ui.adapters.RecipeAdapter
import online.fatbook.fatbookapp.ui.listeners.BaseFragmentActions
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener
import online.fatbook.fatbookapp.ui.listeners.OnRecipeRevertDeleteListener
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.ui.viewmodel.FeedViewModel
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.Constants
import online.fatbook.fatbookapp.util.KeyboardActionUtil
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedFragment : Fragment(), OnRecipeClickListener, OnRecipeRevertDeleteListener, BaseFragmentActions {

    private var binding: FragmentFeedBinding? = null
    private var adapter: RecipeAdapter? = null

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }
    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private val feedViewModel by lazy { obtainViewModel(FeedViewModel::class.java) }
    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }

    companion object {
        private const val TAG = "FeedFragment"
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("===t=======FeedFragment==========", "onViewCreated")
        swipe_refresh_feed.setOnClickListener {
            Toast.makeText(requireContext(), "swipe refresh", Toast.LENGTH_SHORT).show()
        }
        rv_feed.setOnClickListener {
            Toast.makeText(requireContext(), "rv", Toast.LENGTH_SHORT).show()
        }
        progress_overlay.setOnClickListener {
            Toast.makeText(requireContext(), "overlay", Toast.LENGTH_SHORT).show()
        }
        setupSwipeRefresh()
        setupMenu()
        setupAdapter()
        progress_overlay.visibility = View.VISIBLE
        toolbar_feed.visibility = View.GONE
        if (!authViewModel.isUserAuthenticated.value!!) {
            login()
        } else if (userViewModel.user.value == null) {
            loadUser()
        } else {
            swipe_refresh_feed.isEnabled = true
        }
    }

    private fun login() {
        val request: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("username", authViewModel.username.value!!)
                .addFormDataPart("password", authViewModel.password.value!!).build()
        authViewModel.login(request, object : ResultCallback<LoginResponse> {
            override fun onResult(value: LoginResponse?) {
                if (value == null || value.access_token.isNullOrEmpty()) {
                    Log.d("LOGOUT", "${authViewModel.username.value}")
                    logout()
                } else {
                    saveTokens(value)
                    authViewModel.isUserAuthenticated.value = true
                    Log.d("LOGIN", "${authViewModel.username.value}")
                }
            }

            override fun onFailure(value: LoginResponse?) {
                login()
            }
        })
    }

    private fun saveTokens(value: LoginResponse) {
        authViewModel.jwtAccess.value = value.access_token
        authViewModel.jwtRefresh.value = value.refresh_token
        RetrofitFactory.updateJWT(value.access_token!!)
        loadUser()
    }

    private fun loadUser() {
        userViewModel.getUserByUsername(
                authViewModel.username.value!!,
                object : ResultCallback<User> {
                    override fun onResult(value: User?) {
                        userViewModel.user.value = value
                        toolbar_feed.visibility = View.VISIBLE
                        progress_overlay.visibility = View.GONE
                        swipe_refresh_feed.isEnabled = true
                        loadFeed()
                    }

                    override fun onFailure(value: User?) {
                        loadUser()
                    }
                })
    }

    private fun setupSwipeRefresh() {
        swipe_refresh_feed.isEnabled = false
        swipe_refresh_feed.isRefreshing = false
        swipe_refresh_feed.setColorSchemeColors(
                ContextCompat.getColor(
                        requireContext(),
                        R.color.color_pink_a200
                )
        )
        swipe_refresh_feed.setOnRefreshListener {
            loadFeed()
        }
    }

    private fun setupMenu() {
        toolbar_feed.inflateMenu(R.menu.feed_overflow_menu)
        toolbar_feed.setOnMenuItemClickListener(this::onOptionsItemSelected)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_direct_messages -> {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_go_to_direct_messages_from_feed)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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


    //TODO убрать костыль в запросе
    private fun loadFeed() {
        feedViewModel.feed(userViewModel.user.value!!.username!!, 0L, object : ResultCallback<List<RecipeSimpleObject>> {
            override fun onResult(value: List<RecipeSimpleObject>?) {
                swipe_refresh_feed.isRefreshing = false
                feedViewModel.recipes.value = value
                adapter!!.setData(feedViewModel.recipes.value, userViewModel.user.value)
            }

            override fun onFailure(value: List<RecipeSimpleObject>?) {
//                loadFeed()
                swipe_refresh_feed.isRefreshing = false
                Toast.makeText(requireContext(), "error feed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupAdapter() {
        val rv = rv_feed
        adapter = RecipeAdapter()
        adapter!!.setClickListener(this)
        adapter!!.setContext(requireContext())
//        (rv.itemAnimator as SimpleItemAnimator?)!!.supportsChangeAnimations = false
        rv.adapter = adapter
    }

    override fun onRecipeClick(position: Int) {
        Toast.makeText(requireContext(), "clicked $position", Toast.LENGTH_SHORT).show()
//        val recipe = userViewModel.feedRecipeList.value!![position]
//        recipeViewModel.selectedRecipe.value = recipe
//        recipeViewModel.selectedRecipePosition.value = position
//        NavHostFragment.findNavController(this).navigate(R.id.action_go_to_recipe_view_from_feed)
    }

    override fun onBookmarksClick(recipe: RecipeSimpleObject?, bookmark: Boolean, position: Int) {
        recipeBookmarked(recipe, bookmark, position)
    }

    private fun recipeBookmarked(recipe: RecipeSimpleObject?, bookmark: Boolean, position: Int) {
        Toast.makeText(requireContext(), "bookmarked", Toast.LENGTH_SHORT).show()
//        RetrofitFactory.apiService()
//                .recipeBookmarked(userViewModel.user.value!!.pid, recipe!!.pid, bookmark)
//                .enqueue(object : Callback<Recipe?> {
//                    override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
////                    FeedFragment.log.log(Level.INFO, "bookmark SUCCESS")
//                        recipeViewModel.selectedRecipe.value = response.body()
//                        loadUser(userViewModel.user.value!!.username!!, position)
//                    }
//
//                    override fun onFailure(call: Call<Recipe?>, t: Throwable) {
////                    FeedFragment.log.log(Level.INFO, "bookmark FAILED")
//                    }
//                })
    }

    override fun onForkClicked(recipe: RecipeSimpleObject?, fork: Boolean, position: Int) {
        recipeForked(recipe, fork, position)
    }

    private fun recipeForked(recipe: RecipeSimpleObject?, fork: Boolean, position: Int) {
        Toast.makeText(requireContext(), "forked", Toast.LENGTH_SHORT).show()
//        RetrofitFactory.apiService()
//                .recipeForked(userViewModel.user.value!!.pid, recipe!!.pid, fork)
//                .enqueue(object : Callback<Recipe?> {
//                    override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
////                    FeedFragment.log.log(Level.INFO, "fork SUCCESS")
//                        recipeViewModel.selectedRecipe.value = response.body()
//                        loadUser(userViewModel.user.value!!.username!!, position)
//                    }
//
//                    override fun onFailure(call: Call<Recipe?>, t: Throwable) {
////                    FeedFragment.log.log(Level.INFO, "fork FAILED")
//                    }
//                })
    }

    override fun onRecipeRevertDeleteClick(recipe: Recipe?) {
        //TODO revert delete recipe
        /**
         * post recipe again
         */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("===t=======FeedFragment==========", "onDestroy")
    }

    //TODO check if needed
//    override fun onResume() {
//        super.onResume()
//        binding!!.root.viewTreeObserver.addOnGlobalLayoutListener(
//                KeyboardActionUtil(
//                        binding!!.root, requireActivity()
//                ).listenerForAdjustResize
//        )
//    }
//
//    override fun onPause() {
//        super.onPause()
//        binding!!.root.viewTreeObserver.removeOnGlobalLayoutListener(
//                KeyboardActionUtil(
//                        binding!!.root, requireActivity()
//                ).listenerForAdjustResize
//        )
//    }

    override fun onBackPressedBase(): Boolean {
        Log.d(TAG, "onBackPressedBase")
        return false
    }

    override fun scrollUpBase() {
        Log.d(TAG, "scrollUpBase")
        nsv_userprofile.smoothScrollTo(0, 0)
        appBarLayout_feed.setExpanded(true, false)
    }

    //===========================================================================================

//    private fun addObservers() {
//        userViewModel.user.observe(viewLifecycleOwner) {
//            android.util.Log.d("TAG", " test ")
//        }
//        userViewModel.feedRecipeList.observe(viewLifecycleOwner) {
//            android.util.Log.d("TAG", " FEED ")
//            adapter!!.setData(it ?: ArrayList(), userViewModel.user.value)
//        }
//    }

//    private fun filter(text: String) {
//        try {
//            val temp: ArrayList<Recipe> = ArrayList()
//            for (r in userViewModel.feedRecipeList.value!!) {
//                if (StringUtils.containsIgnoreCase(
//                                r.title, text
//                        )
//                ) {
//                    temp.add(r)
//                }
//            }
//            adapter!!.setData(temp)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    private fun loadUser1(login: String, position: Int?) {
//        if (userViewModel.user.value == null) {
        if (login == null) {
            val sharedPreferences =
                    requireActivity().getSharedPreferences(Constants.APP_PREFS, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(Constants.USER_LOGIN, StringUtils.EMPTY)
            editor.apply()
            startActivity(Intent(requireActivity(), SplashActivity::class.java))
            requireActivity().finish()
        } else {
            RetrofitFactory.apiService().getUserByUsername(login)
                    .enqueue(object : Callback<User?> {
                        override fun onResponse(call: Call<User?>, response: Response<User?>) {
                            if (response.code() == 200) {
//                            FeedFragment.log.log(Level.INFO, "user load SUCCESS")
                                userViewModel.user.value = response.body()
                                println(userViewModel.user.value)
                                adapter!!.setUser(userViewModel.user.value!!)
//                            if (position != null) {
//                                if (recipeViewModel.selectedRecipe.value != null) {
//                                    userViewModel.feedRecipeList.value!![position].forks =
//                                        recipeViewModel.selectedRecipe.value!!.forks
//                                    adapter!!.notifyItemChanged(position)
//                                } else {
//                                    userViewModel.feedRecipeList.value!!.removeAt(position)
//                                    adapter!!.notifyItemRemoved(position)
//                                }
//                                recipeViewModel.selectedRecipe.value = null
//                                recipeViewModel.selectedRecipePosition.value = null
//                            } else {
//                                adapter!!.notifyDataSetChanged()
//                            }
                            } else {
//                            FeedFragment.log.log(Level.INFO, "user load FAILED " + response.code())
                            }
                            if (binding != null) {
                                swipe_refresh_feed.isRefreshing = false
                            }
                        }

                        override fun onFailure(call: Call<User?>, t: Throwable) {
//                        FeedFragment.log.log(Level.INFO, "user load FAILED")
                        }
                    })
        }
    }


}