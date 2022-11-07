package online.fatbook.fatbookapp.ui.fragment.recipe_create

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.transition.AutoTransition
import androidx.transition.Scene
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_recipe_create_add_ingredients.*
import kotlinx.android.synthetic.main.fragment_recipe_create_cooking_step.*
import kotlinx.android.synthetic.main.fragment_recipe_create_first_stage.*
import kotlinx.android.synthetic.main.fragment_recipe_create_second_stage.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.CookingStep
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateCookingStepBinding
import online.fatbook.fatbookapp.ui.adapters.CookingStepAdapter
import online.fatbook.fatbookapp.ui.viewmodel.ImageViewModel
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.util.FileUtils
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import java.io.File

class RecipeCreateCookingStepFragment : Fragment() {

    private var binding: FragmentRecipeCreateCookingStepBinding? = null
    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private val imageViewModel by lazy { obtainViewModel(ImageViewModel::class.java) }
    private var cookingStep: CookingStep? = CookingStep()
    private var selectedCookingStep: CookingStep? = null

    private var currentCookingStep: CookingStep? = null
    private var currentStep: Int = 1

    private var descriptionTextLength: Int = 0

    private var chooseImageFromGallery: ActivityResultLauncher<String>? = null
    private var image: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeCreateCookingStepBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        toolbar_recipe_create_cooking_step.title = "TODO title"
        setupMenu()
        checkEnableMenu()
        setupImageEditButtons()

        if (recipeViewModel.selectedCookingStep.value != null) {
            selectedCookingStep = recipeViewModel.selectedCookingStep.value
            edittext_recipe_create_cooking_step.setText(selectedCookingStep!!.description.toString())
            checkEnableMenu()
        }

        edittext_recipe_create_cooking_step.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkData()
                descriptionTextLength =
                    edittext_recipe_create_cooking_step.filters.filterIsInstance<InputFilter.LengthFilter>()
                        .firstOrNull()?.max!!
                descriptionTextLength -= s.toString().length
                textview_length_recipe_create_cooking_step.text = descriptionTextLength.toString()

                TransitionManager.go(Scene(cardview_recipe_create_cooking_step), AutoTransition())

                if (descriptionTextLength == 0) {
                    hideKeyboard(edittext_recipe_create_cooking_step)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun checkData() {
        if (edittext_recipe_create_cooking_step.text.toString().isNotEmpty()) {
            edittext_recipe_create_cooking_step.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_corner_multiline_et
                )
            toolbar_recipe_create_cooking_step.menu.findItem(R.id.menu_create_step_confirm).isVisible =
                true
        } else {
            edittext_recipe_create_cooking_step.isFocusable
            edittext_recipe_create_cooking_step.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_corner_multiline_et_stroke
                )
            toolbar_recipe_create_cooking_step.menu.findItem(R.id.menu_create_step_confirm).isVisible =
                false
        }
    }

    private fun setupMenu() {
        toolbar_recipe_create_cooking_step.inflateMenu(R.menu.recipe_create_step_menu)
        toolbar_recipe_create_cooking_step.setOnMenuItemClickListener(this::onOptionsItemSelected)
        toolbar_recipe_create_cooking_step.setNavigationOnClickListener {
            popBackStack()
        }
    }

    private fun checkEnableMenu() {
        val isEmpty = edittext_recipe_create_cooking_step.text.isNullOrEmpty()
        toolbar_recipe_create_cooking_step.menu.findItem(R.id.menu_create_step_confirm).isVisible =
            !isEmpty
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_create_step_confirm -> {
                createStep()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createStep() {
        val description =
            edittext_recipe_create_cooking_step.text.toString()
                .replace("\\s+".toRegex(), " ")
        if (selectedCookingStep != null) {
            selectedCookingStep!!.description = description
        } else {
            var cookingStepsAmount = recipeViewModel.newRecipe.value!!.steps!!.size
            cookingStep!!.description = description
            cookingStep!!.stepNumber = ++cookingStepsAmount
            recipeViewModel.newRecipe.value!!.steps!!.add(cookingStep!!)
        }
        image?.let {
            recipeViewModel.newRecipeStepImages.value!!.put(
                cookingStep!!.stepNumber ?: selectedCookingStep!!.stepNumber!!, it
            )
        }
        recipeViewModel.selectedCookingStep.value = null
        recipeViewModel.selectedCookingStepPosition.value = null
        NavHostFragment.findNavController(this).popBackStack()
    }

    private fun setupImageEditButtons() {
        imageview_step.setOnClickListener {
            imageViewModel.image.value = imageview_step.drawable
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_image_view_from_step)
        }
        button_edit_photo_recipe_create_cooking_step.setOnClickListener {
            if (verifyStoragePermissions(requireActivity())) {
                chooseImageFromGallery!!.launch("image/*")
            }
        }
        button_delete_photo_recipe_create_cooking_step.setOnClickListener {
            image = null
            toggleImageButtons(false)
            Glide.with(requireContext())
                .load(R.drawable.ic_default_recipe_image)
                .into(imageview_step)
        }
        chooseImageFromGallery =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (verifyStoragePermissions(requireActivity())) {
                    uri?.let {
                        toggleImageButtons(true)
                        val path = FileUtils.getPath(requireContext(), uri)
                        image = path?.let { File(it) }
                        Glide.with(requireContext())
                            .load(uri)
                            .into(imageview_step)
                    }
                }
            }
    }

    private fun toggleImageButtons(isImageExists: Boolean) {
        if (isImageExists) {
            imageview_step.isClickable = true
            button_edit_photo_recipe_create_cooking_step.setImageResource(R.drawable.ic_btn_edit)
            button_edit_photo_recipe_create_cooking_step.visibility = View.VISIBLE
            button_delete_photo_recipe_create_cooking_step.visibility = View.VISIBLE
        } else {
            imageview_step.isClickable = false
            button_edit_photo_recipe_create_cooking_step.setImageResource(R.drawable.ic_btn_add)
            button_edit_photo_recipe_create_cooking_step.visibility = View.VISIBLE
            button_delete_photo_recipe_create_cooking_step.visibility = View.GONE
        }
    }

    private fun verifyStoragePermissions(requireActivity: FragmentActivity): Boolean {
        val permission = ActivityCompat.checkSelfPermission(
            requireActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE
            )
            false
        } else {
            true
        }
    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
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