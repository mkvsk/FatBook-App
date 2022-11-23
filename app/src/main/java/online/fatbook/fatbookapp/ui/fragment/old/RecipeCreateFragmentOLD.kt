package online.fatbook.fatbookapp.ui.fragment.old

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateOldBinding
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import online.fatbook.fatbookapp.ui.adapters.ViewRecipeIngredientAdapter
import online.fatbook.fatbookapp.ui.listeners.OnRecipeViewDeleteIngredient
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.FileUtils
import online.fatbook.fatbookapp.util.FormatUtils
import online.fatbook.fatbookapp.util.KeyboardActionUtil
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class RecipeCreateFragmentOLD : Fragment(), OnRecipeViewDeleteIngredient {
    private var binding: FragmentRecipeCreateOldBinding? = null
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
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        recipeViewModel = ViewModelProvider(requireActivity())[RecipeViewModel::class.java]
        recipeViewModel!!.selectedRecipeIngredients.value = ArrayList()
        try {
            choosePhotoFromGallery = registerForActivityResult(GetContent()) { uri: Uri? ->
                verifyStoragePermissions(requireActivity())
                if (uri != null) {
                    selectedImageUri = uri
                    val path = FileUtils.getPath(requireContext(), selectedImageUri!!)
                    recipePhoto = File(path)
                    binding!!.imageViewRecipeCreateImageV1.setImageURI(selectedImageUri)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding!!.buttonRecipeCreateImageV1.setOnClickListener { _view: View? ->
            verifyStoragePermissions(requireActivity())
            choosePhotoFromGallery!!.launch("image/*")
        }
        binding!!.buttonRecipeAddSaveV1.setOnClickListener { _view: View? -> saveRecipe() }
        binding!!.buttonRecipeAddIngredientAddV1.setOnClickListener { _view: View? ->
            NavHostFragment.findNavController(
                this
            ).navigate(R.id.navigation_add_ingredient_old)
        }
        setupAdapter()
        binding!!.editTextRecipeAddTitleV1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                niceCheck()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding!!.editTextRecipeAddDescriptionV1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                niceCheck()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun saveRecipe() {
        recipe!!.title = binding!!.editTextRecipeAddTitleV1.text.toString()
        recipe!!.image = StringUtils.EMPTY
        recipe!!.author = userViewModel!!.user.value!!.username
        recipe!!.createDate = FormatUtils.dateFormat.format(Date())
        save()
        NavHostFragment.findNavController(this).popBackStack()
    }

    private fun save() {
//        RetrofitFactory.apiService().recipeCreate(recipe).enqueue(object : Callback<Recipe?> {
//            override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
//                if (response.code() == 200) {
////                    RecipeCreateFragment.log.log(Level.INFO, "recipe create SUCCESS")
//                    recipe = response.body()
//                    uploadImage()
//                } else {
////                    RecipeCreateFragment.log.log(
////                        Level.INFO,
////                        "recipe create FAILED " + response.code()
////                    )
//                }
//            }
//
//            override fun onFailure(call: Call<Recipe?>, t: Throwable) {
////                RecipeCreateFragment.log.log(Level.INFO, "recipe create FAILED")
//            }
//        })
    }

    private fun uploadImage() {
        if (recipePhoto != null) {
            try {
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), recipePhoto!!)
                val fileName = "image" + recipePhoto!!.name.substring(
                    recipePhoto!!.name.indexOf('.')
                )
                val file = MultipartBody.Part.createFormData("file", fileName, requestFile)
                RetrofitFactory.apiService()
                    .uploadImage(file, FileUtils.TYPE_RECIPE, recipe!!.identifier)
                    .enqueue(object : Callback<Recipe?> {
                        override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
                            if (response.code() == 200) {
//                                RecipeCreateFragment.log.log(Level.INFO, "image add SUCCESS")
                            } else {
//                                RecipeCreateFragment.log.log(
//                                    Level.INFO,
//                                    "image add FAILED " + response.code()
//                                )
                            }
                        }

                        override fun onFailure(call: Call<Recipe?>, t: Throwable) {
//                            RecipeCreateFragment.log.log(Level.INFO, "image add FAILED")
                        }
                    })
            } catch (e: Exception) {
//                RecipeCreateFragment.log.log(Level.INFO, e.toString())
                e.printStackTrace()
            }
        }
    }

    private fun initRecipe() {
        recipe = Recipe()
        recipe!!.ingredients = ArrayList()
        recipe!!.forks = 0
    }

    private fun niceCheck() {
        if (StringUtils.isNotEmpty(binding!!.editTextRecipeAddTitleV1.text.toString())
            && StringUtils.isNotEmpty(binding!!.editTextRecipeAddDescriptionV1.text.toString())
            && recipe!!.ingredients!!.isNotEmpty()
        ) {
            binding!!.buttonRecipeAddSaveV1.isEnabled = true
            binding!!.buttonRecipeAddSaveV1.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.color_pink_a200)
        } else {
            binding!!.buttonRecipeAddSaveV1.isEnabled = false
            binding!!.buttonRecipeAddSaveV1.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.color_blue_grey_200)
        }
    }

    override fun onResume() {
        super.onResume()
        binding!!.root.viewTreeObserver.addOnGlobalLayoutListener(
            KeyboardActionUtil(
                binding!!.root, requireActivity(), binding!!.viewRecipeCreateV1
            ).listenerForAdjustPan
        )
        if (recipeViewModel!!.selectedRecipeIngredient.value != null) {
            recipe!!.ingredients!!.add(recipeViewModel!!.selectedRecipeIngredient.value!!)
            adapter!!.setData(recipe!!.ingredients)
            adapter!!.notifyDataSetChanged()
            recipeViewModel!!.selectedRecipeIngredient.value = null
        }
        if (selectedImageUri != null) {
            binding!!.imageViewRecipeCreateImageV1.setImageURI(selectedImageUri)
        }
        niceCheck()
    }

    private fun setupAdapter() {
        if (recipe == null) {
            initRecipe()
        }
        val recyclerView = binding!!.rvRecipeAddIngredientsV1
        adapter!!.setData(recipe!!.ingredients)
        adapter!!.setClickListener(this)
        adapter!!.setEditMode(true)
        recyclerView.adapter = adapter
    }

    override fun onDeleteIngredientClick(recipeIngredient: RecipeIngredient?, position: Int) {
        recipe!!.ingredients!!.remove(recipeIngredient)
        adapter!!.setData(recipe!!.ingredients)
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
    ): View {
        binding = FragmentRecipeCreateOldBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onPause() {
        super.onPause()
        binding!!.root.viewTreeObserver.removeOnGlobalLayoutListener(
            KeyboardActionUtil(
                binding!!.root, requireActivity(), binding!!.viewRecipeCreateV1
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