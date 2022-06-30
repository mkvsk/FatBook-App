package online.fatbook.fatbookapp.ui.fragment

import android.content.*
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.SimpleItemAnimator
import lombok.extern.java.Log
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.*
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
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.logging.Level

@Log
class FeedFragment : Fragment(), OnRecipeClickListener, OnRecipeRevertDeleteListener {
    private var binding: FragmentFeedBinding? = null
    private var feedRecipeList: ArrayList<Recipe>? = null
    private var recipeViewModel: RecipeViewModel? = null
    private var userViewModel: UserViewModel? = null
    private var user: User? = null
    private var adapter: RecipeAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        recipeViewModel = ViewModelProvider(requireActivity())[RecipeViewModel::class.java]
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        if (recipeViewModel!!.selectedRecipePosition.value != null) {
            if (user != null) {
                loadUser(recipeViewModel!!.selectedRecipePosition.value)
            }
        }
        feedRecipeList = ArrayList()
        userViewModel!!.user.observe(viewLifecycleOwner) { userUpdated: User? ->
            user = userUpdated
            if (userViewModel!!.feedRecipeList.value == null) {
                loadData()
            }
        }
        binding!!.swipeRefreshBookmarks.setColorSchemeColors(resources.getColor(R.color.color_pink_a200))
        binding!!.swipeRefreshBookmarks.setOnRefreshListener { loadData() }
        userViewModel!!.feedRecipeList.observe(viewLifecycleOwner) { recipeList: ArrayList<Recipe> ->
            feedRecipeList = recipeList
            if (feedRecipeList == null) {
                feedRecipeList = ArrayList()
            }
            adapter!!.setData(recipeList, user)
            adapter!!.notifyDataSetChanged()
        }
        setupAdapter()
        feedRecipeList = userViewModel!!.feedRecipeList.value
        setupSearch()
    }

    private fun setupSearch() {
        binding!!.editTextFeedSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                filter(editable.toString())
            }
        })
    }

    private fun filter(text: String) {
        try {
            val temp: MutableList<Recipe?> = ArrayList()
            for (r in feedRecipeList!!) {
                if (StringUtils.containsIgnoreCase(
                        r.name,
                        text
                    ) || StringUtils.containsIgnoreCase(r.description, text)
                ) {
                    temp.add(r)
                }
            }
            adapter!!.updateList(temp)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupAdapter() {
        val recyclerView = binding!!.rvFeed
        adapter = RecipeAdapter(binding!!.root.context, feedRecipeList, user, this)
        (recyclerView.itemAnimator as SimpleItemAnimator?)!!.supportsChangeAnimations = false
        recyclerView.adapter = adapter
    }

    //TODO убрать костыль в запросе
    private fun loadData() {
        RetrofitFactory.apiServiceClient().getFeed(0L).enqueue(object : Callback<ArrayList<Recipe>?> {
            override fun onResponse(
                call: Call<ArrayList<Recipe>?>,
                response: Response<ArrayList<Recipe>?>
            ) {
                userViewModel!!.recipeList.value = response.body()
//                FeedFragment.log.log(Level.INFO, "feed data load: SUCCESS")
                if (response.body() != null) {
//                    FeedFragment.log.log(
//                        Level.INFO,
//                        "loaded " + response.body()!!.size + " recipes"
//                    )
                }
                loadUser(null)
            }

            override fun onFailure(call: Call<ArrayList<Recipe>?>, t: Throwable) {
                userViewModel!!.recipeList.value = ArrayList()
//                FeedFragment.log.log(Level.INFO, "feed data load: FAILED")
            }
        })
    }

    override fun onRecipeClick(position: Int) {
        val recipe = feedRecipeList!![position]
        recipeViewModel!!.selectedRecipe.value = recipe
        recipeViewModel!!.selectedRecipePosition.value = position
        NavHostFragment.findNavController(this).navigate(R.id.action_go_to_recipe_view)
    }

    override fun onBookmarksClick(recipe: Recipe?, bookmark: Boolean, position: Int) {
        recipeBookmarked(recipe, bookmark, position)
    }

    private fun recipeBookmarked(recipe: Recipe?, bookmark: Boolean, position: Int) {
        RetrofitFactory.apiServiceClient()
            .recipeBookmarked(user!!.pid, recipe!!.pid, bookmark)
            .enqueue(object : Callback<Recipe?> {
                override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
//                    FeedFragment.log.log(Level.INFO, "bookmark SUCCESS")
                    recipeViewModel!!.selectedRecipe.value = response.body()
                    loadUser(position)
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
        RetrofitFactory.apiServiceClient().recipeForked(user!!.pid, recipe!!.pid, fork)
            .enqueue(object : Callback<Recipe?> {
                override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
//                    FeedFragment.log.log(Level.INFO, "fork SUCCESS")
                    recipeViewModel!!.selectedRecipe.value = response.body()
                    loadUser(position)
                }

                override fun onFailure(call: Call<Recipe?>, t: Throwable) {
//                    FeedFragment.log.log(Level.INFO, "fork FAILED")
                }
            })
    }

    private fun loadUser(position: Int?) {
        if (user == null) {
            val sharedPreferences =
                requireActivity().getSharedPreferences(UserUtils.APP_PREFS, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(UserUtils.USER_LOGIN, StringUtils.EMPTY)
            editor.apply()
            startActivity(Intent(requireActivity(), SplashActivity::class.java))
            requireActivity().finish()
        } else {
            RetrofitFactory.apiServiceClient().getUser(user!!.login)
                .enqueue(object : Callback<User?> {
                    override fun onResponse(call: Call<User?>, response: Response<User?>) {
                        if (response.code() == 200) {
//                            FeedFragment.log.log(Level.INFO, "user load SUCCESS")
                            userViewModel!!.user.value = response.body()
                            println(user)
                            adapter!!.setUser(user)
                            if (position != null) {
                                if (recipeViewModel!!.selectedRecipe.value != null) {
                                    feedRecipeList!![position].forks = recipeViewModel!!.selectedRecipe.value!!.forks
                                    adapter!!.notifyItemChanged(position)
                                } else {
                                    feedRecipeList!!.removeAt(position)
                                    adapter!!.notifyItemRemoved(position)
                                }
                                recipeViewModel!!.selectedRecipe.value = null
                                recipeViewModel!!.selectedRecipePosition.value = null
                            } else {
                                adapter!!.notifyDataSetChanged()
                            }
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