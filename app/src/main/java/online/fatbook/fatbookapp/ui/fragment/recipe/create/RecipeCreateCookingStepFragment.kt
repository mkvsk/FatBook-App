package online.fatbook.fatbookapp.ui.fragment.recipe.create

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_recipe_create_cooking_step.*
import online.fatbook.fatbookapp.core.recipe.CookingStep
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateCookingStepBinding
import online.fatbook.fatbookapp.ui.adapters.CookingStepAdapter
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class RecipeCreateCookingStepFragment : Fragment() {

    private var binding: FragmentRecipeCreateCookingStepBinding? = null
    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private var adapter: CookingStepAdapter? = null
    private var cookingStep: CookingStep? = CookingStep()
    private var selectedCookingStep: CookingStep? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeCreateCookingStepBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (recipeViewModel.selectedCookingStep.value != null) {
            selectedCookingStep = recipeViewModel.selectedCookingStep.value
            edittext_recipe_create_cooking_step.setText(selectedCookingStep!!.description.toString())
        }

        edittext_recipe_create_cooking_step.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                button_add_recipe_create_cooking_step.isEnabled =
                    !edittext_recipe_create_cooking_step.text.isNullOrEmpty()
            }
        })

        button_add_recipe_create_cooking_step.setOnClickListener {
            if (selectedCookingStep != null) {
                selectedCookingStep!!.description =
                    edittext_recipe_create_cooking_step.text.toString()
                adapter?.notifyItemChanged(recipeViewModel.selectedCookingStepPosition.value!!)
            } else {
                var cookingStepsAmount = recipeViewModel.newRecipe.value!!.steps!!.size

                cookingStep!!.description = edittext_recipe_create_cooking_step.text.toString()
                cookingStep!!.stepNumber = ++cookingStepsAmount

                recipeViewModel.newRecipe.value!!.steps!!.add(cookingStep!!)
                adapter?.notifyDataSetChanged()
            }
            recipeViewModel.selectedCookingStep.value = null
            recipeViewModel.selectedCookingStepPosition.value = null
            NavHostFragment.findNavController(this).popBackStack()
        }
    }
}