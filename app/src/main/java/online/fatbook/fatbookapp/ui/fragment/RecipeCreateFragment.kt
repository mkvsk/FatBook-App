package online.fatbook.fatbookapp.ui.fragment

import android.Manifest
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
import android.net.Uri
import android.provider.OpenableColumns
import android.view.*
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import lombok.extern.java.Log
import okhttp3.MediaType
import online.fatbook.fatbookapp.core.Recipe
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateBinding
import online.fatbook.fatbookapp.retrofit.NetworkInfoService
import online.fatbook.fatbookapp.util.FileUtils
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Multipart
import java.io.File
import java.lang.Exception
import java.util.*
import java.util.logging.Level

@Log
class RecipeCreateFragment : Fragment(), OnRecipeViewDeleteIngredient {
    private var binding: FragmentRecipeCreateBinding? = null
    private var userViewModel: UserViewModel? = null
    private var recipeViewModel: RecipeViewModel? = null
    private var recipe: Recipe? = null
    private var adapter: ViewRecipeIngredientAdapter? = null
    private var choosePhotoFromGallery: ActivityResultLauncher<String>? = null
    private var recipePhoto: File? = null
    private var selectedImageUri: Uri? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        recipeViewModel = ViewModelProvider(requireActivity()).get(RecipeViewModel::class.java)
        recipeViewModel!!.setSelectedRecipeIngredients(ArrayList())
        try {
            choosePhotoFromGallery = registerForActivityResult(GetContent()) { uri: Uri? ->
                verifyStoragePermissions(requireActivity())
                if (uri != null) {
                    selectedImageUri = uri
                    val path = FileUtils.getPath(requireContext(), selectedImageUri!!)
                    recipePhoto = File(path)
                    binding!!.imageViewRecipeCreateImage.setImageURI(selectedImageUri)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding!!.buttonRecipeCreateImage.setOnClickListener { _view: View? ->
            verifyStoragePermissions(requireActivity())
            choosePhotoFromGallery!!.launch("image/*")
        }
        binding!!.buttonRecipeAddSave.setOnClickListener { _view: View? -> saveRecipe() }
        binding!!.buttonRecipeAddIngredientAdd.setOnClickListener { _view: View? ->
            NavHostFragment.findNavController(
                this
            ).navigate(R.id.navigation_add_ingredient)
        }
        setupAdapter()
        binding!!.editTextRecipeAddTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                niceCheck()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding!!.editTextRecipeAddDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                niceCheck()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun saveRecipe() {
        recipe.setName(binding!!.editTextRecipeAddTitle.text.toString())
        recipe.setDescription(binding!!.editTextRecipeAddDescription.text.toString())
        recipe.setImage(StringUtils.EMPTY)
        recipe.setAuthor(userViewModel!!.user.value.getLogin())
        recipe.setCreateDate(RecipeUtils.regDateFormat.format(Date()))
        save()
        NavHostFragment.findNavController(this).popBackStack()
    }

    private fun save() {
        RetrofitFactory.apiServiceClient().recipeCreate(recipe).enqueue(object : Callback<Recipe?> {
            override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
                if (response.code() == 200) {
                    RecipeCreateFragment.log.log(Level.INFO, "recipe create SUCCESS")
                    recipe = response.body()
                    uploadImage()
                } else {
                    RecipeCreateFragment.log.log(
                        Level.INFO,
                        "recipe create FAILED " + response.code()
                    )
                }
            }

            override fun onFailure(call: Call<Recipe?>, t: Throwable) {
                RecipeCreateFragment.log.log(Level.INFO, "recipe create FAILED")
            }
        })
    }

    private fun uploadImage() {
        if (recipePhoto != null) {
            try {
                val requestFile = RequestBody.create(MediaType.parse("image/*"), recipePhoto)
                val fileName = "image" + recipePhoto!!.name.substring(
                    recipePhoto!!.name.indexOf('.')
                )
                val file = MultipartBody.Part.createFormData("file", fileName, requestFile)
                RetrofitFactory.apiServiceClient()
                    .uploadImage(file, FileUtils.TAG_RECIPE, recipe.getIdentifier())
                    .enqueue(object : Callback<Recipe?> {
                        override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
                            if (response.code() == 200) {
                                RecipeCreateFragment.log.log(Level.INFO, "image add SUCCESS")
                            } else {
                                RecipeCreateFragment.log.log(
                                    Level.INFO,
                                    "image add FAILED " + response.code()
                                )
                            }
                        }

                        override fun onFailure(call: Call<Recipe?>, t: Throwable) {
                            RecipeCreateFragment.log.log(Level.INFO, "image add FAILED")
                        }
                    })
            } catch (e: Exception) {
                RecipeCreateFragment.log.log(Level.INFO, e.toString())
                e.printStackTrace()
            }
        }
    }

    private fun initRecipe() {
        recipe = Recipe()
        recipe.setIngredients(ArrayList())
        recipe.setForks(0)
        recipe.setIdentifier(0L)
    }

    private fun niceCheck() {
        if (StringUtils.isNotEmpty(binding!!.editTextRecipeAddTitle.text.toString())
            && StringUtils.isNotEmpty(binding!!.editTextRecipeAddDescription.text.toString())
            && !recipe.getIngredients().isEmpty()
        ) {
            binding!!.buttonRecipeAddSave.isEnabled = true
            binding!!.buttonRecipeAddSave.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.color_pink_a200)
        } else {
            binding!!.buttonRecipeAddSave.isEnabled = false
            binding!!.buttonRecipeAddSave.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.color_blue_grey_200)
        }
    }

    override fun onResume() {
        super.onResume()
        binding!!.root.viewTreeObserver.addOnGlobalLayoutListener(
            KeyboardActionUtil(
                binding!!.root, requireActivity(), binding!!.viewRecipeCreate
            ).listenerForAdjustPan
        )
        if (recipeViewModel!!.selectedRecipeIngredient.value != null) {
            recipe.getIngredients().add(recipeViewModel!!.selectedRecipeIngredient.value)
            adapter!!.setData(recipe.getIngredients())
            adapter!!.notifyDataSetChanged()
            recipeViewModel!!.setSelectedRecipeIngredient(null)
        }
        if (selectedImageUri != null) {
            binding!!.imageViewRecipeCreateImage.setImageURI(selectedImageUri)
        }
        niceCheck()
    }

    private fun setupAdapter() {
        if (recipe == null) {
            initRecipe()
        }
        val recyclerView = binding!!.rvRecipeAddIngredients
        adapter = ViewRecipeIngredientAdapter(binding!!.root.context, recipe.getIngredients(), this)
        adapter!!.setEditMode(true)
        recyclerView.adapter = adapter
    }

    override fun onDeleteIngredientClick(recipeIngredient: RecipeIngredient?, position: Int) {
        recipe.getIngredients().remove(recipeIngredient)
        adapter!!.setData(recipe.getIngredients())
        adapter!!.notifyItemRemoved(position)
        niceCheck()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeCreateBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onPause() {
        super.onPause()
        binding!!.root.viewTreeObserver.removeOnGlobalLayoutListener(
            KeyboardActionUtil(
                binding!!.root, requireActivity(), binding!!.viewRecipeCreate
            ).listenerForAdjustPan
        )
    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        private fun verifyStoragePermissions(activity: Activity) {
            val permission = ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
            }
        }
    }
}