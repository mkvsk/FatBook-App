package online.fatbook.fatbookapp.ui.fragment.recipe

import android.content.DialogInterface
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
import androidx.navigation.fragment.findNavController
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingStep
import online.fatbook.fatbookapp.databinding.FragmentRecipeSecondStageBinding
import online.fatbook.fatbookapp.ui.adapters.CookingStepAdapter
import online.fatbook.fatbookapp.ui.adapters.RecipeIngredientAdapter
import online.fatbook.fatbookapp.ui.listeners.OnCookingStepClickListener
import online.fatbook.fatbookapp.ui.listeners.OnRecipeIngredientItemClickListener
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.FormatUtils
import online.fatbook.fatbookapp.util.alert_dialog.FBAlertDialogBuilder
import online.fatbook.fatbookapp.util.alert_dialog.FBAlertDialogListener
import online.fatbook.fatbookapp.util.obtainViewModel
import java.util.*
import kotlin.math.roundToInt

class RecipeSecondStageFragment : Fragment(), OnRecipeIngredientItemClickListener,
        OnCookingStepClickListener {

    private var _binding: FragmentRecipeSecondStageBinding? = null
    private val binding get() = _binding!!

    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }
    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }

    private var ingredientsAdapter: RecipeIngredientAdapter? = null
    private var cookingStepsAdapter: CookingStepAdapter? = null

    private var maxStepsQtt = 10
    private var maxIngredientsQtt = 30

    private val TAG = "RecipeCreateSecondStageFragment"

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeSecondStageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarRecipeCreate2Stage.title = "TODO title"
        setupMenu()
        checkEnableMenu()
        checkIngredientsQtt(recipeViewModel.newRecipe.value!!.ingredients!!.size)
        checkStepsQtt(recipeViewModel.newRecipe.value!!.steps!!.size)
        binding.buttonAddIngredientRecipeCreate2Stage.setOnClickListener {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_ingredient_from_second_stage)
        }
        binding.cardviewAddCookingStep.setOnClickListener {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_step_from_second_stage)
        }
        setupIngredientsAdapter()
        setupCookingStepsAdapter()

        ingredientsAdapter!!.setData(recipeViewModel.newRecipe.value!!.ingredients)
        cookingStepsAdapter!!.setData(recipeViewModel.newRecipe.value!!.steps)
        cookingStepsAdapter!!.setImages(recipeViewModel.newRecipeStepImages.value)

        recipeViewModel.setSelectedCookingStep(null)

        binding.nsvRecipeCreate2Stage.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->

            if (scrollY > 250) {
                binding.floatingButtonUpRecipeCreate.visibility = View.VISIBLE
            } else {
                binding.floatingButtonUpRecipeCreate.visibility = View.GONE
            }

            binding.floatingButtonUpRecipeCreate.setOnClickListener {
                binding.nsvRecipeCreate2Stage.post {
                    binding.nsvRecipeCreate2Stage.smoothScrollTo(0, 0)
                }
            }
        })
    }

    private fun checkEnableMenu() {
        val isEmpty =
                recipeViewModel.newRecipe.value!!.ingredients.isNullOrEmpty() || recipeViewModel.newRecipe.value!!.steps.isNullOrEmpty()
        binding.toolbarRecipeCreate2Stage.menu.findItem(R.id.menu_create_second_stage_save_recipe).isVisible =
                !isEmpty
    }

    private fun setupMenu() {
        binding.toolbarRecipeCreate2Stage.inflateMenu(R.menu.recipe_create_2_stage_menu)
        binding.toolbarRecipeCreate2Stage.setOnMenuItemClickListener(this::onOptionsItemSelected)
        binding.toolbarRecipeCreate2Stage.setNavigationOnClickListener {
            popBackStack()
        }
    }

    //TODO recipe create images
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_create_second_stage_save_recipe -> {
//                progress_overlay.visibility = View.VISIBLE
//                toolbar_recipe_create_2_stage.visibility = View.GONE
                checkRecipe()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkRecipe() {
        if (recipeViewModel.newRecipeImage.value == null && !recipeViewModel.newRecipe.value!!.isPrivate!!) {
            FBAlertDialogBuilder.getDialogWithPositiveAndNegativeButtons(
                    title = "Notice",
                    msg = "Recipe without an image will be visible only for you. Would you still want to create new recipe?",
                    positiveBtnListener = object : FBAlertDialogListener {
                        override fun onClick(dialogInterface: DialogInterface) {
                            recipeViewModel.newRecipe.value!!.isPrivate = true
                            dialogInterface.dismiss()
                            fillRecipe()
                            saveRecipe()
                        }
                    },
                    positiveBtnText = "Yes",
                    negativeBtnText = "No"
            ).show()
        } else {
            fillRecipe()
            saveRecipe()
        }
    }

    private fun fillRecipe() {
        recipeViewModel.newRecipe.value!!.createDate = FormatUtils.dateFormat.format(Date())
        with(recipeViewModel.newRecipe.value!!) {
            kcalPerPortion = (kcalPerPortion!! * 100.0).roundToInt() / 100.0
            fatsPerPortion = (fatsPerPortion!! * 100.0).roundToInt() / 100.0
            carbsPerPortion = (carbsPerPortion!! * 100.0).roundToInt() / 100.0
            proteinsPerPortion = (proteinsPerPortion!! * 100.0).roundToInt() / 100.0
        }
    }

    //TODO remove toast, uncomment popbackstack
    private fun saveRecipe() {
        recipeViewModel.recipeCreate(
                recipeViewModel.newRecipe.value!!, object : ResultCallback<Void> {
            override fun onResult(value: Void?) {
//                progress_overlay.visibility = View.GONE
//                toolbar_recipe_create_2_stage.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Recipe created!", Toast.LENGTH_SHORT).show()
                    recipeViewModel.setIsRecipeCreated(true)
                    popBackStack()
                }

            override fun onFailure(value: Void?) {
                Toast.makeText(requireContext(), "Error!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkIngredientsQtt(currentIngredientsQtt: Int) {
        if (currentIngredientsQtt == maxIngredientsQtt) {
            binding.buttonAddIngredientRecipeCreate2Stage.visibility = View.GONE
        }
        if (currentIngredientsQtt < maxIngredientsQtt) {
            binding.buttonAddIngredientRecipeCreate2Stage.visibility = View.VISIBLE
        }
        binding.textviewIngredientCountRecipeCreate2Stage.text =
                String.format(
                        getString(R.string.format_count),
                        currentIngredientsQtt,
                        maxIngredientsQtt
                )
    }

    private fun checkStepsQtt(currentStepsQtt: Int) {
        if (currentStepsQtt == maxStepsQtt) {
            binding.cardviewAddCookingStep.visibility = View.GONE
        }
        if (currentStepsQtt < maxStepsQtt) {
            binding.cardviewAddCookingStep.visibility = View.VISIBLE
        }
        binding.textviewStepsCountRecipeCreate2Stage.text =
                String.format(
                        getString(R.string.format_count),
                        currentStepsQtt,
                        maxStepsQtt
                )
    }

    private fun setupIngredientsAdapter() {
        val rv = binding.rvIngredientsRecipeCreate2Stage
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
        val rv = binding.rvStepsRecipeCreate2Stage
        cookingStepsAdapter = CookingStepAdapter(requireContext())
        cookingStepsAdapter!!.setClickListener(this)
        rv.adapter = cookingStepsAdapter
    }

    override fun onCookingStepClick(value: CookingStep, itemPosition: Int) {
        recipeViewModel.setSelectedCookingStep(value)
        recipeViewModel.setSelectedCookingStepPosition(itemPosition)
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_step_from_second_stage)
    }

    //TODO ANIM
    override fun onRecipeCookingStepDelete(itemPosition: Int) {
//        TransitionManager.go(Scene(rv_steps_recipe_create_2_stage), AutoTransition())
        recipeViewModel.newRecipe.value!!.steps!!.removeAt(itemPosition)
        recipeViewModel.newRecipeStepImages.value!!.remove(itemPosition + 1)
        cookingStepsAdapter!!.notifyItemRemoved(itemPosition)
        checkStepsQtt(recipeViewModel.newRecipe.value!!.steps!!.size)
        checkEnableMenu()
        Log.d(TAG, "${recipeViewModel.newRecipe.value!!.steps}")
        Log.d(TAG, "${recipeViewModel.newRecipeStepImages.value}")
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
            binding.textviewPortionKcalsQttRecipeCreate2Stage.text =
                    FormatUtils.prettyCount(
                            recipeViewModel.newRecipe.value?.kcalPerPortion.toString().toDouble()
                    )
            binding.tvQttProteins.text = FormatUtils.prettyCount(
                    recipeViewModel.newRecipe.value?.proteinsPerPortion.toString().toDouble()
            )
            binding.tvQttFats.text = FormatUtils.prettyCount(
                    recipeViewModel.newRecipe.value?.fatsPerPortion.toString().toDouble()
            )
            binding.tvQttCarbs.text = FormatUtils.prettyCount(
                    recipeViewModel.newRecipe.value?.carbsPerPortion.toString().toDouble()
            )
        } else {
            showNutritionFacts(false)
        }
    }

    private fun showNutritionFacts(value: Boolean) {
        //TODO ANIM cards resize animation
        if (value) {
            binding.textviewNutritionFactsTitleRecipeCreate2Stage.visibility = View.VISIBLE
            binding.cardviewNutritionFactsRecipeCreate2Stage.visibility = View.VISIBLE
        } else {
            binding.textviewNutritionFactsTitleRecipeCreate2Stage.visibility = View.GONE
            binding.cardviewNutritionFactsRecipeCreate2Stage.visibility = View.GONE
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
        findNavController().popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}