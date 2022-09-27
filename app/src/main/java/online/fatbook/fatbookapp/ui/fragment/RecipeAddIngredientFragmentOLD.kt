package online.fatbook.fatbookapp.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import lombok.extern.java.Log
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.core.recipe.ingredient.IngredientUnit
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.databinding.FragmentAddIngredientOldBinding
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import online.fatbook.fatbookapp.ui.adapters.AddIngredientToRecipeAdapter
import online.fatbook.fatbookapp.ui.listeners.OnAddIngredientItemClickListener
import online.fatbook.fatbookapp.ui.viewmodel.IngredientViewModel
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.util.KeyboardActionUtil
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Log
class RecipeAddIngredientFragmentOLD : Fragment(), OnAddIngredientItemClickListener {
    private var binding: FragmentAddIngredientOldBinding? = null
    private var adapter: AddIngredientToRecipeAdapter? = null
    private var recipeViewModel: RecipeViewModel? = null
    private var ingredientViewModel: IngredientViewModel? = null
    private var selectedIngredient: Ingredient? = null
    private var ingredientList: List<Ingredient?>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        recipeViewModel = ViewModelProvider(requireActivity())[RecipeViewModel::class.java]
        ingredientViewModel = ViewModelProvider(requireActivity())[IngredientViewModel::class.java]
        binding!!.btnAddIngredientToRecipe.isEnabled = false
        binding!!.textViewSelectedIngredient.setTextColor(resources.getColor(R.color.color_blue_grey_200))
        binding!!.toolbarAddIngredientToRecipe.setNavigationOnClickListener {
            NavHostFragment.findNavController(
                this
            ).popBackStack()
        }
        binding!!.btnAddIngredientToRecipe.setOnClickListener {
            val recipeIngredient = RecipeIngredient()
            recipeIngredient.ingredient = selectedIngredient
            recipeIngredient.quantity =
                binding!!.editTextIngredientQuantity.text.toString().toDouble()
            recipeIngredient.unit = IngredientUnit.values()[binding!!.pickerIngredientUnit.value]
            recipeViewModel!!.selectedRecipeIngredient.value = recipeIngredient
            NavHostFragment.findNavController(this).popBackStack()
        }
        binding!!.editTextIngredientQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                activateButtonSave()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        ingredientViewModel!!.ingredientList.observe(viewLifecycleOwner) {
            ingredientList = it
            adapter!!.setData(it)
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
            val temp: ArrayList<Ingredient> = ArrayList()
            for (i in ingredientList!!) {
                if (StringUtils.containsIgnoreCase(i!!.title, text)) {
                    temp.add(i)
                }
            }
            adapter!!.setData(temp)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadIngredients() {
        RetrofitFactory.apiServiceClient().allIngredients().enqueue(object :
            Callback<List<Ingredient>?> {
            override fun onResponse(
                call: Call<List<Ingredient>?>,
                response: Response<List<Ingredient>?>
            ) {
                ingredientViewModel!!.ingredientList.value = response.body()
//                RecipeAddIngredientFragment.log.log(Level.INFO, "ingredient list load: SUCCESS")
            }

            override fun onFailure(call: Call<List<Ingredient>?>, t: Throwable) {
//                RecipeAddIngredientFragment.log.log(Level.INFO, "ingredient list load: FAILED")
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
        adapter = AddIngredientToRecipeAdapter()
        adapter!!.setData(ArrayList())
        adapter!!.setClickListener(this)
        rv.adapter = adapter
    }

    override fun onIngredientClick(previousItem: Int, selectedItem: Int, ingredient: Ingredient?) {
        selectedIngredient = ingredient
        binding!!.textViewSelectedIngredient.setTextColor(resources.getColor(R.color.color_pink_a200))
        binding!!.textViewSelectedIngredient.text = ingredient!!.title
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
    ): View {
        binding = FragmentAddIngredientOldBinding.inflate(inflater, container, false)
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