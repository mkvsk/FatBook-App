package online.fatbook.fatbookapp.ui.fragment.recipe_create

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.transition.AutoTransition
import androidx.transition.Scene
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_recipe_create_cooking_step.*
import kotlinx.android.synthetic.main.fragment_recipe_create_first_stage.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.CookingStep
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateCookingStepBinding
import online.fatbook.fatbookapp.ui.adapters.CookingStepAdapter
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.util.FileUtils
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import java.io.File

class RecipeCreateCookingStepFragment : Fragment() {

    private var binding: FragmentRecipeCreateCookingStepBinding? = null
    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private var adapter: CookingStepAdapter? = null
    private var cookingStep: CookingStep? = CookingStep()
    private var selectedCookingStep: CookingStep? = null

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

        setupImageEditButtons()

        if (recipeViewModel.selectedCookingStep.value != null) {
            selectedCookingStep = recipeViewModel.selectedCookingStep.value
            edittext_recipe_create_cooking_step.setText(selectedCookingStep!!.description.toString())
        }

        edittext_recipe_create_cooking_step.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
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
                button_add_recipe_create_cooking_step.isEnabled =
                    !edittext_recipe_create_cooking_step.text.isNullOrEmpty()
            }
        })

        button_add_recipe_create_cooking_step.setOnClickListener {
            val description =
                edittext_recipe_create_cooking_step.text.toString().replace("\\s+".toRegex(), " ")
            if (selectedCookingStep != null) {
                selectedCookingStep!!.description = description

                adapter?.notifyItemChanged(recipeViewModel.selectedCookingStepPosition.value!!)
            } else {
                var cookingStepsAmount = recipeViewModel.newRecipe.value!!.steps!!.size

                cookingStep!!.description = description
                cookingStep!!.stepNumber = ++cookingStepsAmount

                recipeViewModel.newRecipe.value!!.steps!!.add(cookingStep!!)
                adapter?.notifyDataSetChanged()
            }
            recipeViewModel.selectedCookingStep.value = null
            recipeViewModel.selectedCookingStepPosition.value = null
            NavHostFragment.findNavController(this).popBackStack()
        }

    }

    private fun setupImageEditButtons() {
        button_add_photo_recipe_create_cooking_step.setOnClickListener {
            chooseImageFromGallery!!.launch("image/*")
        }
        button_delete_photo_recipe_create_cooking_step.setOnClickListener {
            Glide.with(requireContext())
                .load(R.drawable.ic_default_recipe_image)
                .into(imageview_full_image)
        }
        try {
            chooseImageFromGallery =
                registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                    if (verifyStoragePermissions(requireActivity())) {
                        val path = FileUtils.getPath(requireContext(), uri!!)
                        image = path?.let { File(it) }
                        Glide.with(requireContext())
                            .load(uri)
                            .into(imageview_full_image)
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
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
}