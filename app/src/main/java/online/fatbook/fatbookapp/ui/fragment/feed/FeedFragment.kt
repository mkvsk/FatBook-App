package online.fatbook.fatbookapp.ui.fragment.feed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import okhttp3.MultipartBody
import okhttp3.RequestBody
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.FragmentFeedBinding
import online.fatbook.fatbookapp.network.service.RetrofitFactory
import online.fatbook.fatbookapp.ui.activity.SplashActivity
import online.fatbook.fatbookapp.ui.adapters.RecipeAdapter
import online.fatbook.fatbookapp.ui.listeners.BaseFragmentActionsListener
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener
import online.fatbook.fatbookapp.ui.listeners.OnRecipeRevertDeleteListener
import online.fatbook.fatbookapp.ui.viewmodel.*
import online.fatbook.fatbookapp.util.Constants
import online.fatbook.fatbookapp.util.KeyboardActionUtil
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedFragment : Fragment(), OnRecipeClickListener, OnRecipeRevertDeleteListener,
    BaseFragmentActionsListener {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private var adapter: RecipeAdapter? = null
    private var rv: RecyclerView? = null

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }
    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private val feedViewModel by lazy { obtainViewModel(FeedViewModel::class.java) }
    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }

    final val KEY_RECYCLER_STATE = "recycler_state"
    private var mBundleRecyclerViewState: Bundle? = null
    private var mListState: Parcelable? = null
    private var mRecyclerView: RecyclerView? = null

    companion object {
        private const val TAG = "FeedFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("===t=======FeedFragment==========", "onViewCreated")

        initViews()
        initListeners()
        initObservers()
        setupSwipeRefresh()
        setupMenu()
        setupAdapter()

        feedViewModel.setIsLoading(true)

        binding.toolbarFeed.visibility = View.GONE
//        if (!authViewModel.isUserAuthenticated.value!!) {
//            login()
//        } else {
//            if (userViewModel.user.value == null) {
//                loadUser()
//            } else {
//                if (feedViewModel.recipes.value == null) {
//                    loadFeed()
//                } else {
//                    drawFeed()
//                }
//            }
//        }
    }

    private fun initListeners() {

    }

    private fun initViews() {
//        binding.rvFeed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//            }
//        })
    }

    private fun initObservers() {
        authViewModel.isUserAuthenticated.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    loadFeed()
                }
                else -> {
                    login()
                }
            }
        }

        feedViewModel.recipes.observe(viewLifecycleOwner) {
            when(it) {
                null -> {
                    loadFeed()
                }
                else -> {
                    drawFeed()
                }
            }
        }

        authViewModel.resultCode.observe(viewLifecycleOwner) {
            when (it) {
                0 -> {
                    logout()
                }
                1 -> {
                    loadUser()
                }
            }
        }

        feedViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.loader.progressOverlay.visibility = View.VISIBLE
                binding.toolbarFeed.visibility = View.GONE
            } else {
                binding.loader.progressOverlay.visibility = View.GONE
                binding.toolbarFeed.visibility = View.VISIBLE
            }
        }

        userViewModel.resultCode.observe(viewLifecycleOwner) {
            when (it) {
                0 -> {
                    feedViewModel.setIsLoading(false)
                    loadFeed()
                }
                else -> {}
            }
        }


    }

    private fun login() {
        val request: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("username", authViewModel.username.value!!)
            .addFormDataPart("password", authViewModel.password.value!!).build()
        authViewModel.loginFeed(request)
    }

