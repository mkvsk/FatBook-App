package online.fatbook.fatbookapp.ui.fragment.recipe_create

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.android.synthetic.main.fragment_recipe_create_first_stage.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingDifficulty
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateFirstStageBinding
import online.fatbook.fatbookapp.ui.activity.MainActivity
import online.fatbook.fatbookapp.ui.adapters.RecipeCookingDifficultyAdapter
import online.fatbook.fatbookapp.ui.listeners.OnRecipeDifficultyClickListener
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.ui.viewmodel.ImageViewModel
import online.fatbook.fatbookapp.util.FileUtils
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import java.io.File


class RecipeCreateFirstStageFragment : Fragment(), OnRecipeDifficultyClickListener {

    private var binding: FragmentRecipeCreateFirstStageBinding? = null
    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }
    private val imageViewModel by lazy { obtainViewModel(ImageViewModel::class.java) }
    private var adapter: RecipeCookingDifficultyAdapter? = null

    private var chooseImageFromGallery: ActivityResultLauncher<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeCreateFirstStageBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupMenu()

        if (staticDataViewModel.cookingDifficulties.value.isNullOrEmpty()) {
            loadDifficulty()
        } else {
            adapter?.setData(staticDataViewModel.cookingDifficulties.value)
            adapter?.selectedDifficulty = staticDataViewModel.cookingDifficulties.value!![0]
        }
        setupImageEditButtons()
        setupObservers()

        //TODO fix
        if (recipeViewModel.newRecipe.value == null) {
            recipeViewModel.newRecipe.value = Recipe()
        }

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.WRAP_CONTENT)
        textview_set_time_recipe_create_1_stage.setOnClickListener {
            showTimePickerDialog()
        }

        textview_cooking_method_recipe_create_1_stage.setOnClickListener {
            staticDataViewModel.loadCookingMethod.value = true
            navigation(false)
        }

        textview_category_recipe_create_1_stage.setOnClickListener {
            staticDataViewModel.loadCookingMethod.value = false
            navigation(false)
        }

        if (recipeViewModel.newRecipe.value!!.cookingMethod != null) {
            textview_cooking_method_recipe_create_1_stage.text =
                recipeViewModel.newRecipe.value!!.cookingMethod!!.title
        }

        if (recipeViewModel.newRecipe.value!!.cookingCategories!!.isNotEmpty()) {
            textview_category_recipe_create_1_stage.text =
                recipeViewModel.newRecipe.value!!.cookingCategories!!.joinToString { "${it.title}" }
        }

        edittext_title_recipe_create_1_stage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    toolbar_recipe_create_1_stage.title = edittext_title_recipe_create_1_stage.text
                    toolbar_recipe_create_1_stage.menu.findItem(R.id.menu_create_first_stage_next).isVisible = true
                } else {
                    toolbar_recipe_create_1_stage.title = resources.getString(R.string.nav_recipe_create)
                    toolbar_recipe_create_1_stage.menu.findItem(R.id.menu_create_first_stage_next).isVisible = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        edittext_portions_qtt_recipe_create_1_stage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (edittext_portions_qtt_recipe_create_1_stage.length() > 1) {
                    hideKeyboard(edittext_portions_qtt_recipe_create_1_stage)
                }
            }
        })

        switch_private_recipe_recipe_create_1_stage.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                textview_description_private_recipe_create_1_stage.text = getString(R.string.title_recipe_private)
            } else {
                textview_description_private_recipe_create_1_stage.text = getString(R.string.title_recipe_public)
            }
        }
    }

    private fun setupMenu() {
        toolbar_recipe_create_1_stage.inflateMenu(R.menu.recipe_create_first_stage_menu)
        toolbar_recipe_create_1_stage.setOnMenuItemClickListener(this::onOptionsItemSelected)
        toolbar_recipe_create_1_stage.setNavigationOnClickListener {
            showClearFormDialog()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_create_first_stage_next -> {
                fillRecipe()
                navigation(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showClearFormDialog() {
        val msg = "Are you sure you want to cancel creating new recipe?"
        val textViewMsg = TextView(requireContext())
        textViewMsg.text = msg
        textViewMsg.setSingleLine()
        textViewMsg.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_text))
        val container = FrameLayout(requireContext())
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.rightMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.topMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        textViewMsg.layoutParams = params
        container.addView(textViewMsg)
        val title =
            LayoutInflater.from(requireContext())
                .inflate(R.layout.alert_dialog_clear_recipe_create_form, null)
        AlertDialog.Builder(requireContext())
            .setView(container)
            .setCustomTitle(title)
            .setPositiveButton(getString(R.string.alert_dialog_btn_yes)) { dialogInterface: DialogInterface, _: Int ->
                clearForm()
                dialogInterface.dismiss()
            }
            .setNegativeButton(getString(R.string.alert_dialog_btn_cancel)) { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
            .show()
    }

    private fun clearForm() {
        recipeViewModel.newRecipe.value = Recipe()
        recipeViewModel.newRecipeImage.value = null
        recipeViewModel.newRecipeSteps.value = null
        recipeViewModel.newRecipeIngredients.value = null
        recipeViewModel.newRecipeCookingMethod.value = null
        recipeViewModel.newRecipeCookingCategories.value = null
        recipeViewModel.newRecipeAddIngredient.value = null
        recipeViewModel.newRecipeAddRecipeIngredient.value = null
        recipeViewModel.newRecipeStepImages.value = null
        recipeViewModel.selectedCookingStep.value = null
        recipeViewModel.selectedCookingStepPosition.value = null
        (requireActivity() as MainActivity).redrawFragment(2)
    }

    private fun setupObservers() {
        imageview_photo_recipe_create_1_stage.isClickable = false
        recipeViewModel.newRecipeImage.observe(viewLifecycleOwner) {
            imageview_photo_recipe_create_1_stage.isClickable = it != null
        }
    }

    private fun setupImageEditButtons() {
        imageview_photo_recipe_create_1_stage.setOnClickListener {
            imageViewModel.image.value = imageview_photo_recipe_create_1_stage.drawable
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_image_view_from_first_stage)
        }
        button_add_photo_recipe_create_1_stage.setOnClickListener {
            if (verifyStoragePermissions(requireActivity())) {
                chooseImageFromGallery!!.launch("image/*")
            }
        }
        button_delete_photo_recipe_create_1_stage.setOnClickListener {
            recipeViewModel.newRecipeImage.value = null
            Glide.with(requireContext()).load(R.drawable.ic_default_recipe_image)
                .into(imageview_photo_recipe_create_1_stage)
        }
        try {
            chooseImageFromGallery =
                registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                    if (verifyStoragePermissions(requireActivity())) {
                        uri?.let {
                            val path = FileUtils.getPath(requireContext(), it)
                            recipeViewModel.newRecipeImage.value = path?.let { file -> File(file) }
                            Glide.with(requireContext()).load(uri)
                                .into(imageview_photo_recipe_create_1_stage)
                        }
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

    private fun loadDifficulty() {
        staticDataViewModel.getAllCookingDifficulties(object :
            ResultCallback<List<CookingDifficulty>> {
            override fun onResult(value: List<CookingDifficulty>?) {
                value as ArrayList
                staticDataViewModel.cookingDifficulties.value = value
                adapter?.setData(value)
                adapter?.selectedDifficulty = value[0]
            }

            override fun onFailure(value: List<CookingDifficulty>?) {
                loadDifficulty()
            }
        })
    }

    private fun setupAdapter() {
        val rv = rv_select_difficulty_recipe_create_1_stage
        rv.layoutManager = getLayoutManager()
        adapter = RecipeCookingDifficultyAdapter()
        adapter!!.setClickListener(this)
        rv.adapter = adapter
    }

    private fun getLayoutManager(): RecyclerView.LayoutManager {
        return FlexboxLayoutManager(context).apply {
            this.flexDirection = FlexDirection.ROW
            this.flexWrap = FlexWrap.WRAP
            this.justifyContent = JustifyContent.SPACE_BETWEEN
        }
    }

    private fun fillRecipe() {
        recipeViewModel.newRecipe.value!!.title =
            edittext_title_recipe_create_1_stage.text.toString().replace("\\s+".toRegex(), " ")
                .trim()
        edittext_title_recipe_create_1_stage.setText(recipeViewModel.newRecipe.value!!.title)

        recipeViewModel.newRecipe.value!!.portions =
            if (edittext_portions_qtt_recipe_create_1_stage.text.toString().isEmpty()) {
                1
            } else {
                edittext_portions_qtt_recipe_create_1_stage.text.toString().toInt()
            }

        recipeViewModel.newRecipe.value!!.cookingTime =
            textview_set_time_recipe_create_1_stage.text.toString()

        if (switch_private_recipe_recipe_create_1_stage.isChecked) {
            recipeViewModel.newRecipe.value!!.isPrivate
        } else {
            recipeViewModel.newRecipe.value!!.isPrivate = false
        }

        Log.d("NEW RECIPE:", "${recipeViewModel.newRecipe.value}")
    }

    private fun navigation(nextStep: Boolean) {
        if (nextStep) {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_recipe_create_second_stage_from_first_stage)
        } else {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_recipe_choose_method_or_category_from_first_stage)
        }
    }

    private fun showTimePickerDialog() {
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = resources.getDimensionPixelSize(R.dimen.cards_margin_start)
        params.rightMargin = resources.getDimensionPixelSize(R.dimen.cards_margin_end)

        val dialog = AlertDialog.Builder(requireContext()).setView(R.layout.dialog_timepicker)
            .setPositiveButton(resources.getString(R.string.alert_dialog_btn_ok), null)
            .setNegativeButton(resources.getString(R.string.alert_dialog_btn_cancel)) { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()
        val picker = dialog.findViewById<TimePicker>(R.id.timepicker_dialog_cooking_time)
        picker.setIs24HourView(true)

        picker.setOnTimeChangedListener { _, hourOfDay, minute ->
            if (hourOfDay == 0 && minute == 0) {
                textview_set_time_recipe_create_1_stage.text =
                    getString(R.string.default_cooking_time)
            }
            if (hourOfDay != 0 && minute != 0) {
                textview_set_time_recipe_create_1_stage.text =
                    String.format("%d h %d min", hourOfDay, minute)
            }
            if (hourOfDay != 0 && minute == 0) {
                textview_set_time_recipe_create_1_stage.text =
                    String.format("%d h", hourOfDay, minute)
            }
            if (hourOfDay == 0 && minute != 0) {
                textview_set_time_recipe_create_1_stage.text =
                    String.format("%d min", minute)
            }
        }
    }

    override fun onRecipeDifficultyClick(
        previousItem: Int,
        selectedItem: Int,
        difficulty: CookingDifficulty?
    ) {
        adapter!!.selectedDifficulty =
            staticDataViewModel.cookingDifficulties.value!!.find { it.title == difficulty!!.title }
        adapter!!.notifyItemChanged(previousItem)
        adapter!!.notifyItemChanged(selectedItem)
        recipeViewModel.newRecipe.value!!.difficulty = difficulty

        Log.d("DIFFICULTY", "${recipeViewModel.newRecipe.value!!.difficulty!!.title}")
    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    fun scrollUp() {
        nsv_recipe_create_1_stage.scrollTo(0, 0)
        appBarLayout_recipe_create_1_stage.setExpanded(true, false)
    }
}
