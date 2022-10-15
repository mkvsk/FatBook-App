package online.fatbook.fatbookapp.ui.fragment.recipe.create

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
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
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils

class RecipeCreateAddIngredientsFragment : Fragment(), OnIngredientItemClickListener {

    private var binding: FragmentRecipeCreateAddIngredientsBinding? = null
    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }
    private var adapter: IngredientAdapter? = null
    private var units: List<IngredientUnit> = ArrayList()

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
        progress_overlay.visibility = View.GONE
//        handleBackPressed()

        edittext_search_recipe_add_ingredients.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(edittext_search_recipe_add_ingredients.text.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        editText_ingredient_quantity_recipe_add_ingredients.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                checkData()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkData()
            }

            override fun afterTextChanged(s: Editable?) {
                checkData()
            }

        })
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
        val activity = (activity as AppCompatActivity?)!!
        activity.setSupportActionBar(toolbar_recipe_add_ingredients)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_recipe_ingredient_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add_ingredient_to_recipe -> {
                //TODO fix
                checkData()
                addIngredient()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addIngredient() {
        selectedUnit = units[picker_ingredient_unit.value]

        selectedQtt = if (editText_ingredient_quantity_recipe_add_ingredients.text.isNullOrEmpty()
        ) {
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
        adapter!!.setClickListener(this)
        rv.adapter = adapter
    }

    private fun loadIngredients() {
        staticDataViewModel.getAllIngredients(object : ResultCallback<List<Ingredient>> {
            override fun onResult(value: List<Ingredient>?) {
                staticDataViewModel.ingredients.value = value
                adapter?.setData(value)
            }

            override fun onFailure(value: List<Ingredient>?) {
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

        setNutritionFacts(ingredient)
    }

    private fun setNutritionFacts(ingredient: Ingredient) {
        val nutritionFacts = ingredient.units!!.find { it.unit!! == selectedUnit }

        val kcal = nutritionFacts?.kcal.toString()
        val qtt = nutritionFacts?.amount.toString()
        textview_ingredient_kcals_qtt_recipe_add_ingredients.text =
            String.format("%s kcal/%s gram", kcal, qtt)

        tv_ingredient_proteins_recipe_add_ingredients.text = nutritionFacts?.proteins.toString()
        tv_ingredient_fats_recipe_add_ingredients.text = nutritionFacts?.fats.toString()
        tv_ingredient_carbs_recipe_add_ingredients.text = nutritionFacts?.carbs.toString()
    }

    private fun setupUnitPicker(ingredient: Ingredient?) {
        val unitData: Array<String>

        if (ingredient != null) {
            units = ingredient.units!!.map { it.unit!! }
            unitData = units.map { it.getMultiplyNaming(requireContext()) }.toTypedArray()

            selectedUnit = units[0]

            picker_ingredient_unit.setOnValueChangedListener { _, _, newVal ->
                selectedUnit = units[newVal]
                setNutritionFacts(ingredient)
            }
        } else {
            unitData = arrayOf("unit")
        }

        picker_ingredient_unit.minValue = 0
        picker_ingredient_unit.maxValue = unitData.size - 1
        picker_ingredient_unit.displayedValues = unitData
        picker_ingredient_unit.value = 0
    }

    //TODO sdelat' K P A C U B O
    private fun filter(text: String) {
        try {
            var temp: List<Ingredient> = ArrayList()
//            for (r in staticDataViewModel.ingredients.value!!) {
//                if (StringUtils.containsIgnoreCase(r.title, text)) {
//                    temp.add(r)
//                }
//            }
            temp = staticDataViewModel.ingredients.value!!.filter {
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