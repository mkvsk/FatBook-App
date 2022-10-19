package online.fatbook.fatbookapp.ui.fragment.recipe.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.transition.AutoTransition
import androidx.transition.Scene
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.fragment_recipe_create_second_stage.*
import kotlinx.android.synthetic.main.rv_feed_recipe_card_preview.*
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeCreateSecondStageBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }

    private fun setupCookingStepsAdapter() {
        val rv = rv_steps_recipe_create_2_stage
        cookingStepsAdapter = CookingStepAdapter()
        cookingStepsAdapter!!.setClickListener(this)
        rv.adapter = cookingStepsAdapter
    }

    override fun onCookingStepClick(selectedStep: Int, step: Int, value: CookingStep) {

    }

    override fun onRecipeCookingStepDelete(selectedItem: Int) {
        TransitionManager.go(Scene(rv_card_recipe_preview), AutoTransition())
        recipeViewModel.newRecipe.value!!.steps!!.removeAt(selectedItem)
        cookingStepsAdapter!!.notifyItemRemoved(selectedItem)
    }


}