//    private fun saveTokens(value: LoginResponse) {
////        authViewModel.setJwtAccess(value.access_token.toString())
////        authViewModel.setJwtRefresh(value.refresh_token.toString())
////        RetrofitFactory.updateJWT(value.access_token!!, value.username!!)
//        loadUser()
//    }

    private fun loadUser() {
        userViewModel.getUserByUsername(
            authViewModel.username.value!!
        )
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshFeed.isEnabled = false
        binding.swipeRefreshFeed.isRefreshing = false
        binding.swipeRefreshFeed.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                requireContext(), R.color.theme_primary_bgr
            )
        )
        binding.swipeRefreshFeed.setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(), R.color.color_pink_a200
            )
        )
        binding.swipeRefreshFeed.setOnRefreshListener {
            loadFeed()
        }
    }

    private fun setupMenu() {
        binding.toolbarFeed.inflateMenu(R.menu.feed_overflow_menu)
        binding.toolbarFeed.setOnMenuItemClickListener(this::onOptionsItemSelected)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_direct_messages -> {
                findNavController().navigate(R.id.action_go_to_direct_messages_from_feed)
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
        feedViewModel.feed(0L, object : ResultCallback<List<RecipeSimpleObject>> {
            override fun onResult(value: List<RecipeSimpleObject>?) {
                binding.swipeRefreshFeed.isRefreshing = false
                feedViewModel.setRecipes(value as List<RecipeSimpleObject>)
                drawFeed()
            }

            override fun onFailure(value: List<RecipeSimpleObject>?) {
                binding.swipeRefreshFeed.isRefreshing = false
                Toast.makeText(requireContext(), "error feed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun drawFeed() {
        adapter!!.setData(feedViewModel.recipes.value, userViewModel.user.value)
        binding.swipeRefreshFeed.isEnabled = true
        feedViewModel.setIsLoading(false)
    }

    private fun setupAdapter() {
        rv = binding.rvFeed
        mRecyclerView = rv
        adapter = RecipeAdapter()
        adapter!!.setClickListener(this)
        adapter!!.setContext(requireContext())
//        (rv.itemAnimator as SimpleItemAnimator?)!!.supportsChangeAnimations = false
        rv!!.adapter = adapter
        rv!!.adapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun onRecipeClick(id: Long) {
        recipeViewModel.setSelectedRecipeId(id)
        NavHostFragment.findNavController(this).navigate(R.id.action_go_to_recipe_view_from_feed)
    }

    override fun onBookmarksClick(recipe: RecipeSimpleObject?, bookmark: Boolean, position: Int) {
        recipeBookmarked(recipe, bookmark)
    }

    private fun recipeBookmarked(recipe: RecipeSimpleObject?, bookmark: Boolean) {
        Toast.makeText(requireContext(), "bookmarked", Toast.LENGTH_SHORT).show()
        RetrofitFactory.apiService()
            .recipeBookmarked(userViewModel.user.value!!.pid, recipe!!.pid, bookmark)
            .enqueue(object : Callback<Recipe?> {
                override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
                    Log.d(TAG, "onResponse: bookmark SUCCESS")
                    recipeViewModel.setSelectedRecipe(response.body())
                    loadUser()
                }

                override fun onFailure(call: Call<Recipe?>, t: Throwable) {
                    Log.d(TAG, "onResponse: bookmark FAILED")
                }
            })
    }

    override fun onForkClicked(recipe: RecipeSimpleObject?, fork: Boolean, position: Int) {
        recipeForked(recipe, fork)
    }

    override fun onUsernameClick(username: String) {
        userViewModel.setSelectedUsername(username)
        findNavController().navigate(R.id.action_go_to_user_profile_from_feed)
    }

    private fun recipeForked(recipe: RecipeSimpleObject?, fork: Boolean) {
        Toast.makeText(requireContext(), "forked", Toast.LENGTH_SHORT).show()



        RetrofitFactory.apiService()
            .recipeForked(userViewModel.user.value!!.pid, recipe!!.pid, fork)
            .enqueue(object : Callback<Recipe?> {
                override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
                    Log.d(TAG, "onResponse: fork SUCCESS")
                    recipeViewModel.setSelectedRecipe(response.body())
                    loadUser()
                }

                override fun onFailure(call: Call<Recipe?>, t: Throwable) {
                    Log.d(TAG, "onResponse: fork FAILED")
                }
            })
    }

    override fun onRecipeRevertDeleteClick(recipe: Recipe?) {
        //TODO revert delete recipe
        /**
         * post recipe again
         */
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("===t=======FeedFragment==========", "onDestroy")
        _binding = null
    }

    //TODO check if needed
    override fun onResume() {
        super.onResume()

        if (mBundleRecyclerViewState != null) {
            Looper.myLooper()?.let {
                Handler(it).post {
                    mListState = mBundleRecyclerViewState?.getParcelable(KEY_RECYCLER_STATE)
                    mRecyclerView!!.layoutManager?.onRestoreInstanceState(mListState)
                }
            }
        }

//        binding!!.root.viewTreeObserver.addOnGlobalLayoutListener(
//                KeyboardActionUtil(
//                        binding!!.root, requireActivity()
//                ).listenerForAdjustResize
//        )
    }

    override fun onPause() {
        super.onPause()

        mBundleRecyclerViewState = Bundle()
        mListState = mRecyclerView!!.layoutManager?.onSaveInstanceState()
        mBundleRecyclerViewState?.putParcelable(KEY_RECYCLER_STATE, mListState)

//        binding!!.root.viewTreeObserver.removeOnGlobalLayoutListener(
//                KeyboardActionUtil(
//                        binding!!.root, requireActivity()
//                ).listenerForAdjustResize
//        )
    }

    override fun onBackPressedBase(): Boolean {
        Log.d(TAG, "onBackPressedBase")
        return false
    }

    override fun scrollUpBase() {
        Log.d(TAG, "scrollUpBase")
        binding.nsvUserprofile.smoothScrollTo(0, 0)
        binding.appBarLayoutFeed.setExpanded(true, false)
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
            RetrofitFactory.apiService().getUserByUsername(login).enqueue(object : Callback<User?> {
                override fun onResponse(call: Call<User?>, response: Response<User?>) {
                    if (response.code() == 200) {
//                            FeedFragment.log.log(Level.INFO, "user load SUCCESS")
                        response.body()?.let { userViewModel.setUser(it) }
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
                        binding.swipeRefreshFeed.isRefreshing = false
                    }
                }

                override fun onFailure(call: Call<User?>, t: Throwable) {
//                        FeedFragment.log.log(Level.INFO, "user load FAILED")
                }
            })
        }
    }
}