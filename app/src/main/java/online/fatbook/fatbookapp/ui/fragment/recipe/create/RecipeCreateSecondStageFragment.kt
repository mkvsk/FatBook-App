package online.fatbook.fatbookapp.ui.fragment.recipe.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.NavHostFragment
import androidx.transition.AutoTransition
import androidx.transition.Scene
import androidx.transition.TransitionManager
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
import online.fatbook.fatbookapp.util.obtainViewModel

class RecipeCreateSecondStageFragment : Fragment(), OnRecipeIngredientItemClickListener,
    OnCookingStepClickListener {

    private var binding: FragmentRecipeCreateSecondStageBinding? = null

    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }
    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }

    private var ingredientsAdapter: RecipeIngredientAdapter? = null
    private var cookingStepsAdapter: CookingStepAdapter? = null

    private var currentStepsQtt: Int? = 0
    private var maxStepsQtt = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeCreateSecondStageBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentStepsQtt = recipeViewModel.newRecipe.value!!.steps!!.size
        checkSteps(currentStepsQtt!!)

        button_add_ingredient_recipe_create_2_stage.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_add_ingredient_from_second_stage)
        }

        cardview_add_cooking_step.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_create_cooking_step_from_second_stage)
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

    private fun checkSteps(currentStepsQtt: Int) {
        if (currentStepsQtt == maxStepsQtt) {
            cardview_add_cooking_step.visibility = View.GONE
        }
        if (currentStepsQtt < maxStepsQtt) {
            TransitionManager.go(Scene(cardview_add_cooking_step), AutoTransition())
            cardview_add_cooking_step.visibility = View.VISIBLE
        }
        textview_steps_title_recipe_create_2_stage.text =
            String.format("Cooking steps: (%s/%s)", currentStepsQtt, maxStepsQtt)
    }

    private fun setupIngredientsAdapter() {
        val rv = rv_ingredients_recipe_create_2_stage
        ingredientsAdapter = RecipeIngredientAdapter()
        ingredientsAdapter!!.setClickListener(this)
        rv.adapter = ingredientsAdapter
    }

    override fun onRecipeIngredientDelete(selectedItem: Int) {
        TransitionManager.go(Scene(cardview_right_recipe_create_2_stage), AutoTransition())
        recipeViewModel.newRecipe.value!!.ingredients!!.removeAt(selectedItem)
        ingredientsAdapter!!.notifyItemRemoved(selectedItem)
        drawNutritionFacts()
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

    override fun onRecipeCookingStepDelete(itemPosition: Int) {
        TransitionManager.go(Scene(rv_steps_recipe_create_2_stage), AutoTransition())
        recipeViewModel.newRecipe.value!!.steps!!.removeAt(itemPosition)
        cookingStepsAdapter!!.notifyItemRemoved(itemPosition)

        if (currentStepsQtt != 0) {
            currentStepsQtt = recipeViewModel.newRecipe.value!!.steps!!.size
            checkSteps(currentStepsQtt!!)
        }
    }

    override fun onResume() {
        super.onResume()
        drawNutritionFacts()
    }

    private fun drawNutritionFacts() {
        if (recipeViewModel.newRecipe.value!!.isAllIngredientUnitsValid) {
            showNutritionFacts(true)
            textview_portion_kcals_qtt_recipe_create_2_stage.text = recipeViewModel.newRecipe.value?.kcalPerPortion.toString()
            //TODO остальные крабы
        } else {
            showNutritionFacts(false)
        }

    }

    private fun showNutritionFacts(value: Boolean) {
        if (value) {
            //View.VISIBLE
        } else {
            //View.GONE
        }
    }
}