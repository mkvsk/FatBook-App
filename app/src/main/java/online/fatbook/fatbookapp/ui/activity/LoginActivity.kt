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
import android.graphics.Rect
import android.provider.OpenableColumns
import android.view.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import lombok.extern.java.Log
import online.fatbook.fatbookapp.core.*
import online.fatbook.fatbookapp.databinding.ActivityLoginBinding
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
import java.util.*
import java.util.logging.Level

@Log
class LoginActivity : AppCompatActivity() {
    private var binding: ActivityLoginBinding? = null
    private var signInViewModel: SignInViewModel? = null
    private var btnNextClicked = false
    private var isKeyboardVisible = false
    val listener = OnGlobalLayoutListener {
        val rectangle = Rect()
        val contentView: View = binding!!.root
        contentView.getWindowVisibleDisplayFrame(rectangle)
        val screenHeight = contentView.rootView.height
        val keypadHeight = screenHeight - rectangle.bottom
        val isKeyboardNowVisible = keypadHeight > screenHeight * 0.15
        if (isKeyboardVisible != isKeyboardNowVisible) {
            if (isKeyboardNowVisible) {
                binding!!.textViewLoginVersion.visibility = View.GONE
                binding!!.textViewLoginCopyright.visibility = View.GONE
                binding!!.textViewLoginTagline.visibility = View.GONE
            } else {
                binding!!.textViewLoginVersion.visibility = View.VISIBLE
                binding!!.textViewLoginCopyright.visibility = View.VISIBLE
                binding!!.textViewLoginTagline.visibility = View.VISIBLE
            }
        }
        isKeyboardVisible = isKeyboardNowVisible
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.buttonLoginNext.isEnabled = false
        binding!!.buttonLoginNext.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.color_blue_grey_200)
        signInViewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        signInViewModel!!.loginAvailable.observe(this) { available: Boolean? ->
            if (available!!) {
                toggleViews(true)
                if (btnNextClicked) {
                    val intent = Intent(application, PasswordActivity::class.java)
                    val user = User()
                    user.login = binding!!.editTextLogin.text.toString()
                    user.role = Role.USER
                    user.birthday = StringUtils.EMPTY
                    user.recipes = ArrayList()
                    user.recipesForked = ArrayList()
                    user.recipesBookmarked = ArrayList()
                    user.regDate = UserUtils.regDateFormat.format(Date())
                    intent.putExtra(UserUtils.TAG_USER, user)
                    startActivity(intent)
                    btnNextClicked = false
                }
            } else {
                toggleViews(false)
                btnNextClicked = false
            }
        }
        binding!!.editTextLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                validateLogin(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding!!.buttonLoginNext.setOnClickListener { view: View? ->
            btnNextClicked = true
            validateLogin(binding!!.editTextLogin.text.toString())
        }
    }

    private fun toggleViews(enable: Boolean) {
        if (enable) {
            binding!!.imageViewLoginIconAccepted.visibility = View.VISIBLE
            binding!!.editTextLogin.background =
                AppCompatResources.getDrawable(baseContext, R.drawable.round_corner_edittext_login)
            binding!!.buttonLoginNext.isEnabled = true
            binding!!.buttonLoginNext.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.color_pink_a200)
        } else {
            binding!!.imageViewLoginIconAccepted.visibility = View.INVISIBLE
            binding!!.editTextLogin.background =
                AppCompatResources.getDrawable(
                    baseContext,
                    R.drawable.round_corner_edittext_login_error
                )
            binding!!.buttonLoginNext.isEnabled = false
            binding!!.buttonLoginNext.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.color_blue_grey_200)
        }
    }

    private fun validateLogin(login: String) {
        if (login.matches("[a-zA-Z0-9]+") && login.length >= 4) {
            LoginActivity.log.log(Level.INFO, "login valid")
            loginCheckForCreation(login)
        } else {
            LoginActivity.log.log(Level.INFO, "login invalid")
            toggleViews(false)
        }
    }

    private fun loginCheckForCreation(login: String) {
        RetrofitFactory.apiServiceClient().loginCheck(login).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                LoginActivity.log.log(
                    Level.INFO,
                    "login check SUCCESS: " + response.code() + " = " + response.body()
                )
                if (response.code() == 200) {
                    signInViewModel!!.setLoginAvailable(response.body())
                } else {
                    signInViewModel!!.setLoginAvailable(false)
                }
                if (response.body()!!) {
                    LoginActivity.log.log(Level.INFO, "login available")
                } else {
                    LoginActivity.log.log(Level.INFO, "login unavailable")
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                LoginActivity.log.log(Level.INFO, "login check FAILED")
                t.printStackTrace()
                signInViewModel!!.setLoginAvailable(false)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        binding!!.root.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    override fun onPause() {
        super.onPause()
        binding!!.root.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }
}