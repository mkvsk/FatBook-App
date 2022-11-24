package online.fatbook.fatbookapp.ui.fragment.recipe

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
import android.widget.TimePicker
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_recipe_first_stage.*
import kotlinx.android.synthetic.main.include_progress_overlay.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingDifficulty
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.databinding.FragmentRecipeFirstStageBinding
import online.fatbook.fatbookapp.ui.activity.MainActivity
import online.fatbook.fatbookapp.ui.adapters.RecipeCookingDifficultyAdapter
import online.fatbook.fatbookapp.ui.listeners.BaseFragmentActions
import online.fatbook.fatbookapp.ui.listeners.OnRecipeDifficultyClickListener
import online.fatbook.fatbookapp.ui.viewmodel.ImageViewModel
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.util.*
import online.fatbook.fatbookapp.util.alert_dialog.FBAlertDialogBuilder
import online.fatbook.fatbookapp.util.alert_dialog.FBAlertDialogListener
import java.io.File
import java.time.LocalTime

class RecipeFirstStageFragment : Fragment(), OnRecipeDifficultyClickListener, BaseFragmentActions {

    private var binding: FragmentRecipeFirstStageBinding? = null
    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }
    private val imageViewModel by lazy { obtainViewModel(ImageViewModel::class.java) }
    private var adapter: RecipeCookingDifficultyAdapter? = null

    private var chooseImageFromGallery: ActivityResultLauncher<String>? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeFirstStageBinding.inflate(inflater, container, false)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility =
                View.VISIBLE
        return binding!!.root
    }

    //TODO remove all drawing logic after static data loaded
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("===t=======RecipeCreateFirstStageFragment==========", "onViewCreated")
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
//        progress_overlay.visibility = View.VISIBLE
//        toolbar_recipe_1_stage.visibility = View.GONE
//        toolbar_recipe_create_1_stage.title = resources.getString(R.string.nav_recipe_create)
        toolbar_recipe_1_stage.title = "TODO title"
        if (recipeViewModel.newRecipe.value == null) {
            recipeViewModel.newRecipe.value = Recipe()
        }
        if (recipeViewModel.newRecipeStepImages.value == null) {
            recipeViewModel.newRecipeStepImages.value = HashMap()
        }
        setupAdapter()
        setupMenu()
        setupImageEditButtons()

        if (staticDataViewModel.cookingDifficulties.value.isNullOrEmpty()) {
            loadDifficulty()
        } else {
            adapter?.setData(staticDataViewModel.cookingDifficulties.value)
            adapter?.selectedDifficulty = staticDataViewModel.cookingDifficulties.value!![0]
            if (recipeViewModel.newRecipe.value!!.difficulty == null) {
                recipeViewModel.newRecipe.value!!.difficulty =
                        staticDataViewModel.cookingDifficulties.value!![0]
            }
        }

        if (recipeViewModel.newRecipeImage.value != null) {
            toggleImageButtons(true)
            Glide.with(requireContext())
                    .load(recipeViewModel.newRecipeImage.value)
                    .placeholder(Utils.getCircularProgressDrawable())
                    .into(imageview_photo_recipe_1_stage)
        } else {
            toggleImageButtons(false)
        }

        edittext_title_recipe_1_stage.setText(recipeViewModel.newRecipe.value!!.title)
        edittext_title_recipe_1_stage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkDataTitle()
                if (!s.isNullOrEmpty()) {
                    recipeViewModel.newRecipe.value!!.title =
                            s.toString().replace("\\s+".toRegex(), " ")
                                    .trim()
                } else {
                    recipeViewModel.newRecipe.value!!.title = null
                }
                checkEnableMenu()
                Log.d("NEWRECIPE", "title = ${recipeViewModel.newRecipe.value!!.title}")
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        recipeViewModel.newRecipe.value!!.difficulty?.let { difficulty ->
            adapter!!.selectedDifficulty =
                    staticDataViewModel.cookingDifficulties.value!!.find { it.title == difficulty.title }
        }

        edittext_portions_qtt_recipe_1_stage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkDataPortions()
                if (!s.isNullOrEmpty()) {
                    recipeViewModel.newRecipe.value!!.portions = s.toString().toInt()
                    if (edittext_portions_qtt_recipe_1_stage.length() > 1) {
                        hideKeyboard()
                    }
                } else {
                    recipeViewModel.newRecipe.value!!.portions = null
                }
                checkEnableMenu()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        showCookingTime()
        textview_set_time_recipe_1_stage.setOnClickListener {
            showTimePickerDialog()
        }

        textview_cooking_method_recipe_1_stage.setOnClickListener {
            staticDataViewModel.loadCookingMethod.value = true
            navigation(false)
        }

        if (recipeViewModel.newRecipe.value!!.cookingMethod != null) {
            textview_cooking_method_recipe_1_stage.setTextColor(
                    ContextCompat.getColor(
                            requireContext(),
                            R.color.main_text
                    )
            )
            textview_cooking_method_recipe_1_stage.text =
                    recipeViewModel.newRecipe.value!!.cookingMethod!!.title
        } else {
            textview_cooking_method_recipe_1_stage.setTextColor(
                    ContextCompat.getColor(
                            requireContext(),
                            R.color.hint_text
                    )
            )
            textview_cooking_method_recipe_1_stage.text =
                    getString(R.string.hint_choose_method)
        }

        textview_category_recipe_1_stage.setOnClickListener {
            staticDataViewModel.loadCookingMethod.value = false
            navigation(false)
        }

        if (!recipeViewModel.newRecipe.value!!.cookingCategories.isNullOrEmpty()) {
            textview_category_recipe_1_stage.setTextColor(
                    ContextCompat.getColor(
                            requireContext(),
                            R.color.main_text
                    )
            )
            textview_category_recipe_1_stage.text =
                    recipeViewModel.newRecipe.value!!.cookingCategories!!.joinToString { "${it.title}" }
        } else {
            textview_category_recipe_1_stage.setTextColor(
                    ContextCompat.getColor(
                            requireContext(),
                            R.color.hint_text
                    )
            )
            textview_category_recipe_1_stage.text =
                    getString(R.string.hint_choose_category)
        }

        recipeViewModel.newRecipe.value!!.isPrivate?.let {
            switch_private_recipe_recipe_1_stage.isChecked = it
            if (it) {
                textview_description_private_recipe_1_stage.text =
                        getString(R.string.title_recipe_private)
            } else {
                textview_description_private_recipe_1_stage.text =
                        getString(R.string.title_recipe_public)
            }
        }
        switch_private_recipe_recipe_1_stage.setOnCheckedChangeListener { _, isChecked ->
            recipeViewModel.newRecipe.value!!.isPrivate = isChecked
            if (isChecked) {
                textview_description_private_recipe_1_stage.text =
                        getString(R.string.title_recipe_private)
            } else {
                textview_description_private_recipe_1_stage.text =
                        getString(R.string.title_recipe_public)
            }
        }

        //TODO remove stub
        edittext_title_recipe_1_stage.setText("Test recipe name, REMOVE ME LATER")
        edittext_portions_qtt_recipe_1_stage.setText("1")
        Log.d(TAG, "=======================================================================")
        Log.d(TAG, "onViewCreated: ${recipeViewModel.newRecipe.value}")
        Log.d(TAG, "=======================================================================")
    }

    private fun checkDataTitle() {
        if (edittext_title_recipe_1_stage.text.toString().isNotEmpty()) {
            edittext_title_recipe_1_stage.background =
                    ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.round_corner_edittext_recipe_create
                    )
        } else {
            edittext_title_recipe_1_stage.isFocusable
            edittext_title_recipe_1_stage.background =
                    ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.round_corner_edittext_recipe_create_stroke
                    )
        }
    }

    private fun checkDataPortions() {
        if (edittext_portions_qtt_recipe_1_stage.text.toString().isNotEmpty()
                && edittext_portions_qtt_recipe_1_stage.text.toString()
                        .toInt() != 0
        ) {
            edittext_portions_qtt_recipe_1_stage.background =
                    ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.round_corner_edittext_recipe_create
                    )
        } else {
            edittext_portions_qtt_recipe_1_stage.isFocusable
            edittext_portions_qtt_recipe_1_stage.background =
                    ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.round_corner_edittext_recipe_create_stroke
                    )
        }
    }

    private fun checkEnableMenu() {
        val isEmpty =
                edittext_portions_qtt_recipe_1_stage.text.isNullOrEmpty() ||
                        edittext_title_recipe_1_stage.text.isNullOrEmpty() ||
                        edittext_portions_qtt_recipe_1_stage.text.toString().toInt() == 0
        toolbar_recipe_1_stage.menu.findItem(R.id.menu_create_first_stage_next).isVisible =
                !isEmpty
    }

    private fun toggleImageButtons(isImageExists: Boolean) {
        if (isImageExists) {
            imageview_photo_recipe_1_stage.isClickable = true
            button_edit_photo_recipe_1_stage.setImageResource(R.drawable.ic_btn_edit)
            button_edit_photo_recipe_1_stage.visibility = View.VISIBLE
            button_delete_photo_recipe_1_stage.visibility = View.VISIBLE
        } else {
            imageview_photo_recipe_1_stage.isClickable = false
            button_edit_photo_recipe_1_stage.setImageResource(R.drawable.ic_btn_add)
            button_edit_photo_recipe_1_stage.visibility = View.VISIBLE
            button_delete_photo_recipe_1_stage.visibility = View.GONE
        }
    }

    private fun setupMenu() {
        toolbar_recipe_1_stage.inflateMenu(R.menu.recipe_create_1_stage_menu)
        toolbar_recipe_1_stage.setOnMenuItemClickListener(this::onOptionsItemSelected)
        toolbar_recipe_1_stage.setNavigationOnClickListener {
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
        FBAlertDialogBuilder.getDialogWithPositiveAndNegativeButtons(
                getString(R.string.dialog_clear_form_title),
                getString(R.string.dialog_clear_form_msg),
                object : FBAlertDialogListener{
                    override fun onClick(dialogInterface: DialogInterface) {
                        clearForm()
                        dialogInterface.dismiss()
                    }
                },
                object : FBAlertDialogListener{
                    override fun onClick(dialogInterface: DialogInterface) {
                        dialogInterface.dismiss()
                    }
                }).show()
    }

    private fun clearForm() {
        recipeViewModel.newRecipe.value = Recipe()
        recipeViewModel.newRecipeImage.value = null
        recipeViewModel.newRecipeCookingMethod.value = null
        recipeViewModel.newRecipeCookingCategories.value = null
        recipeViewModel.newRecipeCookingTimeHours.value = null
        recipeViewModel.newRecipeCookingTimeMinutes.value = null

        recipeViewModel.newRecipeSteps.value = null
        recipeViewModel.newRecipeIngredients.value = null
        recipeViewModel.newRecipeAddIngredient.value = null
        recipeViewModel.newRecipeAddRecipeIngredient.value = null
        recipeViewModel.newRecipeStepImages.value = null
        recipeViewModel.selectedCookingStep.value = null
        recipeViewModel.selectedCookingStepPosition.value = null
        (requireActivity() as MainActivity).redrawFragment(2)
    }

    private fun setupImageEditButtons() {
        imageview_photo_recipe_1_stage.setOnClickListener {
            imageViewModel.image.value = imageview_photo_recipe_1_stage.drawable
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_image_view_from_first_stage)
        }
        button_edit_photo_recipe_1_stage.setOnClickListener {
            if (verifyStoragePermissions(requireActivity())) {
                chooseImageFromGallery!!.launch("image/*")
            }
        }
        button_delete_photo_recipe_1_stage.setOnClickListener {
            recipeViewModel.newRecipeImage.value = null
            toggleImageButtons(false)
            Glide.with(requireContext()).load(R.drawable.default_recipe_image_recipe_create_second_stage)
                    .into(imageview_photo_recipe_1_stage)
        }
        chooseImageFromGallery =
                registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                    if (verifyStoragePermissions(requireActivity())) {
                        uri?.let {
                            toggleImageButtons(true)
                            val path = FileUtils.getPath(requireContext(), it)
                            recipeViewModel.newRecipeImage.value = path?.let { file -> File(file) }
                            Glide.with(requireContext()).load(uri)
                                    .into(imageview_photo_recipe_1_stage)
                        }
                    }
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
                if (recipeViewModel.newRecipe.value!!.difficulty == null) {
                    recipeViewModel.newRecipe.value!!.difficulty = value[0]
                }
                toolbar_recipe_1_stage.visibility = View.VISIBLE
                progress_overlay.visibility = View.GONE
            }

            override fun onFailure(value: List<CookingDifficulty>?) {
                loadDifficulty()
            }
        })
    }

    private fun setupAdapter() {
        val rv = rv_select_difficulty_recipe_1_stage
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
        if (recipeViewModel.newRecipe.value!!.cookingTime.isNullOrEmpty()) {
            recipeViewModel.newRecipe.value!!.cookingTime = "00:15"
            recipeViewModel.newRecipeCookingTimeHours.value = 0
            recipeViewModel.newRecipeCookingTimeMinutes.value = 15
        }
        if (recipeViewModel.newRecipe.value!!.cookingMethod == null) {
            recipeViewModel.newRecipe.value!!.cookingMethod = staticDataViewModel.getOtherMethod()
        }
        if (recipeViewModel.newRecipe.value!!.cookingCategories.isNullOrEmpty()) {
            recipeViewModel.newRecipe.value!!.cookingCategories =
                    staticDataViewModel.getOtherCategory()
        }
        Log.d("NEW RECIPE:", "${recipeViewModel.newRecipe.value}")
    }

    private fun navigation(nextStep: Boolean) {
        if (nextStep) {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_second_stage_from_first_stage)
        } else {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_method_category_from_first_stage)
        }
    }

    private fun showTimePickerDialog() {
        val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = resources.getDimensionPixelSize(R.dimen.cards_margin_start)
        params.rightMargin = resources.getDimensionPixelSize(R.dimen.cards_margin_end)

        val dialogView = layoutInflater.inflate(R.layout.dialog_timepicker, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView)
                .setPositiveButton(resources.getString(R.string.alert_dialog_btn_ok)) { dialogInterface: DialogInterface, _: Int ->
                    val dialog =
                            dialogView.findViewById<TimePicker>(R.id.timepicker_dialog_cooking_time)
                    val cookingTime: LocalTime = if (dialog.hour == 0 && dialog.minute == 0) {
                        LocalTime.of(0, 15)
                    } else {
                        LocalTime.of(dialog.hour, dialog.minute)
                    }
                    recipeViewModel.newRecipeCookingTimeHours.value = cookingTime.hour
                    recipeViewModel.newRecipeCookingTimeMinutes.value = cookingTime.minute
                    recipeViewModel.newRecipe.value!!.cookingTime = cookingTime.toString()
                    showCookingTime()
                    dialogInterface.dismiss()
                    Log.d(TAG, "cooking time set to ${cookingTime.hour} h ${cookingTime.minute} min")
                }
                .setNegativeButton(resources.getString(R.string.alert_dialog_btn_cancel)) { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
                .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()
        val picker = dialog.findViewById<TimePicker>(R.id.timepicker_dialog_cooking_time)
        picker.setIs24HourView(true)
        picker.hour =
                if (recipeViewModel.newRecipeCookingTimeHours.value == null || recipeViewModel.newRecipeCookingTimeHours.value == 0) {
                    0
                } else {
                    recipeViewModel.newRecipeCookingTimeHours.value!!
                }
        picker.minute =
                if (recipeViewModel.newRecipeCookingTimeMinutes.value == null || recipeViewModel.newRecipeCookingTimeMinutes.value == 0) {
                    0
                } else {
                    recipeViewModel.newRecipeCookingTimeMinutes.value!!
                }
    }

    private fun showCookingTime() {
        if (recipeViewModel.newRecipe.value!!.cookingTime.isNullOrEmpty()) {
            textview_set_time_recipe_1_stage.setTextColor(
                    ContextCompat.getColor(
                            requireContext(),
                            R.color.hint_text
                    )
            )
        } else {
            RecipeUtils.prettyCookingTime(recipeViewModel.newRecipe.value!!.cookingTime)?.let {
                textview_set_time_recipe_1_stage.setTextColor(
                        ContextCompat.getColor(
                                requireContext(),
                                R.color.main_text
                        )
                )
                textview_set_time_recipe_1_stage.text = it
            }
        }
    }

    override fun onRecipeDifficultyClick(
            previousItem: Int, selectedItem: Int, difficulty: CookingDifficulty?
    ) {
        adapter!!.selectedDifficulty =
                staticDataViewModel.cookingDifficulties.value!!.find { it.title == difficulty!!.title }
        adapter!!.notifyItemChanged(previousItem)
        adapter!!.notifyItemChanged(selectedItem)
        recipeViewModel.newRecipe.value!!.difficulty = difficulty
    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        private const val TAG = "RecipeCreateFirstStageFragment"
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("===t=======RecipeCreateFirstStageFragment==========", "onDestroy")
    }

    override fun onBackPressedBase(): Boolean {
        return false
    }

    override fun scrollUpBase() {
        nsv_recipe_1_stage.scrollTo(0, 0)
        appBarLayout_recipe_1_stage.setExpanded(true, false)
    }
}
