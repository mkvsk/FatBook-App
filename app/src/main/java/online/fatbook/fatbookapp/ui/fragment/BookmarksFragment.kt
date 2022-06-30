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
import android.content.SharedPreferences
import com.google.android.material.navigation.NavigationBarView
import online.fatbook.fatbookapp.ui.viewmodel.SignInViewModel
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.content.ContextCompat
import android.content.Intent
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
import android.content.DialogInterface
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
import android.content.ContentUris
import android.provider.OpenableColumns
import android.view.*
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import lombok.extern.java.Log
import online.fatbook.fatbookapp.core.*
import online.fatbook.fatbookapp.databinding.FragmentBookmarksBinding
import online.fatbook.fatbookapp.retrofit.NetworkInfoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Multipart
import java.util.ArrayList
import java.util.logging.Level

@Log
class BookmarksFragment : Fragment(), OnRecipeClickListener {
    private var binding: FragmentBookmarksBinding? = null
    private var userViewModel: UserViewModel? = null
    private var recipeViewModel: RecipeViewModel? = null
    private var recipeList: ArrayList<Recipe?>? = null
    private var adapter: RecipeAdapter? = null
    private var user: User? = null
    private var userUpdated = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.swipeRefreshBookmarks.setColorSchemeColors(resources.getColor(R.color.color_pink_a200))
        binding!!.swipeRefreshBookmarks.setOnRefreshListener {
            userUpdated = false
            loadRecipes()
        }
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        recipeViewModel = ViewModelProvider(requireActivity()).get(RecipeViewModel::class.java)
        user = userViewModel!!.user.value
        recipeList = ArrayList()
        userViewModel!!.user.observe(viewLifecycleOwner) { _user: User? ->
            user = _user
            if (!userUpdated) {
                loadRecipes()
            }
        }
        setupAdapter()
    }

    private fun loadRecipes() {
        RetrofitFactory.apiServiceClient().getUserBookmarks(user!!.login)
            .enqueue(object : Callback<List<Recipe?>?> {
                override fun onResponse(
                    call: Call<List<Recipe?>?>,
                    response: Response<List<Recipe?>?>
                ) {
                    binding!!.swipeRefreshBookmarks.isRefreshing = false
                    if (response.code() == 200) {
//                        log(Level.INFO, "bookmarks load SUCCESS")
                        if (response.body() != null) {
//                            BookmarksFragment.log.log(Level.INFO, response.body().toString())
                        }
                        recipeList = response.body() as ArrayList<Recipe?>?
                        adapter!!.setData(recipeList, user)
                        adapter!!.notifyDataSetChanged()
                    } else {
//                        BookmarksFragment.log.log(Level.INFO, "bookmarks load FAILED")
                    }
                }

                override fun onFailure(call: Call<List<Recipe?>?>, t: Throwable) {
                    binding!!.swipeRefreshBookmarks.isRefreshing = false
//                    BookmarksFragment.log.log(Level.INFO, "bookmarks load FAILED")
                }
            })
    }

    private fun setupAdapter() {
        val recyclerView = binding!!.rvBookmarks
        adapter = RecipeAdapter(binding!!.root.context, recipeList, user, this)
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onRecipeClick(position: Int) {
        val recipe = recipeList!![position]
        recipeViewModel!!.setSelectedRecipe(recipe)
        recipeViewModel!!.setSelectedRecipePosition(position)
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_go_to_recipe_view_from_bookmarks)
    }

    override fun onBookmarksClick(recipe: Recipe?, bookmark: Boolean, position: Int) {
        recipeList.removeAt(position)
        adapter!!.notifyItemRemoved(position)
        user!!.recipesBookmarked!!.remove(recipe.iden)
        saveUser()
    }

    private fun saveUser() {
        RetrofitFactory.apiServiceClient().userUpdate(user).enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                if (response.code() == 200) {
//                    BookmarksFragment.log.log(Level.INFO, "user update SUCCESS")
                    if (response.body() != null) {
//                        BookmarksFragment.log.log(Level.INFO, response.body().toString())
                    }
                    userViewModel!!.setUser(response.body())
                    userUpdated = true
                } else {
//                    BookmarksFragment.log.log(Level.INFO, "user update FAILED")
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
//                BookmarksFragment.log.log(Level.INFO, "user update FAILED")
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
                    if (response.code() == 200) {
                        BookmarksFragment.log.log(Level.INFO, "fork SUCCESS")
                        recipeViewModel!!.setSelectedRecipe(response.body())
                        loadUser()
                    } else {
                        BookmarksFragment.log.log(Level.INFO, "fork FAILED")
                    }
                }

                override fun onFailure(call: Call<Recipe?>, t: Throwable) {
                    BookmarksFragment.log.log(Level.INFO, "fork FAILED")
                }
            })
    }

    private fun loadUser() {
        RetrofitFactory.apiServiceClient().getUser(user.getLogin())
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.code() == 200) {
                        BookmarksFragment.log.log(Level.INFO, "load user SUCCESS")
                        BookmarksFragment.log.log(
                            Level.INFO,
                            "" + response.code() + " found user: " + response.body()
                        )
                        userViewModel!!.setUser(response.body())
                    } else {
                        BookmarksFragment.log.log(Level.INFO, "load user ERROR")
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    BookmarksFragment.log.log(Level.INFO, "load user ERROR")
                }
            })
    }
}