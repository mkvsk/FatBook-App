package online.fatbook.fatbookapp.ui.fragment.recipe_create

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_recipe_create_second_stage.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.CookingStep
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateSecondStageBinding
import online.fatbook.fatbookapp.ui.adapters.CookingStepAdapter
import online.fatbook.fatbookapp.ui.adapters.RecipeIngredientAdapter
import online.fatbook.fatbookapp.ui.listeners.OnCookingStepClickListener
import online.fatbook.fatbookapp.ui.listeners.OnRecipeIngredientItemClickListener
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.util.FormatUtils
import online.fatbook.fatbookapp.util.obtainViewModel

class RecipeCreateSecondStageFragment : Fragment(), OnRecipeIngredientItemClickListener,
    OnCookingStepClickListener {

    private var binding: FragmentRecipeCreateSecondStageBinding? = null

    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }
    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }

    private var ingredientsAdapter: RecipeIngredientAdapter? = null
    private var cookingStepsAdapter: CookingStepAdapter? = null

    private var maxStepsQtt = 30
    private var maxIngredientsQtt = 10

    private val TAG = "RecipeCreateSecondStageFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeCreateSecondStageBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar_recipe_create_2_stage.title = "TODO title"
        setupMenu()
        checkEnableMenu()
        checkIngredientsQtt(recipeViewModel.newRecipe.value!!.ingredients!!.size)
        checkStepsQtt(recipeViewModel.newRecipe.value!!.steps!!.size)

        button_add_ingredient_recipe_create_2_stage.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_ingredient_from_second_stage)
        }

        cardview_add_cooking_step.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_step_from_second_stage)
        }
        setupIngredientsAdapter()
        setupCookingStepsAdapter()

        ingredientsAdapter!!.setData(recipeViewModel.newRecipe.value!!.ingredients)
        cookingStepsAdapter!!.setData(recipeViewModel.newRecipe.value!!.steps)

        recipeViewModel.selectedCookingStep.value = null

        nsv_recipe_create_2_stage.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->

            if (scrollY > 250) {
                floating_button_up_recipe_create.visibility = View.VISIBLE
            } else {
                floating_button_up_recipe_create.visibility = View.GONE
            }

            floating_button_up_recipe_create.setOnClickListener {
                nsv_recipe_create_2_stage.post {
                    nsv_recipe_create_2_stage.smoothScrollTo(0, 0)
                }
            }
        })
    }

    private fun checkEnableMenu() {
        val isEmpty =
            recipeViewModel.newRecipe.value!!.ingredients.isNullOrEmpty() || recipeViewModel.newRecipe.value!!.steps.isNullOrEmpty()
        toolbar_recipe_create_2_stage.menu.findItem(R.id.menu_create_second_stage_save_recipe).isVisible =
            !isEmpty
    }

    private fun setupMenu() {
        toolbar_recipe_create_2_stage.inflateMenu(R.menu.recipe_create_2_stage_menu)
        toolbar_recipe_create_2_stage.setOnMenuItemClickListener(this::onOptionsItemSelected)
        toolbar_recipe_create_2_stage.setNavigationOnClickListener {
            popBackStack()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_create_second_stage_save_recipe -> {
                checkRecipe()
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkRecipe() {
        Toast.makeText(requireContext(), "Recipe created!", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "${recipeViewModel.newRecipe.value}")
    }

    private fun checkIngredientsQtt(currentIngredientsQtt: Int) {
        if (currentIngredientsQtt == maxIngredientsQtt) {
            button_add_ingredient_recipe_create_2_stage.visibility = View.GONE
        }
        if (currentIngredientsQtt < maxIngredientsQtt) {
            button_add_ingredient_recipe_create_2_stage.visibility = View.VISIBLE
        }
        textview_ingredient_count_recipe_create_2_stage.text =
            String.format(
                getString(R.string.format_count),
                currentIngredientsQtt,
                maxIngredientsQtt
            )
    }

    private fun checkStepsQtt(currentStepsQtt: Int) {
        if (currentStepsQtt == maxStepsQtt) {
            cardview_add_cooking_step.visibility = View.GONE
        }
        if (currentStepsQtt < maxStepsQtt) {
            cardview_add_cooking_step.visibility = View.VISIBLE
        }
        textview_steps_count_recipe_create_2_stage.text =
            String.format(
                getString(R.string.format_count),
                currentStepsQtt,
                maxStepsQtt
            )
    }

    private fun setupIngredientsAdapter() {
        val rv = rv_ingredients_recipe_create_2_stage
        ingredientsAdapter = RecipeIngredientAdapter()
        ingredientsAdapter!!.setContext(requireContext())
        ingredientsAdapter!!.setClickListener(this)
        rv.adapter = ingredientsAdapter
    }

    //TODO ANIM
    override fun onRecipeIngredientDelete(selectedItem: Int) {
//        TransitionManager.go(Scene(cardview_right_recipe_create_2_stage), AutoTransition())
        recipeViewModel.newRecipe.value!!.ingredients!!.removeAt(selectedItem)
        ingredientsAdapter!!.notifyItemRemoved(selectedItem)
        drawNutritionFacts()
        checkIngredientsQtt(recipeViewModel.newRecipe.value!!.ingredients!!.size)
        checkEnableMenu()
    }

    private fun setupCookingStepsAdapter() {
        val rv = rv_steps_recipe_create_2_stage
        cookingStepsAdapter = CookingStepAdapter()
        cookingStepsAdapter!!.setClickListener(this)
        rv.adapter = cookingStepsAdapter
    }

    override fun onCookingStepClick(value: CookingStep, itemPosition: Int) {
        recipeViewModel.selectedCookingStep.value = value
        recipeViewModel.selectedCookingStepPosition.value = itemPosition
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_go_to_create_cooking_step_from_second_stage)
    }

    //TODO ANIM
    override fun onRecipeCookingStepDelete(itemPosition: Int) {
//        TransitionManager.go(Scene(rv_steps_recipe_create_2_stage), AutoTransition())
        recipeViewModel.newRecipe.value!!.steps!!.removeAt(itemPosition)
        cookingStepsAdapter!!.notifyItemRemoved(itemPosition)
        checkStepsQtt(recipeViewModel.newRecipe.value!!.steps!!.size)
        checkEnableMenu()
    }

    override fun onResume() {
        super.onResume()
        drawNutritionFacts()
    }

    private fun drawNutritionFacts() {
        if (recipeViewModel.newRecipe.value!!.isAllIngredientUnitsValid
            && !recipeViewModel.newRecipe.value!!.ingredients.isNullOrEmpty()
        ) {
            showNutritionFacts(true)
            textview_portion_kcals_qtt_recipe_create_2_stage.text =
                FormatUtils.prettyCount(
                    recipeViewModel.newRecipe.value?.kcalPerPortion.toString().toDouble()
                )
            tv_qtt_proteins.text = FormatUtils.prettyCount(
                recipeViewModel.newRecipe.value?.proteinsPerPortion.toString().toDouble()
            )
            tv_qtt_fats.text = FormatUtils.prettyCount(
                recipeViewModel.newRecipe.value?.fatsPerPortion.toString().toDouble()
            )
            tv_qtt_carbs.text = FormatUtils.prettyCount(
                recipeViewModel.newRecipe.value?.carbsPerPortion.toString().toDouble()
            )
        } else {
            showNutritionFacts(false)
        }
    }

    private fun showNutritionFacts(value: Boolean) {
        //TODO ANIM cards resize animation
        if (value) {
            textview_nutrition_facts_title_recipe_create_2_stage.visibility = View.VISIBLE
            cardview_nutrition_facts_recipe_create_2_stage.visibility = View.VISIBLE
        } else {
            textview_nutrition_facts_title_recipe_create_2_stage.visibility = View.GONE
            cardview_nutrition_facts_recipe_create_2_stage.visibility = View.GONE
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
}