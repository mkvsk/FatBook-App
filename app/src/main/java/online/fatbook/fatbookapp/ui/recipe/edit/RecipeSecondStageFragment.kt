package online.fatbook.fatbookapp.ui.recipe.edit

import android.content.DialogInterface
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.rv_cooking_step_preview.view.*
import okhttp3.RequestBody.Companion.asRequestBody
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.CookingStep
import online.fatbook.fatbookapp.databinding.FragmentRecipeSecondStageBinding
import online.fatbook.fatbookapp.network.callback.ResultCallback
import online.fatbook.fatbookapp.ui.image.viewmodel.ImageViewModel
import online.fatbook.fatbookapp.ui.recipe.adapters.CookingStepAdapter
import online.fatbook.fatbookapp.ui.recipe.adapters.RecipeIngredientAdapter
import online.fatbook.fatbookapp.ui.recipe.listeners.OnCookingStepClickListener
import online.fatbook.fatbookapp.ui.recipe.listeners.OnRecipeIngredientItemClickListener
import online.fatbook.fatbookapp.ui.recipe.viewmodel.RecipeEditViewModel
import online.fatbook.fatbookapp.ui.user.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.Constants
import online.fatbook.fatbookapp.util.Constants.CDN_FB_BASE_URL
import online.fatbook.fatbookapp.util.Constants.getRecipeImageName
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

    private val recipeEditViewModel by lazy { obtainViewModel(RecipeEditViewModel::class.java) }
    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }
    private val imageViewModel by lazy { obtainViewModel(ImageViewModel::class.java) }

    private var ingredientsAdapter: RecipeIngredientAdapter? = null
    private var cookingStepsAdapter: CookingStepAdapter? = null

    private var maxStepsQty = 10
    private var maxIngredientsQty = 30

    companion object {
        private const val TAG = "RecipeCreateSecondStageFragment"
    }

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
        recipeEditViewModel.setIsLoading(false)
        setupMenu()
        checkEnableMenu()
        initObservers()
        initListeners()
        checkIngredientsQty(recipeEditViewModel.recipe.value!!.ingredients!!.size)
        checkStepsQty(recipeEditViewModel.recipe.value!!.steps!!.size)
        setupIngredientsAdapter()
        setupCookingStepsAdapter()
        ingredientsAdapter!!.setData(recipeEditViewModel.recipe.value!!.ingredients)
        cookingStepsAdapter!!.setData(recipeEditViewModel.recipe.value!!.steps)
        recipeEditViewModel.setSelectedCookingStep(null)
    }

    private fun initListeners() {
        binding.buttonAddIngredientRecipeCreate2Stage.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_ingredient_from_second_stage)
        }

        binding.cardviewAddCookingStep.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_step_from_second_stage)
        }

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

    private fun initObservers() {
        imageViewModel.imagesToUploadAmount.observe(viewLifecycleOwner) {
            if (it == 0) {
                if (recipeEditViewModel.recipe.value!!.identifier != null) {
                    saveRecipe()
                }
            }
        }
        recipeEditViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.toolbarRecipeCreate2Stage.visibility = View.GONE
                binding.loader.progressOverlay.visibility = View.VISIBLE
            } else {
                binding.toolbarRecipeCreate2Stage.visibility = View.VISIBLE
                binding.loader.progressOverlay.visibility = View.GONE
            }
        }
    }

    private fun checkEnableMenu() {
        val isEmpty =
            recipeEditViewModel.recipe.value!!.ingredients.isNullOrEmpty() || recipeEditViewModel.recipe.value!!.steps.isNullOrEmpty()
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
                checkRecipe()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkRecipe() {
        if (recipeEditViewModel.recipeImage.value == null && !recipeEditViewModel.recipe.value!!.isPrivate!!) {
            FBAlertDialogBuilder.getDialogWithPositiveAndNegativeButtons(
                title = "Notice",
                msg = "Recipe without an image will be visible only for you. Would you still want to create new recipe?",
                positiveBtnListener = object : FBAlertDialogListener {
                    override fun onClick(dialogInterface: DialogInterface) {
                        recipeEditViewModel.recipe.value!!.isPrivate = true
                        dialogInterface.dismiss()
                        fillRecipe()
                        uploadImages()
                    }
                },
                positiveBtnText = "Yes",
                negativeBtnText = "No"
            ).show()
        } else {
            fillRecipe()
            uploadImages()
        }
    }

    private fun uploadImages() {
        imageViewModel.setImagesToUploadAmount(recipeEditViewModel.recipeStepImages.value!!.size)
        imageViewModel.uploadRecipeImages(
            recipeEditViewModel.recipeStepImages.value!!,
            recipeEditViewModel.recipe.value!!.identifier!!.toString(),
            object : ResultCallback<Pair<Int, String>> {
                override fun onResult(value: Pair<Int, String>?) {
                    value?.let {
                        if (it.first == 0) {
                            recipeEditViewModel.recipe.value!!.image =
                                "${CDN_FB_BASE_URL}r/${recipeEditViewModel.recipe.value!!.identifier}/${it.second}"
                        } else {
                            val find =
                                recipeEditViewModel.recipe.value!!.steps!!.find { cookingStep -> cookingStep.stepNumber == it.first }
                            find!!.image =
                                "${CDN_FB_BASE_URL}r/${recipeEditViewModel.recipe.value!!.identifier}/${it.second}"
                        }
                        imageViewModel.setImagesToUploadAmount(imageViewModel.imagesToUploadAmount.value!! - 1)
                    }
                }

                override fun onFailure(value: Pair<Int, String>?) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.title_error_images_upload),
                        Toast.LENGTH_LONG
                    ).show()
                    imageViewModel.setImagesToUploadAmount(imageViewModel.imagesToUploadAmount.value!! - 1)
                }
            })
    }

    private fun fillRecipe() {
        recipeEditViewModel.setIsLoading(true)
        recipeEditViewModel.recipe.value!!.identifier = FormatUtils.generateRecipeId()
        recipeEditViewModel.recipe.value!!.createDate = FormatUtils.dateFormat.format(Date())
        with(recipeEditViewModel.recipe.value!!) {
            kcalPerPortion = (kcalPerPortion!! * 100.0).roundToInt() / 100.0
            fatsPerPortion = (fatsPerPortion!! * 100.0).roundToInt() / 100.0
            carbsPerPortion = (carbsPerPortion!! * 100.0).roundToInt() / 100.0
            proteinsPerPortion = (proteinsPerPortion!! * 100.0).roundToInt() / 100.0
        }
        recipeEditViewModel.setRecipeStepImages(HashMap())
        recipeEditViewModel.recipeImage.value?.let {
            recipeEditViewModel.recipeStepImages.value!![0] = Pair(
                getRecipeImageName(0) + it.name.substring(it.name.indexOf('.')),
                it.asRequestBody(Constants.MEDIA_TYPE_OCTET_STREAM)
            )
        }
        recipeEditViewModel.recipe.value!!.steps!!.forEach { step ->
            step.imageFile?.let {
                recipeEditViewModel.recipeStepImages.value!![step.stepNumber!!] =
                    Pair(step.imageName!!, it.asRequestBody(Constants.MEDIA_TYPE_OCTET_STREAM))
            }
        }
        Log.d(
            TAG,
            "loadImages: ${recipeEditViewModel.recipeStepImages.value!!.size} | ${recipeEditViewModel.recipeStepImages.value}"
        )
    }

    private fun saveRecipe() {
        recipeEditViewModel.recipeCreate(
            recipeEditViewModel.recipe.value!!, object : ResultCallback<Boolean> {
                override fun onResult(value: Boolean?) {
                    Toast.makeText(requireContext(), "Recipe created!", Toast.LENGTH_SHORT).show()
                    recipeEditViewModel.setIsRecipeEditFinishedCreated(true)
                    recipeEditViewModel.setIsLoading(false)
                    popBackStack()
                }

                override fun onFailure(value: Boolean?) {
                    Toast.makeText(requireContext(), "Error!", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun checkIngredientsQty(currentIngredientsQty: Int) {
        if (currentIngredientsQty == maxIngredientsQty) {
            binding.buttonAddIngredientRecipeCreate2Stage.visibility = View.GONE
        }
        if (currentIngredientsQty < maxIngredientsQty) {
            binding.buttonAddIngredientRecipeCreate2Stage.visibility = View.VISIBLE
        }
        binding.textviewIngredientCountRecipeCreate2Stage.text =
            String.format(
                getString(R.string.format_count),
                currentIngredientsQty,
                maxIngredientsQty
            )
    }

    private fun checkStepsQty(currentStepsQty: Int) {
        if (currentStepsQty == maxStepsQty) {
            binding.cardviewAddCookingStep.visibility = View.GONE
        }
        if (currentStepsQty < maxStepsQty) {
            binding.cardviewAddCookingStep.visibility = View.VISIBLE
        }
        binding.textviewStepsCountRecipeCreate2Stage.text =
            String.format(
                getString(R.string.format_count),
                currentStepsQty,
                maxStepsQty
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
        recipeEditViewModel.recipe.value!!.ingredients!!.removeAt(selectedItem)
        ingredientsAdapter!!.notifyItemRemoved(selectedItem)
        drawNutritionFacts()
        checkIngredientsQty(recipeEditViewModel.recipe.value!!.ingredients!!.size)
        checkEnableMenu()
    }

    private fun setupCookingStepsAdapter() {
        val rv = binding.rvStepsRecipeCreate2Stage
        cookingStepsAdapter = CookingStepAdapter(requireContext())
        cookingStepsAdapter!!.setClickListener(this)
        rv.adapter = cookingStepsAdapter
    }

    override fun onCookingStepClick(value: CookingStep, itemPosition: Int) {
        recipeEditViewModel.setSelectedCookingStep(value)
        recipeEditViewModel.setSelectedCookingStepPosition(itemPosition)
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_go_to_step_from_second_stage)
    }

    //TODO ANIM
    override fun onRecipeCookingStepDelete(itemPosition: Int) {
        TransitionManager.go(
            android.transition.Scene(binding.rvStepsRecipeCreate2Stage),
            android.transition.Fade()
        )
        var step = itemPosition + 1
        recipeEditViewModel.recipe.value!!.steps!!.removeAt(itemPosition)
        recipeEditViewModel.recipeStepImages.value!!.remove(itemPosition)
        recipeEditViewModel.recipe.value!!.steps!!.forEach { cookingStep ->
            if (cookingStep.stepNumber!! >= step) {
                cookingStep.stepNumber = step
                step++
            }
        }
        Log.d(TAG, "onRecipeCookingStepDelete: ${recipeEditViewModel.recipe.value!!.steps}")
        cookingStepsAdapter!!.notifyItemRemoved(itemPosition)
        checkStepsQty(recipeEditViewModel.recipe.value!!.steps!!.size)
        checkEnableMenu()
        Log.d(TAG, "${recipeEditViewModel.recipe.value!!.steps}")
        Log.d(TAG, "${recipeEditViewModel.recipeStepImages.value}")
    }

    override fun onResume() {
        super.onResume()
        drawNutritionFacts()
    }

    private fun drawNutritionFacts() {
        if (recipeEditViewModel.recipe.value!!.isAllIngredientUnitsValid
            && !recipeEditViewModel.recipe.value!!.ingredients.isNullOrEmpty()
        ) {
            showNutritionFacts(true)
            binding.textviewPortionKcalsQtyRecipeCreate2Stage.text =
                FormatUtils.prettyCount(
                    recipeEditViewModel.recipe.value?.kcalPerPortion.toString().toDouble()
                )
            binding.tvQtyProteins.text = FormatUtils.prettyCount(
                recipeEditViewModel.recipe.value?.proteinsPerPortion.toString().toDouble()
            )
            binding.tvQtyFats.text = FormatUtils.prettyCount(
                recipeEditViewModel.recipe.value?.fatsPerPortion.toString().toDouble()
            )
            binding.tvQtyCarbs.text = FormatUtils.prettyCount(
                recipeEditViewModel.recipe.value?.carbsPerPortion.toString().toDouble()
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

    private fun popBackStack() {
        findNavController().popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}