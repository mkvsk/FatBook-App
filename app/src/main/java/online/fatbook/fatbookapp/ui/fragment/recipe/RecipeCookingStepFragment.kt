package online.fatbook.fatbookapp.ui.fragment.recipe

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.transition.AutoTransition
import androidx.transition.Scene
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.CookingStep
import online.fatbook.fatbookapp.databinding.FragmentRecipeCookingStepBinding
import online.fatbook.fatbookapp.ui.viewmodel.ImageViewModel
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.util.FileUtils
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import java.io.File

class RecipeCookingStepFragment : Fragment() {

    private var _binding: FragmentRecipeCookingStepBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentRecipeCookingStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility =
                View.GONE
        binding.toolbarRecipeCreateCookingStep.title = "TODO title"
        setupMenu()
        checkEnableMenu()
        setupImageEditButtons()

        if (recipeViewModel.selectedCookingStep.value != null) {
            selectedCookingStep = recipeViewModel.selectedCookingStep.value
            binding.edittextRecipeCreateCookingStep.setText(selectedCookingStep!!.description.toString())
            checkEnableMenu()
        }

        binding.edittextRecipeCreateCookingStep.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkData()
                descriptionTextLength =
                    binding.edittextRecipeCreateCookingStep.filters.filterIsInstance<InputFilter.LengthFilter>()
                        .firstOrNull()?.max!!
                descriptionTextLength -= s.toString().length
                binding.textviewLengthRecipeCreateCookingStep.text =
                    descriptionTextLength.toString()

                TransitionManager.go(
                    Scene(binding.cardviewRecipeCreateCookingStep),
                    AutoTransition()
                )

                if (descriptionTextLength == 0) {
                    hideKeyboard(binding.edittextRecipeCreateCookingStep)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun checkData() {
        if (binding.edittextRecipeCreateCookingStep.text.toString().isNotEmpty()) {
            binding.edittextRecipeCreateCookingStep.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_corner_multiline_et
                )
            binding.toolbarRecipeCreateCookingStep.menu.findItem(R.id.menu_create_step_confirm).isVisible =
                true
        } else {
            binding.edittextRecipeCreateCookingStep.isFocusable
            binding.edittextRecipeCreateCookingStep.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_corner_multiline_et_stroke
                )
            binding.toolbarRecipeCreateCookingStep.menu.findItem(R.id.menu_create_step_confirm).isVisible =
                false
        }
    }

    private fun setupMenu() {
        binding.toolbarRecipeCreateCookingStep.inflateMenu(R.menu.recipe_create_step_menu)
        binding.toolbarRecipeCreateCookingStep.setOnMenuItemClickListener(this::onOptionsItemSelected)
        binding.toolbarRecipeCreateCookingStep.setNavigationOnClickListener {
            popBackStack()
        }
    }

    private fun checkEnableMenu() {
        val isEmpty = binding.edittextRecipeCreateCookingStep.text.isNullOrEmpty()
        binding.toolbarRecipeCreateCookingStep.menu.findItem(R.id.menu_create_step_confirm).isVisible =
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
            binding.edittextRecipeCreateCookingStep.text.toString()
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
        findNavController().popBackStack()
    }

    private fun setupImageEditButtons() {
        binding.imageviewStep.setOnClickListener {
            imageViewModel.setImage(binding.imageviewStep.drawable)
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_image_view_from_step)
        }
        binding.buttonEditPhotoRecipeCreateCookingStep.setOnClickListener {
            if (verifyStoragePermissions(requireActivity())) {
                chooseImageFromGallery!!.launch("image/*")
            }
        }
        binding.buttonDeletePhotoRecipeCreateCookingStep.setOnClickListener {
            image = null
            toggleImageButtons(false)
            Glide.with(requireContext())
                .load(R.drawable.default_recipe_image_recipe_create_second_stage)
                .into(binding.imageviewStep)
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
                            .into(binding.imageviewStep)
                    }
                }
            }
    }

    private fun toggleImageButtons(isImageExists: Boolean) {
        if (isImageExists) {
            binding.imageviewStep.isClickable = true
            binding.buttonEditPhotoRecipeCreateCookingStep.setImageResource(R.drawable.ic_btn_edit)
            binding.buttonEditPhotoRecipeCreateCookingStep.visibility = View.VISIBLE
            binding.buttonDeletePhotoRecipeCreateCookingStep.visibility = View.VISIBLE
        } else {
            binding.imageviewStep.isClickable = false
            binding.buttonEditPhotoRecipeCreateCookingStep.setImageResource(R.drawable.ic_btn_add)
            binding.buttonEditPhotoRecipeCreateCookingStep.visibility = View.VISIBLE
            binding.buttonDeletePhotoRecipeCreateCookingStep.visibility = View.GONE
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

    private fun popBackStack() {
        findNavController().popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility =
                View.VISIBLE
    }
}