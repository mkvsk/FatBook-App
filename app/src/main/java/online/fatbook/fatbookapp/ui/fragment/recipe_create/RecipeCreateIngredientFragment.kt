package online.fatbook.fatbookapp.ui.fragment.recipe_create

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.NavHostFragment
import androidx.transition.AutoTransition
import androidx.transition.Scene
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.fragment_recipe_create_add_ingredients.*
import kotlinx.android.synthetic.main.include_progress_overlay.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.core.recipe.ingredient.IngredientUnit
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateAddIngredientsBinding
import online.fatbook.fatbookapp.ui.adapters.IngredientAdapter
import online.fatbook.fatbookapp.ui.listeners.OnIngredientItemClickListener
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.util.FormatUtils
import online.fatbook.fatbookapp.util.RecipeUtils
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils

class RecipeCreateIngredientFragment : Fragment(), OnIngredientItemClickListener {

    private var binding: FragmentRecipeCreateAddIngredientsBinding? = null
    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }
    private var adapter: IngredientAdapter? = null
    private var units: ArrayList<IngredientUnit> = ArrayList()

    private var selectedUnit: IngredientUnit? = null
    private var selectedQtt: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeCreateAddIngredientsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress_overlay.visibility = View.VISIBLE
        loadIngredients()
        setupIngredientsAdapter()
        setupUnitPicker(null)
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

    private fun calculateNutrition(ingredient: Ingredient) {
        val nutritionFacts = ingredient.units!![0]
        if (selectedUnit == IngredientUnit.GRAM ||
            selectedUnit == IngredientUnit.ML
        ) {
            if (nutritionFacts.unit == selectedUnit) {
                val newQtt =
                    editText_ingredient_quantity_recipe_add_ingredients.text.toString().toDouble()
                val kcal = nutritionFacts.kcal!!
                val proteins = nutritionFacts.proteins!!
                val fats = nutritionFacts.fats!!
                val carbs = nutritionFacts.carbs!!
                val newKcal = (kcal / 100 * newQtt)

                val newProteins = (proteins / 100 * newQtt)
                val newFats = (fats / 100 * newQtt)
                val newCarbs = (carbs / 100 * newQtt)
                textview_ingredient_kcals_qtt_recipe_add_ingredients.text =
                    String.format(
                        getString(R.string.format_kcal),
                        FormatUtils.prettyCount(newKcal.toString().toDouble())
                    )

                tv_ingredient_proteins_recipe_add_ingredients.text =
                    FormatUtils.prettyCount(newProteins.toString().toDouble())
                tv_ingredient_fats_recipe_add_ingredients.text =
                    FormatUtils.prettyCount(newFats.toString().toDouble())
                tv_ingredient_carbs_recipe_add_ingredients.text =
                    FormatUtils.prettyCount(newCarbs.toString().toDouble())
            } else {
                setDefaultNutritionFacts(ingredient)
            }
        } else {
            setDefaultNutritionFacts(ingredient)
        }
    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    popBackStack()
                }
            })
    }

    private fun popBackStack() {
        NavHostFragment.findNavController(this).popBackStack()
    }

    private fun setupMenu() {
        toolbar_recipe_add_ingredients.inflateMenu(R.menu.add_recipe_ingredient_menu)
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
        val nutritionFacts = ingredient.units!![0]
        val kcal = nutritionFacts.kcal.toString()
        val qtt = nutritionFacts.amount.toString()
        textview_ingredient_kcals_qtt_recipe_add_ingredients.text =
            String.format("%s kcal/%s %s", kcal, qtt, ingredient.units[0].unit)
        tv_ingredient_proteins_recipe_add_ingredients.text = nutritionFacts.proteins.toString()
        tv_ingredient_fats_recipe_add_ingredients.text = nutritionFacts.fats.toString()
        tv_ingredient_carbs_recipe_add_ingredients.text = nutritionFacts.carbs.toString()
    }

    private fun setupUnitPicker(ingredient: Ingredient?) {
        val unitData: Array<String>
        if (ingredient != null) {
            val currentUnit = ingredient.units!![0].unit

            units = RecipeUtils.getAllAvailableUnits()

            when (currentUnit) {
                IngredientUnit.GRAM -> {
                    units.remove(currentUnit)
                    units[0] = currentUnit
                }
                IngredientUnit.ML -> {
                    units.remove(currentUnit)
                    units[0] = currentUnit
                }
            }
            unitData = units.map { it.getMultiplyNaming(requireContext()) }.toTypedArray()

            selectedUnit = units[0]

            picker_ingredient_unit.setOnValueChangedListener { _, _, newVal ->
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