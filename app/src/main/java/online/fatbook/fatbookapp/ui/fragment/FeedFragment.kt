package online.fatbook.fatbookapp.ui.fragment

import androidx.lifecycle.ViewModelProvider.get
import androidx.navigation.NavController.navigate
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.NavController.popBackStack
import androidx.appcompat.app.AppCompatActivity
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.ui.viewmodel.IngredientViewModel
import androidx.navigation.NavController
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import online.fatbook.fatbookapp.util.UserUtils
import online.fatbook.fatbookapp.R
import com.google.android.material.navigation.NavigationBarView
import online.fatbook.fatbookapp.ui.viewmodel.SignInViewModel
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.content.ContextCompat
import online.fatbook.fatbookapp.ui.activity.PasswordActivity
import android.text.TextWatcher
import android.text.Editable
import androidx.appcompat.content.res.AppCompatResources
import online.fatbook.fatbookapp.ui.activity.LoginActivity
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import online.fatbook.fatbookapp.ui.activity.MainActivity
import online.fatbook.fatbookapp.ui.activity.SignInActivity
import online.fatbook.fatbookapp.ui.activity.WelcomeActivity
import online.fatbook.fatbookapp.ui.activity.SplashActivity
import androidx.activity.result.ActivityResultLauncher
import online.fatbook.fatbookapp.ui.activity.fill_additional_info.FillAdditionalInfoViewModel
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.ActivityResultCallback
import online.fatbook.fatbookapp.ui.activity.fill_additional_info.FillAdditionalInfoActivity
import android.app.Activity
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import online.fatbook.fatbookapp.ui.activity.SkipAdditionalInfoActivity
import android.widget.Toast
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import online.fatbook.fatbookapp.util.RecipeUtils
import android.widget.TextView
import android.widget.ImageButton
import online.fatbook.fatbookapp.ui.listeners.OnRecipeViewDeleteIngredient
import online.fatbook.fatbookapp.ui.listeners.OnAddIngredientItemClickListener
import android.annotation.SuppressLint
import android.content.*
import androidx.cardview.widget.CardView
import online.fatbook.fatbookapp.ui.listeners.OnRecipeRevertDeleteListener
import online.fatbook.fatbookapp.ui.adapters.RecipeAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import androidx.recyclerview.widget.SimpleItemAnimator
import online.fatbook.fatbookapp.ui.fragment.FeedFragment
import androidx.navigation.fragment.NavHostFragment
import online.fatbook.fatbookapp.util.KeyboardActionUtil
import online.fatbook.fatbookapp.ui.fragment.BookmarksFragment
import online.fatbook.fatbookapp.ui.adapters.ViewRecipeIngredientAdapter
import online.fatbook.fatbookapp.ui.fragment.RecipeViewFragment
import okhttp3.RequestBody
import okhttp3.MultipartBody
import android.widget.FrameLayout
import online.fatbook.fatbookapp.ui.adapters.IngredientsAdapter
import online.fatbook.fatbookapp.ui.fragment.IngredientsFragment
import android.widget.EditText
import android.content.DialogInterface.OnShowListener
import com.google.android.material.appbar.AppBarLayout
import android.graphics.PorterDuff
import androidx.fragment.app.FragmentActivity
import online.fatbook.fatbookapp.ui.fragment.UserProfileFragment
import online.fatbook.fatbookapp.ui.fragment.RecipeCreateFragment
import online.fatbook.fatbookapp.ui.adapters.AddIngredientToRecipeAdapter
import online.fatbook.fatbookapp.ui.fragment.RecipeAddIngredientFragment
import androidx.lifecycle.ViewModel
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.os.Environment
import android.text.TextUtils
import android.provider.OpenableColumns
import android.view.*
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import lombok.extern.java.Log
import online.fatbook.fatbookapp.core.*
import online.fatbook.fatbookapp.databinding.FragmentFeedBinding
import online.fatbook.fatbookapp.retrofit.NetworkInfoService
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Multipart
import java.lang.Exception
import java.util.ArrayList
import java.util.logging.Level

