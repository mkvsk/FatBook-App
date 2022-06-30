package online.fatbook.fatbookapp.ui.viewmodel

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
import android.view.LayoutInflater
import android.view.ViewGroup
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
import android.view.WindowManager
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
import android.view.MenuInflater
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
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import online.fatbook.fatbookapp.core.*
import online.fatbook.fatbookapp.retrofit.NetworkInfoService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Multipart

class UserViewModel : ViewModel() {
    val user = MutableLiveData<User?>()
    val recipeList = MutableLiveData<List<Recipe>>()
    val bookmarkedRecipeList = MutableLiveData<List<Recipe>>()
    val forkedRecipeList = MutableLiveData<List<Recipe>>()
    val feedRecipeList = MutableLiveData<List<Recipe?>?>()
    fun setUser(user: User?) {
        this.user.value = user
    }

    fun getUser(): LiveData<User?> {
        return user
    }

    fun setRecipeList(recipeList: List<Recipe>) {
        this.recipeList.value = recipeList
    }

    fun getRecipeList(): LiveData<List<Recipe>> {
        return recipeList
    }

    fun setBookmarkedRecipeList(bookmarkedRecipeList: List<Recipe>) {
        this.bookmarkedRecipeList.value = bookmarkedRecipeList
    }

    fun getBookmarkedRecipeList(): LiveData<List<Recipe>> {
        return bookmarkedRecipeList
    }

    fun setForkedRecipeList(forkedRecipeList: List<Recipe>) {
        this.forkedRecipeList.value = forkedRecipeList
    }

    fun getForkedRecipeList(): LiveData<List<Recipe>> {
        return forkedRecipeList
    }

    fun setFeedRecipeList(feedRecipeList: List<Recipe?>?) {
        this.feedRecipeList.value = feedRecipeList
    }

    fun getFeedRecipeList(): LiveData<List<Recipe?>?> {
        return feedRecipeList
    }
}