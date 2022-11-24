package online.fatbook.fatbookapp.ui.fragment.recipe

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.transition.AutoTransition
import androidx.transition.Scene
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.fragment_recipe_ingredient.*
import kotlinx.android.synthetic.main.include_progress_overlay.*
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

    private var binding: FragmentRecipeIngredientBinding? = null
    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }
    private var adapter: IngredientAdapter? = null
    private var units: ArrayList<IngredientUnit> = ArrayList()

    private var selectedUnit: IngredientUnit? = null
    private var selectedQtt: Double = 0.0
    private var newQtt: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeIngredientBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress_overlay.visibility = View.VISIBLE
        loadIngredients()
        loadIngredientUnits()
        setupIngredientsAdapter()
        setupMenu()
//        handleBackPressed()

        cardview_left_recipe_add_ingredients.visibility = View.GONE
        textView_selected_ingredient_recipe_add_ingredients.doOnTextChanged { _, _, _, _ ->
            toolbar_recipe_add_ingredients.menu.findItem(R.id.menu_add_ingredient_to_recipe).isVisible =
                !editText_ingredient_quantity_recipe_add_ingredients.text.isNullOrEmpty()
            TransitionManager.go(Scene(cardview_left_recipe_add_ingredients), AutoTransition())
            TransitionManager.go(Scene(cardview_right_recipe_add_ingredients), AutoTransition())
            cardview_left_recipe_add_ingredients.visibility = View.VISIBLE
            editText_ingredient_quantity_recipe_add_ingredients.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(s: CharSequence, i1: Int, i2: Int, i3: Int) {
                }

                override fun onTextChanged(s: CharSequence, i1: Int, i2: Int, i3: Int) {
                    checkData()
                    if (s.isEmpty()) {
                        toolbar_recipe_add_ingredients.menu.findItem(R.id.menu_add_ingredient_to_recipe).isVisible =
                            false
                        recipeViewModel.newRecipeAddIngredient.value?.let {
                            setDefaultNutritionFacts(
                                it
                            )
                        }
                    } else {
                        toolbar_recipe_add_ingredients.menu.findItem(R.id.menu_add_ingredient_to_recipe).isVisible =
                            true
                        recipeViewModel.newRecipeAddIngredient.value?.let { calculateNutrition(it) }
                    }
                }

                override fun afterTextChanged(s: Editable) {
                }
            })
        }

        edittext_search_recipe_add_ingredients.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, i1: Int, i2: Int, i3: Int) {
            }

            override fun onTextChanged(s: CharSequence, i1: Int, i2: Int, i3: Int) {
                filter(edittext_search_recipe_add_ingredients.text.toString())
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
                    staticDataViewModel.ingredientUnits.value = value
                    staticDataViewModel.unitG.value =
                        value.find { ingredientUnit -> ingredientUnit.ordinal == 1 }
                    staticDataViewModel.unitML.value =
                        value.find { ingredientUnit -> ingredientUnit.ordinal == 2 }
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
        newQtt =
            editText_ingredient_quantity_recipe_add_ingredients.text.toString().toDouble()

        textview_ingredient_kcals_qtt_recipe_add_ingredients.text =
            String.format(
                getString(R.string.format_kcal),
                FormatUtils.prettyCount(
                    (nutritionFacts.kcal!! / 100 * newQtt) * 1000.toString().toDouble()
                )
            )

        tv_ingredient_proteins_recipe_add_ingredients.text =
            FormatUtils.prettyCount(
                (nutritionFacts.proteins!! / 100 * newQtt) * 1000.toString().toDouble()
            )
        tv_ingredient_fats_recipe_add_ingredients.text =
            FormatUtils.prettyCount(
                (nutritionFacts.fats!! / 100 * newQtt) * 1000.toString().toDouble()
            )
        tv_ingredient_carbs_recipe_add_ingredients.text =
            FormatUtils.prettyCount(
                (nutritionFacts.carbs!! / 100 * newQtt) * 1000.toString().toDouble()
            )
    }

    private fun setNutritionFactsForMlAndG(nutritionFacts: IngredientUnitRatio) {
        newQtt =
            editText_ingredient_quantity_recipe_add_ingredients.text.toString().toDouble()

        textview_ingredient_kcals_qtt_recipe_add_ingredients.text =
            String.format(
                getString(R.string.format_kcal),
                FormatUtils.prettyCount(
                    (nutritionFacts.kcal!! / 100 * newQtt).toString().toDouble()
                )
            )

        tv_ingredient_proteins_recipe_add_ingredients.text =
            FormatUtils.prettyCount(
                (nutritionFacts.proteins!! / 100 * newQtt).toString().toDouble()
            )
        tv_ingredient_fats_recipe_add_ingredients.text =
            FormatUtils.prettyCount((nutritionFacts.fats!! / 100 * newQtt).toString().toDouble())
        tv_ingredient_carbs_recipe_add_ingredients.text =
            FormatUtils.prettyCount((nutritionFacts.carbs!! / 100 * newQtt).toString().toDouble())
    }

    private fun popBackStack() {
        NavHostFragment.findNavController(this).popBackStack()
    }

    private fun setupMenu() {
        toolbar_recipe_add_ingredients.inflateMenu(R.menu.recipe_create_ingredient_menu)
        toolbar_recipe_add_ingredients.setOnMenuItemClickListener(this::onOptionsItemSelected)
        toolbar_recipe_add_ingredients.setNavigationOnClickListener {
            popBackStack()
        }
        toolbar_recipe_add_ingredients.menu.findItem(R.id.menu_add_ingredient_to_recipe).isVisible =
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
        selectedUnit = units[picker_ingredient_unit.value]
        selectedQtt =
            if (editText_ingredient_quantity_recipe_add_ingredients.text.isNullOrEmpty()) {
                1.0
            } else {
                editText_ingredient_quantity_recipe_add_ingredients.text.toString().toDouble()
            }

        val recipeIngredient = RecipeIngredient(
            pid = null,
            ingredient = recipeViewModel.newRecipeAddIngredient.value,
            unit = selectedUnit,
            quantity = selectedQtt
        )
        recipeViewModel.newRecipe.value!!.ingredients!!.add(recipeIngredient)
        NavHostFragment.findNavController(this).popBackStack()
    }

    private fun checkData() {
        if (editText_ingredient_quantity_recipe_add_ingredients.text.toString().isNotEmpty()
            && editText_ingredient_quantity_recipe_add_ingredients.text.toString()
                .toDouble() != 0.0
        ) {
            editText_ingredient_quantity_recipe_add_ingredients.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_corner_ingredient_qtt_units
                )
        } else {
            editText_ingredient_quantity_recipe_add_ingredients.isFocusable
            editText_ingredient_quantity_recipe_add_ingredients.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_corner_ingredient_qtt_units_stroke
                )
        }
    }

    private fun setupIngredientsAdapter() {
        val rv = rv_ingredients_recipe_add_ingredients
        adapter = IngredientAdapter()
        adapter!!.setContext(requireContext())
        adapter!!.setClickListener(this)
        rv.adapter = adapter
    }

    private fun loadIngredients() {
        staticDataViewModel.getAllIngredients(object : ResultCallback<List<Ingredient>> {
            override fun onResult(value: List<Ingredient>?) {
                staticDataViewModel.ingredients.value = value
                adapter?.setData(value)
                progress_overlay.visibility = View.GONE
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
        recipeViewModel.newRecipeAddIngredient.value = ingredient

        textView_selected_ingredient_recipe_add_ingredients.text = ingredient!!.title
        setupUnitPicker(ingredient)

        if (editText_ingredient_quantity_recipe_add_ingredients.text.isNullOrEmpty()) {
            setDefaultNutritionFacts(ingredient)
        } else {
            calculateNutrition(ingredient)
        }
    }

    private fun setDefaultNutritionFacts(ingredient: Ingredient) {
        val nutritionFacts = ingredient.unitRatio!!
        val kcal = nutritionFacts.kcal.toString()
        val qtt = nutritionFacts.amount.toString()
        textview_ingredient_kcals_qtt_recipe_add_ingredients.text =
            String.format("%s kcal/%s %s", kcal, qtt, ingredient.unitRatio.unit!!.title)
        tv_ingredient_proteins_recipe_add_ingredients.text = nutritionFacts.proteins.toString()
        tv_ingredient_fats_recipe_add_ingredients.text = nutritionFacts.fats.toString()
        tv_ingredient_carbs_recipe_add_ingredients.text = nutritionFacts.carbs.toString()
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

            picker_ingredient_unit.setOnValueChangedListener { _, _, newVal ->
                Log.d(
                    "PICKER NEW VAL =======================================================",
                    "$newVal"
                )
                selectedUnit = units[newVal]
                if (editText_ingredient_quantity_recipe_add_ingredients.text.isNullOrEmpty()) {
                    setDefaultNutritionFacts(ingredient)
                } else {
                    calculateNutrition(ingredient)
                }
            }
        } else {
            unitData = arrayOf("unit")
        }

        picker_ingredient_unit.minValue = 0
        picker_ingredient_unit.maxValue = unitData.size - 1
        picker_ingredient_unit.displayedValues = unitData
        picker_ingredient_unit.value = 0
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
}