@Log
class FeedFragment : Fragment(), OnRecipeClickListener, OnRecipeRevertDeleteListener {
    private var binding: FragmentFeedBinding? = null
    private var feedRecipeList: List<Recipe?>? = null
    private var recipeViewModel: RecipeViewModel? = null
    private var userViewModel: UserViewModel? = null
    private var user: User? = null
    private var adapter: RecipeAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        recipeViewModel = ViewModelProvider(requireActivity()).get(RecipeViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
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
        userViewModel!!.feedRecipeList.observe(viewLifecycleOwner) { recipeList: List<Recipe?> ->
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
                        r.getName(),
                        text
                    ) || StringUtils.containsIgnoreCase(r.getDescription(), text)
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
        RetrofitFactory.apiServiceClient().getFeed(0L).enqueue(object : Callback<List<Recipe?>?> {
            override fun onResponse(
                call: Call<List<Recipe?>?>,
                response: Response<List<Recipe?>?>
            ) {
                userViewModel!!.setFeedRecipeList(response.body())
                FeedFragment.log.log(Level.INFO, "feed data load: SUCCESS")
                if (response.body() != null) {
                    FeedFragment.log.log(
                        Level.INFO,
                        "loaded " + response.body()!!.size + " recipes"
                    )
                }
                loadUser(null)
            }

            override fun onFailure(call: Call<List<Recipe?>?>, t: Throwable) {
                userViewModel!!.setFeedRecipeList(ArrayList())
                FeedFragment.log.log(Level.INFO, "feed data load: FAILED")
            }
        })
    }

    override fun onRecipeClick(position: Int) {
        val recipe = feedRecipeList!![position]
        recipeViewModel!!.setSelectedRecipe(recipe)
        recipeViewModel!!.setSelectedRecipePosition(position)
        NavHostFragment.findNavController(this).navigate(R.id.action_go_to_recipe_view)
    }

    override fun onBookmarksClick(recipe: Recipe?, bookmark: Boolean, position: Int) {
        recipeBookmarked(recipe, bookmark, position)
    }

    private fun recipeBookmarked(recipe: Recipe?, bookmark: Boolean, position: Int) {
        RetrofitFactory.apiServiceClient()
            .recipeBookmarked(user.getPid(), recipe.getPid(), bookmark)
            .enqueue(object : Callback<Recipe?> {
                override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
                    FeedFragment.log.log(Level.INFO, "bookmark SUCCESS")
                    recipeViewModel!!.setSelectedRecipe(response.body())
                    loadUser(position)
                }

                override fun onFailure(call: Call<Recipe?>, t: Throwable) {
                    FeedFragment.log.log(Level.INFO, "bookmark FAILED")
                }
            })
    }

    override fun onForkClicked(recipe: Recipe?, fork: Boolean, position: Int) {
        recipeForked(recipe, fork, position)
    }

    private fun recipeForked(recipe: Recipe?, fork: Boolean, position: Int) {
        RetrofitFactory.apiServiceClient().recipeForked(user.getPid(), recipe.getPid(), fork)
            .enqueue(object : Callback<Recipe?> {
                override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
                    FeedFragment.log.log(Level.INFO, "fork SUCCESS")
                    recipeViewModel!!.setSelectedRecipe(response.body())
                    loadUser(position)
                }

                override fun onFailure(call: Call<Recipe?>, t: Throwable) {
                    FeedFragment.log.log(Level.INFO, "fork FAILED")
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
            RetrofitFactory.apiServiceClient().getUser(user.getLogin())
                .enqueue(object : Callback<User?> {
                    override fun onResponse(call: Call<User?>, response: Response<User?>) {
                        if (response.code() == 200) {
                            FeedFragment.log.log(Level.INFO, "user load SUCCESS")
                            userViewModel!!.setUser(response.body())
                            println(user)
                            adapter!!.setUser(user)
                            if (position != null) {
                                if (recipeViewModel.getSelectedRecipe().value != null) {
                                    feedRecipeList!![position].setForks(recipeViewModel.getSelectedRecipe().value.getForks())
                                    adapter!!.notifyItemChanged(position)
                                } else {
                                    feedRecipeList.removeAt(position)
                                    adapter!!.notifyItemRemoved(position)
                                }
                                recipeViewModel!!.setSelectedRecipe(null)
                                recipeViewModel!!.setSelectedRecipePosition(null)
                            } else {
                                adapter!!.notifyDataSetChanged()
                            }
                        } else {
                            FeedFragment.log.log(Level.INFO, "user load FAILED " + response.code())
                        }
                        if (binding != null) {
                            binding!!.swipeRefreshBookmarks.isRefreshing = false
                        }
                    }

                    override fun onFailure(call: Call<User?>, t: Throwable) {
                        FeedFragment.log.log(Level.INFO, "user load FAILED")
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
    ): View? {
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