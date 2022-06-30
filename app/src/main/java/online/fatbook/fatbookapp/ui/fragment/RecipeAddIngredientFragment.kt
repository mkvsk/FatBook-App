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
import android.provider.OpenableColumns
import android.view.*
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import lombok.extern.java.Log
import online.fatbook.fatbookapp.databinding.FragmentAddIngredientBinding
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
class RecipeAddIngredientFragment : Fragment(), OnAddIngredientItemClickListener {
    private var binding: FragmentAddIngredientBinding? = null
    private var adapter: AddIngredientToRecipeAdapter? = null
    private var recipeViewModel: RecipeViewModel? = null
    private var ingredientViewModel: IngredientViewModel? = null
    private var selectedIngredient: Ingredient? = null
    private var ingredientList: List<Ingredient?>? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        recipeViewModel = ViewModelProvider(requireActivity()).get(RecipeViewModel::class.java)
        ingredientViewModel = ViewModelProvider(requireActivity()).get(
            IngredientViewModel::class.java
        )
        binding!!.btnAddIngredientToRecipe.isEnabled = false
        binding!!.textViewSelectedIngredient.setTextColor(resources.getColor(R.color.color_blue_grey_200))
        binding!!.toolbarAddIngredientToRecipe.setNavigationOnClickListener { view1: View? ->
            NavHostFragment.findNavController(
                this
            ).popBackStack()
        }
        binding!!.btnAddIngredientToRecipe.setOnClickListener { view1: View? ->
            val recipeIngredient = RecipeIngredient()
            recipeIngredient.ingredient = selectedIngredient
            recipeIngredient.quantity =
                binding!!.editTextIngredientQuantity.text.toString().toDouble()
            recipeIngredient.unit = IngredientUnit.values()[binding!!.pickerIngredientUnit.value]
            recipeViewModel!!.setSelectedRecipeIngredient(recipeIngredient)
            NavHostFragment.findNavController(this).popBackStack()
        }
        binding!!.editTextIngredientQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                activateButtonSave()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        ingredientViewModel!!.ingredientList.observe(viewLifecycleOwner) { ingredients: List<Ingredient?>? ->
            ingredientList = ingredients
            adapter!!.setData(ingredients)
            adapter!!.notifyDataSetChanged()
        }
        setupAdapter()
        //        if (ingredientViewModel.getIngredientList().getValue() == null) {
        loadIngredients()
        //        }
        setupUnitPicker()
        binding!!.editTextAddIngredientSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                filter(editable.toString())
            }
        })
    }

    private fun filter(text: String) {
        try {
            val temp: MutableList<Ingredient?> = ArrayList()
            for (i in ingredientList!!) {
                if (StringUtils.containsIgnoreCase(i.getName(), text)) {
                    temp.add(i)
                }
            }
            adapter!!.updateList(temp)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadIngredients() {
        RetrofitFactory.apiServiceClient().allIngredients.enqueue(object :
            Callback<List<Ingredient?>?> {
            override fun onResponse(
                call: Call<List<Ingredient?>?>,
                response: Response<List<Ingredient?>?>
            ) {
                ingredientViewModel!!.setIngredientList(response.body())
                RecipeAddIngredientFragment.log.log(Level.INFO, "ingredient list load: SUCCESS")
            }

            override fun onFailure(call: Call<List<Ingredient?>?>, t: Throwable) {
                RecipeAddIngredientFragment.log.log(Level.INFO, "ingredient list load: FAILED")
            }
        })
    }

    private fun setupUnitPicker() {
        val unitData = arrayOf(
            IngredientUnit.ML.getMultiplyNaming(requireContext()),
            IngredientUnit.PCS.getMultiplyNaming(requireContext()),
            IngredientUnit.GRAM.getMultiplyNaming(requireContext()),
            IngredientUnit.TEA_SPOON.getMultiplyNaming(requireContext()),
            IngredientUnit.TABLE_SPOON.getMultiplyNaming(requireContext())
        )
        binding!!.pickerIngredientUnit.minValue = 0
        binding!!.pickerIngredientUnit.maxValue = unitData.size - 1
        binding!!.pickerIngredientUnit.displayedValues = unitData
    }

    private fun setupAdapter() {
        val rv = binding!!.rvAddIngredientToRecipe
        adapter = AddIngredientToRecipeAdapter(binding!!.root.context, ArrayList())
        adapter!!.setClickListener(this)
        rv.adapter = adapter
    }

    override fun onIngredientClick(previousItem: Int, selectedItem: Int, ingredient: Ingredient?) {
        selectedIngredient = ingredient
        binding!!.textViewSelectedIngredient.setTextColor(resources.getColor(R.color.color_pink_a200))
        binding!!.textViewSelectedIngredient.text = ingredient.getName()
        adapter!!.notifyItemChanged(previousItem)
        adapter!!.notifyItemChanged(selectedItem)
        activateButtonSave()
    }

    private fun activateButtonSave() {
        if (StringUtils.isEmpty(binding!!.editTextIngredientQuantity.text.toString()) || selectedIngredient == null) {
            binding!!.btnAddIngredientToRecipe.isEnabled = false
            binding!!.btnAddIngredientToRecipe.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.color_blue_grey_200)
        } else {
            binding!!.btnAddIngredientToRecipe.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.color_pink_a200)
            binding!!.btnAddIngredientToRecipe.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddIngredientBinding.inflate(inflater, container, false)
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