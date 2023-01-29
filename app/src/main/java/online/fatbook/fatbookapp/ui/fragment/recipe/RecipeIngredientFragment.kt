package online.fatbook.fatbookapp.ui.fragment.recipe

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.AutoTransition
import androidx.transition.Scene
import androidx.transition.TransitionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.core.recipe.ingredient.unit.IngredientUnit
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.core.recipe.ingredient.unit.IngredientUnitRatio
import online.fatbook.fatbookapp.databinding.FragmentRecipeIngredientBinding
import online.fatbook.fatbookapp.ui.adapters.IngredientAdapter
import online.fatbook.fatbookapp.ui.listeners.OnIngredientItemClickListener
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.util.FormatUtils
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils
import kotlin.collections.ArrayList

class RecipeIngredientFragment : Fragment(), OnIngredientItemClickListener {

    private var _binding: FragmentRecipeIngredientBinding? = null
    private val binding get() = _binding!!

    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }
    private var adapter: IngredientAdapter? = null
    private var units: ArrayList<IngredientUnit> = ArrayList()

    private var selectedUnit: IngredientUnit? = null
    private var selectedQty: Double = 0.0
    private var newQty: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeIngredientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility =
            View.GONE
        binding.loader.progressOverlay.visibility = View.VISIBLE
        loadIngredients()
        loadIngredientUnits()
        setupIngredientsAdapter()
        setupMenu()

        binding.cardviewLeftRecipeAddIngredients.visibility = View.GONE
        binding.textViewSelectedIngredientRecipeAddIngredients.doOnTextChanged { _, _, _, _ ->
            binding.toolbarRecipeAddIngredients.menu.findItem(R.id.menu_add_ingredient_to_recipe).isVisible =
                !binding.editTextIngredientQuantityRecipeAddIngredients.text.isNullOrEmpty()
            TransitionManager.go(Scene(binding.cardviewLeftRecipeAddIngredients), AutoTransition())
            TransitionManager.go(Scene(binding.cardviewRightRecipeAddIngredients), AutoTransition())
            binding.cardviewLeftRecipeAddIngredients.visibility = View.VISIBLE
            binding.editTextIngredientQuantityRecipeAddIngredients.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(s: CharSequence, i1: Int, i2: Int, i3: Int) {
                }

                override fun onTextChanged(s: CharSequence, i1: Int, i2: Int, i3: Int) {
                    checkData()
                    if (s.isEmpty()) {
                        binding.toolbarRecipeAddIngredients.menu.findItem(R.id.menu_add_ingredient_to_recipe).isVisible =
                            false
                        recipeViewModel.newRecipeAddIngredient.value?.let {
                            setDefaultNutritionFacts(
                                it
                            )
                        }
                    } else {
                        binding.toolbarRecipeAddIngredients.menu.findItem(R.id.menu_add_ingredient_to_recipe).isVisible =
                            true
                        recipeViewModel.newRecipeAddIngredient.value?.let { calculateNutrition(it) }
                    }
                }

                override fun afterTextChanged(s: Editable) {
                }
            })
        }

        binding.edittextSearchRecipeAddIngredients.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, i1: Int, i2: Int, i3: Int) {
            }

            override fun onTextChanged(s: CharSequence, i1: Int, i2: Int, i3: Int) {
                filter(binding.edittextSearchRecipeAddIngredients.text.toString())
            }

            override fun afterTextChanged(s: Editable) {
            }

        })
    }

    //TODO unit fix progress bar
    private fun loadIngredientUnits() {
        staticDataViewModel.getAllIngredientUnits(object : ResultCallback<List<IngredientUnit>> {
            override fun onResult(value: List<IngredientUnit>?) {
                value?.let {
                    staticDataViewModel.setIngredientUnits(value)
                    staticDataViewModel.setUnitG(value.find { ingredientUnit -> ingredientUnit.ordinal == 1 })

                    staticDataViewModel.setUnitML(value.find { ingredientUnit -> ingredientUnit.ordinal == 2 })
                }
                setupUnitPicker(null)
            }

            override fun onFailure(value: List<IngredientUnit>?) {
                loadIngredientUnits()
            }
        })
    }

    private fun calculateNutrition(ingredient: Ingredient) {
        val nutritionFacts = ingredient.unitRatio!!
        if (selectedUnit!!.ordinal in 1..4) {
            if ((nutritionFacts.unit!!.ordinal == 1 && selectedUnit!!.ordinal == 1) ||
                (nutritionFacts.unit!!.ordinal == 2 && selectedUnit!!.ordinal == 2)
            ) {
                setNutritionFactsForMlAndG(nutritionFacts)
            } else if ((nutritionFacts.unit!!.ordinal == 1 && selectedUnit!!.ordinal == 3) ||
                (nutritionFacts.unit!!.ordinal == 2 && selectedUnit!!.ordinal == 4)
            ) {
                setNutritionFactsForKgAndL(nutritionFacts)
            } else {
                setDefaultNutritionFacts(ingredient)
            }
        } else {
            setDefaultNutritionFacts(ingredient)
        }
    }

    private fun setNutritionFactsForKgAndL(nutritionFacts: IngredientUnitRatio) {
        newQty =
            binding.editTextIngredientQuantityRecipeAddIngredients.text.toString().toDouble()

        binding.textviewIngredientKcalsQtyRecipeAddIngredients.text =
            String.format(
                getString(R.string.format_kcal),
                FormatUtils.roundOffDecimal(
                    (nutritionFacts.kcal!! / 100 * newQty) * 1000.toString().toDouble()
                )
            )

        binding.tvIngredientProteinsRecipeAddIngredients.text =
            FormatUtils.roundOffDecimal(
                (nutritionFacts.proteins!! / 100 * newQty) * 1000.toString().toDouble()
            ).toString()
        binding.tvIngredientFatsRecipeAddIngredients.text =
            FormatUtils.roundOffDecimal(
                (nutritionFacts.fats!! / 100 * newQty) * 1000.toString().toDouble()
            ).toString()
        binding.tvIngredientCarbsRecipeAddIngredients.text =
            FormatUtils.roundOffDecimal(
                (nutritionFacts.carbs!! / 100 * newQty) * 1000.toString().toDouble()
            ).toString()
    }

    private fun setNutritionFactsForMlAndG(nutritionFacts: IngredientUnitRatio) {
        newQty =
            binding.editTextIngredientQuantityRecipeAddIngredients.text.toString().toDouble()

        binding.textviewIngredientKcalsQtyRecipeAddIngredients.text =
            String.format(
                getString(R.string.format_kcal),
                FormatUtils.roundOffDecimal(
                    (nutritionFacts.kcal!! / 100 * newQty).toString().toDouble()
                )
            )

        binding.tvIngredientProteinsRecipeAddIngredients.text =
            FormatUtils.roundOffDecimal(
                (nutritionFacts.proteins!! / 100 * newQty).toString().toDouble()
            ).toString()
        binding.tvIngredientFatsRecipeAddIngredients.text =
            FormatUtils.roundOffDecimal(
                (nutritionFacts.fats!! / 100 * newQty).toString().toDouble()
            ).toString()
        binding.tvIngredientCarbsRecipeAddIngredients.text =
            FormatUtils.roundOffDecimal(
                (nutritionFacts.carbs!! / 100 * newQty).toString().toDouble()
            ).toString()
    }

    private fun popBackStack() {
        findNavController().popBackStack()
    }

    private fun setupMenu() {
        binding.toolbarRecipeAddIngredients.inflateMenu(R.menu.recipe_create_ingredient_menu)
        binding.toolbarRecipeAddIngredients.setOnMenuItemClickListener(this::onOptionsItemSelected)
        binding.toolbarRecipeAddIngredients.setNavigationOnClickListener {
            popBackStack()
        }
        binding.toolbarRecipeAddIngredients.menu.findItem(R.id.menu_add_ingredient_to_recipe).isVisible =
            false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add_ingredient_to_recipe -> {
                addIngredient()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addIngredient() {
        selectedUnit = units[binding.pickerIngredientUnit.value]
        selectedQty =
            if (binding.editTextIngredientQuantityRecipeAddIngredients.text.isNullOrEmpty()) {
                1.0
            } else {
                binding.editTextIngredientQuantityRecipeAddIngredients.text.toString().toDouble()
            }

        val recipeIngredient = RecipeIngredient(
            pid = null,
            ingredient = recipeViewModel.newRecipeAddIngredient.value,
            unit = selectedUnit,
            quantity = selectedQty
        )
        recipeViewModel.newRecipe.value!!.ingredients!!.add(recipeIngredient)
        findNavController().popBackStack()
    }

    private fun checkData() {
        if (binding.editTextIngredientQuantityRecipeAddIngredients.text.toString().isNotEmpty()
            && binding.editTextIngredientQuantityRecipeAddIngredients.text.toString()
                .toDouble() != 0.0
        ) {
            binding.editTextIngredientQuantityRecipeAddIngredients.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_corner_ingredient_qty_units
                )
        } else {
            binding.editTextIngredientQuantityRecipeAddIngredients.isFocusable
            binding.editTextIngredientQuantityRecipeAddIngredients.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_corner_ingredient_qty_units_stroke
                )
        }
    }

    private fun setupIngredientsAdapter() {
        val rv = binding.rvIngredientsRecipeAddIngredients
        adapter = IngredientAdapter()
        adapter!!.setContext(requireContext())
        adapter!!.setClickListener(this)
        rv.adapter = adapter
    }

    private fun loadIngredients() {
        staticDataViewModel.getAllIngredients(object : ResultCallback<List<Ingredient>> {
            override fun onResult(value: List<Ingredient>?) {
                staticDataViewModel.setIngredients(value!!)
                adapter?.setData(value)
                binding.loader.progressOverlay.visibility = View.GONE
            }

            override fun onFailure(value: List<Ingredient>?) {
                loadIngredients()
            }
        })
    }

    override fun onIngredientClick(previousItem: Int, selectedItem: Int, ingredient: Ingredient?) {
        adapter!!.selectedIngredient =
            staticDataViewModel.ingredients.value!!.find { it.title == ingredient!!.title }
        adapter!!.notifyItemChanged(previousItem)
        adapter!!.notifyItemChanged(selectedItem)
        recipeViewModel.setNewRecipeAddIngredient(ingredient)

        binding.textViewSelectedIngredientRecipeAddIngredients.text = ingredient!!.title
        setupUnitPicker(ingredient)

        if (binding.editTextIngredientQuantityRecipeAddIngredients.text.isNullOrEmpty()) {
            setDefaultNutritionFacts(ingredient)
        } else {
            calculateNutrition(ingredient)
        }
    }

    private fun setDefaultNutritionFacts(ingredient: Ingredient) {
        val nutritionFacts = ingredient.unitRatio!!
        val kcal = nutritionFacts.kcal.toString()
        val qty = nutritionFacts.amount.toString()
        binding.textviewIngredientKcalsQtyRecipeAddIngredients.text =
            String.format("%s kcal/%s %s", kcal, qty, ingredient.unitRatio.unit!!.title)
        binding.tvIngredientProteinsRecipeAddIngredients.text = nutritionFacts.proteins.toString()
        binding.tvIngredientFatsRecipeAddIngredients.text = nutritionFacts.fats.toString()
        binding.tvIngredientCarbsRecipeAddIngredients.text = nutritionFacts.carbs.toString()
    }

    private fun setupUnitPicker(ingredient: Ingredient?) {
        val unitData: Array<String>
        if (ingredient != null) {
            units = (staticDataViewModel.ingredientUnits.value as ArrayList<IngredientUnit>?)!!
            when (val currentUnit = ingredient.unitRatio!!.unit) {
                staticDataViewModel.unitG.value -> {
                    units.remove(currentUnit)
                    units.add(0, currentUnit!!)
                }
                staticDataViewModel.unitML.value -> {
                    units.remove(currentUnit)
                    units.add(0, currentUnit!!)
                }
            }
            unitData = units.map { it.titleMultiply!! }.toTypedArray()

            selectedUnit = units[0]

            binding.pickerIngredientUnit.setOnValueChangedListener { _, _, newVal ->
                Log.d(
                    "PICKER NEW VAL =======================================================",
                    "$newVal"
                )
                selectedUnit = units[newVal]
                if (binding.editTextIngredientQuantityRecipeAddIngredients.text.isNullOrEmpty()) {
                    setDefaultNutritionFacts(ingredient)
                } else {
                    calculateNutrition(ingredient)
                }
            }
        } else {
            unitData = arrayOf("unit")
        }

        binding.pickerIngredientUnit.minValue = 0
        binding.pickerIngredientUnit.maxValue = unitData.size - 1
        binding.pickerIngredientUnit.displayedValues = unitData
        binding.pickerIngredientUnit.value = 0
    }

    private fun filter(text: String) {
        try {
            val temp = staticDataViewModel.ingredients.value!!.filter {
                StringUtils.startsWithIgnoreCase(
                    it.title,
                    text
                )
            }
            adapter!!.setData(temp)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility =
            View.VISIBLE
    }
}