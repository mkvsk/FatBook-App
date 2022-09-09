package online.fatbook.fatbookapp.ui.fragment.feed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlinx.android.synthetic.main.fragment_feed.*
import lombok.extern.java.Log
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.Recipe
import online.fatbook.fatbookapp.core.User
import online.fatbook.fatbookapp.databinding.FragmentFeedBinding
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import online.fatbook.fatbookapp.ui.activity.SplashActivity
import online.fatbook.fatbookapp.ui.adapters.RecipeAdapter
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener
import online.fatbook.fatbookapp.ui.listeners.OnRecipeRevertDeleteListener
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.KeyboardActionUtil
import online.fatbook.fatbookapp.util.UserUtils
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Log
class FeedFragment : Fragment(), OnRecipeClickListener, OnRecipeRevertDeleteListener {
    private var binding: FragmentFeedBinding? = null
    private var adapter: RecipeAdapter? = null

    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

//        if (recipeViewModel.selectedRecipePosition.value != null) {
//            if (userViewModel.user.value != null) {
//                loadUser(recipeViewModel.selectedRecipePosition.value)
//            }
//        }

        binding!!.swipeRefreshBookmarks.setColorSchemeColors(resources.getColor(R.color.color_pink_a200))
        binding!!.swipeRefreshBookmarks.setOnRefreshListener { loadData() }
        setupAdapter()
        userViewModel.feedRecipeList.value = userViewModel.feedRecipeList.value
        setupSearch()
        addObservers()

        loadUser(requireActivity().getSharedPreferences(UserUtils.APP_PREFS, Context.MODE_PRIVATE).getString(UserUtils.USER_LOGIN, StringUtils.EMPTY)!!, null)
    }

    private fun addObservers() {
        userViewModel.user.observe(viewLifecycleOwner) {
            android.util.Log.d("TAG", " test ")
        }
        userViewModel.feedRecipeList.observe(viewLifecycleOwner) {
            android.util.Log.d("TAG", " FEED ")
            adapter!!.setData(it ?: ArrayList(), userViewModel.user.value)
        }
    }

    private fun setupSearch() {
       /* binding!!.editTextFeedSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                filter(editable.toString())
            }
        })*/
    }

    private fun filter(text: String) {
        try {
            val temp: ArrayList<Recipe> = ArrayList()
            for (r in userViewModel.feedRecipeList.value!!) {
                if (StringUtils.containsIgnoreCase(
                        r.name,
                        text
                    ) || StringUtils.containsIgnoreCase(r.description, text)
                ) {
                    temp.add(r)
                }
            }
            adapter!!.setData(temp)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupAdapter() {
        val recyclerView = binding!!.rvFeed
        adapter = RecipeAdapter()
        adapter!!.setData(ArrayList(), userViewModel.user.value)
        adapter!!.setClickListener(this)
        (recyclerView.itemAnimator as SimpleItemAnimator?)!!.supportsChangeAnimations = false
        recyclerView.adapter = adapter
    }

    //TODO убрать костыль в запросе
    private fun loadData() {
        RetrofitFactory.apiServiceClient().getFeed(0L)
            .enqueue(object : Callback<ArrayList<Recipe>?> {
                override fun onResponse(
                    call: Call<ArrayList<Recipe>?>,
                    response: Response<ArrayList<Recipe>?>
                ) {
                    if (response.body() != null) {
                        userViewModel.feedRecipeList.value = response.body()
                    }
                    swipe_refresh_bookmarks.isRefreshing = false
//                    loadUser(null)
                }

                override fun onFailure(call: Call<ArrayList<Recipe>?>, t: Throwable) {
                    userViewModel.feedRecipeList.value = ArrayList()
                }
            })
    }

    override fun onRecipeClick(position: Int) {
        val recipe = userViewModel.feedRecipeList.value!![position]
        recipeViewModel.selectedRecipe.value = recipe
        recipeViewModel.selectedRecipePosition.value = position
        NavHostFragment.findNavController(this).navigate(R.id.action_go_to_recipe_view)
    }

    override fun onBookmarksClick(recipe: Recipe?, bookmark: Boolean, position: Int) {
        recipeBookmarked(recipe, bookmark, position)
    }

    private fun recipeBookmarked(recipe: Recipe?, bookmark: Boolean, position: Int) {
        RetrofitFactory.apiServiceClient()
            .recipeBookmarked(userViewModel.user.value!!.pid, recipe!!.pid, bookmark)
            .enqueue(object : Callback<Recipe?> {
                override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
//                    FeedFragment.log.log(Level.INFO, "bookmark SUCCESS")
                    recipeViewModel.selectedRecipe.value = response.body()
                    loadUser(userViewModel.user.value!!.login!!, position)
                }

                override fun onFailure(call: Call<Recipe?>, t: Throwable) {
//                    FeedFragment.log.log(Level.INFO, "bookmark FAILED")
                }
            })
    }

    override fun onForkClicked(recipe: Recipe?, fork: Boolean, position: Int) {
        recipeForked(recipe, fork, position)
    }

    private fun recipeForked(recipe: Recipe?, fork: Boolean, position: Int) {
        RetrofitFactory.apiServiceClient().recipeForked(userViewModel.user.value!!.pid, recipe!!.pid, fork)
            .enqueue(object : Callback<Recipe?> {
                override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
//                    FeedFragment.log.log(Level.INFO, "fork SUCCESS")
                    recipeViewModel.selectedRecipe.value = response.body()
                    loadUser(userViewModel.user.value!!.login!!, position)
                }

                override fun onFailure(call: Call<Recipe?>, t: Throwable) {
//                    FeedFragment.log.log(Level.INFO, "fork FAILED")
                }
            })
    }

    private fun loadUser(login: String, position: Int?) {
//        if (userViewModel.user.value == null) {
        if (login == null) {
            val sharedPreferences =
                requireActivity().getSharedPreferences(UserUtils.APP_PREFS, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(UserUtils.USER_LOGIN, StringUtils.EMPTY)
            editor.apply()
            startActivity(Intent(requireActivity(), SplashActivity::class.java))
            requireActivity().finish()
        } else {
            RetrofitFactory.apiServiceClient().getUser(login)
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
                            binding!!.swipeRefreshBookmarks.isRefreshing = false
                        }
                    }

                    override fun onFailure(call: Call<User?>, t: Throwable) {
//                        FeedFragment.log.log(Level.INFO, "user load FAILED")
                    }
                })
        }
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
        binding!!.root.viewTreeObserver.addOnGlobalLayoutListener(
            KeyboardActionUtil(
                binding!!.root, requireActivity()
            ).listenerForAdjustResize
        )
    }

    override fun onPause() {
        super.onPause()
        binding!!.root.viewTreeObserver.removeOnGlobalLayoutListener(
            KeyboardActionUtil(
                binding!!.root, requireActivity()
            ).listenerForAdjustResize
        )
    }
}