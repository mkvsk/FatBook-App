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
import android.graphics.Rect
import android.provider.OpenableColumns
import android.view.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import online.fatbook.fatbookapp.core.User
import online.fatbook.fatbookapp.databinding.ActivityPasswordBinding
import online.fatbook.fatbookapp.retrofit.NetworkInfoService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Multipart

class PasswordActivity : AppCompatActivity() {
    private var binding: ActivityPasswordBinding? = null
    private var user: User? = null
    var isKeyboardVisible = false
    val listener = OnGlobalLayoutListener {
        val rectangle = Rect()
        val contentView: View = binding!!.root
        contentView.getWindowVisibleDisplayFrame(rectangle)
        val screenHeight = contentView.rootView.height
        val keypadHeight = screenHeight - rectangle.bottom
        val isKeyboardNowVisible = keypadHeight > screenHeight * 0.15
        if (isKeyboardVisible != isKeyboardNowVisible) {
            if (isKeyboardNowVisible) {
                binding!!.textViewPasswordVersion.visibility = View.GONE
                binding!!.textViewPasswordCopyright.visibility = View.GONE
                binding!!.textViewPasswordTagline.visibility = View.GONE
            } else {
                binding!!.textViewPasswordVersion.visibility = View.VISIBLE
                binding!!.textViewPasswordCopyright.visibility = View.VISIBLE
                binding!!.textViewPasswordTagline.visibility = View.VISIBLE
            }
        }
        isKeyboardVisible = isKeyboardNowVisible
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        user = intent.getSerializableExtra(UserUtils.TAG_USER) as User?
        binding!!.buttonPasswordNext.isEnabled = false
        binding!!.buttonPasswordNext.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.color_blue_grey_200)
        binding!!.editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                toggleViews(validateFat(charSequence.toString()))
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding!!.buttonPasswordNext.setOnClickListener { view: View? ->
            if (validateFat(
                    binding!!.editTextPassword.text.toString()
                )
            ) {
                val intent = Intent(this, SkipAdditionalInfoActivity::class.java)
                intent.putExtra(UserUtils.TAG_USER, user)
                intent.putExtra(UserUtils.TAG_FAT, binding!!.editTextPassword.text.toString())
                startActivity(intent)
            } else {
                toggleViews(false)
            }
        }
    }

    private fun toggleViews(enable: Boolean) {
        if (enable) {
            binding!!.imageViewPasswordIconAccepted.visibility = View.VISIBLE
            binding!!.editTextPassword.background =
                AppCompatResources.getDrawable(baseContext, R.drawable.round_corner_edittext_login)
            binding!!.buttonPasswordNext.isEnabled = true
            binding!!.buttonPasswordNext.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.color_pink_a200)
        } else {
            binding!!.imageViewPasswordIconAccepted.visibility = View.INVISIBLE
            binding!!.editTextPassword.background =
                AppCompatResources.getDrawable(
                    baseContext,
                    R.drawable.round_corner_edittext_login_error
                )
            binding!!.buttonPasswordNext.isEnabled = false
            binding!!.buttonPasswordNext.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.color_blue_grey_200)
        }
    }

    override fun onResume() {
        super.onResume()
        binding!!.root.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    override fun onPause() {
        super.onPause()
        binding!!.root.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }

    /**
     * https://mkyong.com/regular-expressions/how-to-validate-password-with-regular-expression/
     *
     *
     * Password must contain at least one digit [0-9].
     * Password must contain at least one lowercase Latin character [a-z].
     * Password must contain at least one uppercase Latin character [A-Z].
     * Password must contain at least one special character like ! @ # & ( ).
     * Password must contain a length of at least 8 characters and a maximum of 20 characters.
     */
    private fun validateFat(fat: String): Boolean {
        return fat.length >= 6 && fat.length <= 16
        //        return fat.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$")
    }
}