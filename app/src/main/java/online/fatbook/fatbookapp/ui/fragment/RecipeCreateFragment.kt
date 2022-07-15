package online.fatbook.fatbookapp.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import lombok.extern.java.Log
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.Recipe
import online.fatbook.fatbookapp.core.RecipeIngredient
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateBinding
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import online.fatbook.fatbookapp.ui.adapters.ViewRecipeIngredientAdapter
import online.fatbook.fatbookapp.ui.listeners.OnRecipeViewDeleteIngredient
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.FileUtils
import online.fatbook.fatbookapp.util.KeyboardActionUtil
import online.fatbook.fatbookapp.util.RecipeUtils
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*
import java.util.logging.Level

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
        recipe!!.name = binding!!.editTextRecipeAddTitle.text.toString()
        recipe!!.description = binding!!.editTextRecipeAddDescription.text.toString()
        recipe!!.image = StringUtils.EMPTY
        recipe!!.author = userViewModel!!.user.value!!.login
        recipe!!.createDate = RecipeUtils.regDateFormat.format(Date())
        save()
        NavHostFragment.findNavController(this).popBackStack()
    }

    private fun save() {
        RetrofitFactory.apiServiceClient().recipeCreate(recipe).enqueue(object : Callback<Recipe?> {
            override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
                if (response.code() == 200) {
//                    RecipeCreateFragment.log.log(Level.INFO, "recipe create SUCCESS")
                    recipe = response.body()
                    uploadImage()
                } else {
//                    RecipeCreateFragment.log.log(
//                        Level.INFO,
//                        "recipe create FAILED " + response.code()
//                    )
                }
            }

            override fun onFailure(call: Call<Recipe?>, t: Throwable) {
//                RecipeCreateFragment.log.log(Level.INFO, "recipe create FAILED")
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
                    .uploadImage(file, FileUtils.TAG_RECIPE, recipe!!.identifier)
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
        recipe!!.identifier = 0L
    }

    private fun niceCheck() {
        if (StringUtils.isNotEmpty(binding!!.editTextRecipeAddTitle.text.toString())
            && StringUtils.isNotEmpty(binding!!.editTextRecipeAddDescription.text.toString())
            && recipe!!.ingredients!!.isNotEmpty()
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
            recipe!!.ingredients!!.add(recipeViewModel!!.selectedRecipeIngredient.value!!)
            adapter!!.setData(recipe!!.ingredients)
            adapter!!.notifyDataSetChanged()
            recipeViewModel!!.selectedRecipeIngredient.value = null
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