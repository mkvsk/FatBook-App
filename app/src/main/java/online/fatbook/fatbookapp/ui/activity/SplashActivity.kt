package online.fatbook.fatbookapp.ui.activity

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
import online.fatbook.fatbookapp.core.Ingredient
import online.fatbook.fatbookapp.core.RecipeIngredient
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
import online.fatbook.fatbookapp.core.IngredientUnit
import androidx.lifecycle.ViewModel
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.os.Environment
import android.text.TextUtils
import android.content.ContentUris
import android.os.Handler
import android.provider.OpenableColumns
import android.view.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import lombok.extern.java.Log
import online.fatbook.fatbookapp.core.User
import online.fatbook.fatbookapp.databinding.ActivitySplashBinding
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
import java.util.logging.Level

@Log
class SplashActivity : AppCompatActivity() {
    private var binding: ActivitySplashBinding? = null
    private var userViewModel: UserViewModel? = null
    private var userLogin: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        val sharedPreferences = getSharedPreferences(UserUtils.APP_PREFS, MODE_PRIVATE)
        userLogin = sharedPreferences.getString(UserUtils.USER_LOGIN, StringUtils.EMPTY)
        if (StringUtils.isEmpty(userLogin)) {
            userViewModel!!.setUser(User())
        } else {
            loadUserData(userLogin)
        }
        userViewModel!!.user.observe(this) { user: User? ->
            val handler = Handler()
            handler.postDelayed({
                val intent: Intent
                intent = if (StringUtils.isEmpty(userLogin)) {
                    Intent(this, WelcomeActivity::class.java)
                } else {
                    Intent(this, MainActivity::class.java)
                }
                intent.putExtra(UserUtils.TAG_USER, user)
                startActivity(intent)
                finish()
            }, 1)
        }
        binding!!.buttonSplashRetry.setOnClickListener { view: View? ->
            binding!!.textViewSplashError.visibility = View.GONE
            binding!!.buttonSplashRetry.visibility = View.GONE
            loadUserData(userLogin)
        }
    }

    private fun loadUserData(login: String?) {
        RetrofitFactory.apiServiceClient().getUser(login).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                SplashActivity.log.log(
                    Level.INFO,
                    "" + response.code() + " found user: " + response.body()
                )
                userViewModel!!.setUser(response.body())
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                SplashActivity.log.log(Level.INFO, "load user ERROR")
                if (t.toString().contains("ConnectException")) {
                    binding!!.textViewSplashError.text =
                        getString(R.string.splash_no_connection_error_client)
                } else {
                    binding!!.textViewSplashError.text =
                        getString(R.string.splash_no_connection_error_api)
                }
                binding!!.textViewSplashError.visibility = View.VISIBLE
                binding!!.buttonSplashRetry.visibility = View.VISIBLE
            }
        })
    }
}