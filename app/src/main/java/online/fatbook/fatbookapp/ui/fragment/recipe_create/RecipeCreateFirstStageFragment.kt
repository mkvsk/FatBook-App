package online.fatbook.fatbookapp.ui.fragment.recipe_create

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
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
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
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
import kotlinx.android.synthetic.main.fragment_recipe_create_first_stage.*
import kotlinx.android.synthetic.main.include_progress_overlay.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingDifficulty
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateFirstStageBinding
import online.fatbook.fatbookapp.ui.activity.MainActivity
import online.fatbook.fatbookapp.ui.adapters.RecipeCookingDifficultyAdapter
import online.fatbook.fatbookapp.ui.listeners.OnRecipeDifficultyClickListener
import online.fatbook.fatbookapp.ui.viewmodel.ImageViewModel
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.util.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min

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
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility =
            View.VISIBLE
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.WRAP_CONTENT)

        if (recipeViewModel.newRecipe.value == null) {
            recipeViewModel.newRecipe.value = Recipe()
        }

        setupAdapter()
        setupMenu()
        setupImageEditButtons()

        //TODO remove picture, true -> false
        toggleImageButtons(true)
        Glide.with(requireContext()).load("https://fatbook.b-cdn.net/root/alarm.jpg")
            .into(imageview_photo_recipe_create_1_stage)

        if (staticDataViewModel.cookingDifficulties.value.isNullOrEmpty()) {
            progress_overlay.visibility = View.VISIBLE
            loadDifficulty()
        } else {
            adapter?.setData(staticDataViewModel.cookingDifficulties.value)
            adapter?.selectedDifficulty = staticDataViewModel.cookingDifficulties.value!![0]
            if (recipeViewModel.newRecipe.value!!.difficulty == null) {
                recipeViewModel.newRecipe.value!!.difficulty =
                    staticDataViewModel.cookingDifficulties.value!![0]
            }
        }

        edittext_title_recipe_create_1_stage.setText(recipeViewModel.newRecipe.value!!.title)
        edittext_title_recipe_create_1_stage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    recipeViewModel.newRecipe.value!!.title =
                        s.toString().replace("\\s+".toRegex(), " ")
                            .trim()
                    toolbar_recipe_create_1_stage.title = recipeViewModel.newRecipe.value!!.title
                    toolbar_recipe_create_1_stage.menu.findItem(R.id.menu_create_first_stage_next).isVisible =
                        true
                } else {
                    toolbar_recipe_create_1_stage.title =
                        resources.getString(R.string.nav_recipe_create)
                    toolbar_recipe_create_1_stage.menu.findItem(R.id.menu_create_first_stage_next).isVisible =
                        false
                }
                Log.d("NEWRECIPE", "title = ${recipeViewModel.newRecipe.value!!.title}")
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        recipeViewModel.newRecipe.value!!.difficulty?.let { difficulty ->
            adapter!!.selectedDifficulty =
                staticDataViewModel.cookingDifficulties.value!!.find { it.title == difficulty.title }
        }

        recipeViewModel.newRecipe.value!!.portions?.let {
            edittext_portions_qtt_recipe_create_1_stage.setText(it.toString())
        }
        edittext_portions_qtt_recipe_create_1_stage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    recipeViewModel.newRecipe.value!!.portions = s.toString().toInt()
                    if (edittext_portions_qtt_recipe_create_1_stage.length() > 1) {
                        hideKeyboard()
                    }
                } else {
                    recipeViewModel.newRecipe.value!!.portions = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        showCookingTime()
        textview_set_time_recipe_create_1_stage.setOnClickListener {
            showTimePickerDialog()
        }

        textview_cooking_method_recipe_create_1_stage.setOnClickListener {
            staticDataViewModel.loadCookingMethod.value = true
            navigation(false)
        }

        if (recipeViewModel.newRecipe.value!!.cookingMethod != null) {
            textview_cooking_method_recipe_create_1_stage.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.main_text
                )
            )
            textview_cooking_method_recipe_create_1_stage.text =
                recipeViewModel.newRecipe.value!!.cookingMethod!!.title
        } else {
            textview_cooking_method_recipe_create_1_stage.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.hint_text
                )
            )
            textview_cooking_method_recipe_create_1_stage.text =
                getString(R.string.hint_choose_method)
        }

        textview_category_recipe_create_1_stage.setOnClickListener {
            staticDataViewModel.loadCookingMethod.value = false
            navigation(false)
        }

        if (!recipeViewModel.newRecipe.value!!.cookingCategories.isNullOrEmpty()) {
            textview_category_recipe_create_1_stage.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.main_text
                )
            )
            textview_category_recipe_create_1_stage.text =
                recipeViewModel.newRecipe.value!!.cookingCategories!!.joinToString { "${it.title}" }
        } else {
            textview_category_recipe_create_1_stage.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.hint_text
                )
            )
            textview_category_recipe_create_1_stage.text =
                getString(R.string.hint_choose_category)
        }

        recipeViewModel.newRecipe.value!!.isPrivate?.let {
            switch_private_recipe_recipe_create_1_stage.isChecked = it
        }
        switch_private_recipe_recipe_create_1_stage.setOnCheckedChangeListener { _, isChecked ->
            recipeViewModel.newRecipe.value!!.isPrivate = isChecked
            if (isChecked) {
                textview_description_private_recipe_create_1_stage.text =
                    getString(R.string.title_recipe_private)
            } else {
                textview_description_private_recipe_create_1_stage.text =
                    getString(R.string.title_recipe_public)
            }
        }

        Log.d(TAG, "=======================================================================")
        Log.d(TAG, "onViewCreated: ${recipeViewModel.newRecipe.value}")
        Log.d(TAG, "=======================================================================")
    }

    private fun toggleImageButtons(isImageExists: Boolean) {
        if (isImageExists) {
            imageview_photo_recipe_create_1_stage.isClickable = true
            button_edit_photo_recipe_create_1_stage.setImageResource(R.drawable.ic_btn_edit)
            button_edit_photo_recipe_create_1_stage.visibility = View.VISIBLE
            button_delete_photo_recipe_create_1_stage.visibility = View.VISIBLE
        } else {
            imageview_photo_recipe_create_1_stage.isClickable = false
            button_edit_photo_recipe_create_1_stage.setImageResource(R.drawable.ic_btn_add)
            button_edit_photo_recipe_create_1_stage.visibility = View.VISIBLE
            button_delete_photo_recipe_create_1_stage.visibility = View.GONE
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
        val dialogBinding = layoutInflater.inflate(R.layout.alert_dialog_layout, null)
        val myDialog = Dialog(requireContext())
        val msg = "Are you sure you want to cancel creating new recipe?"
        val textViewMsg = TextView(requireContext())
        textViewMsg.text = msg
        myDialog.setContentView(dialogBinding)
        myDialog.setCancelable(true)
        myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.show()
        val positiveButton =
            dialogBinding.findViewById<Button>(R.id.button_clear_new_recipe_alert_dialog)
        val negativeButton = dialogBinding.findViewById<Button>(R.id.button_cancel_alert_dialog)
        positiveButton.setOnClickListener {
            clearForm()
            myDialog.dismiss()
        }
        negativeButton.setOnClickListener {
            myDialog.dismiss()
        }
    }

    private fun clearForm() {
        recipeViewModel.newRecipe.value = Recipe()
        recipeViewModel.newRecipeImage.value = null
        recipeViewModel.newRecipeCookingMethod.value = null
        recipeViewModel.newRecipeCookingCategories.value = null

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
        imageview_photo_recipe_create_1_stage.setOnClickListener {
            imageViewModel.image.value = imageview_photo_recipe_create_1_stage.drawable
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_image_view_from_first_stage)
        }
        button_edit_photo_recipe_create_1_stage.setOnClickListener {
            if (verifyStoragePermissions(requireActivity())) {
                chooseImageFromGallery!!.launch("image/*")
            }
        }
        button_delete_photo_recipe_create_1_stage.setOnClickListener {
            recipeViewModel.newRecipeImage.value = null
            toggleImageButtons(false)
            Glide.with(requireContext()).load(R.drawable.ic_default_recipe_image)
                .into(imageview_photo_recipe_create_1_stage)
        }
        try {
            chooseImageFromGallery =
                registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                    if (verifyStoragePermissions(requireActivity())) {
                        uri?.let {
                            toggleImageButtons(true)
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
                if (recipeViewModel.newRecipe.value!!.difficulty == null) {
                    recipeViewModel.newRecipe.value!!.difficulty = value[0]
                }
                progress_overlay.visibility = View.GONE
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

        val dialog = AlertDialog.Builder(requireContext()).setView(R.layout.dialog_timepicker)
            .setPositiveButton(resources.getString(R.string.alert_dialog_btn_ok)) { dialogInterface: DialogInterface, _: Int ->
                showCookingTime()
                dialogInterface.dismiss()
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
        picker.setOnTimeChangedListener { _, hourOfDay, minute ->
            val date: Date
            if (hourOfDay == 0 && minute == 0) {
                date = Date(RecipeUtils.defaultCookingTimeInMilliseconds)
                recipeViewModel.newRecipeCookingTimeHours.value = 0
                recipeViewModel.newRecipeCookingTimeMinutes.value = 15
            } else {
                date = Date((hourOfDay * 60L * 60L * 1000L) + (minute * 60L * 1000L))
                recipeViewModel.newRecipeCookingTimeHours.value = hourOfDay
                recipeViewModel.newRecipeCookingTimeMinutes.value = minute
            }
            recipeViewModel.newRecipe.value!!.cookingTime = RecipeUtils.timeFormat.format(date)
        }
    }

    private fun showCookingTime() {
        if (recipeViewModel.newRecipe.value!!.cookingTime.isNullOrEmpty()) {
            textview_set_time_recipe_create_1_stage.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.hint_text
                )
            )
        } else {
            RecipeUtils.prettyCookingTime(recipeViewModel.newRecipe.value!!.cookingTime)?.let {
                textview_set_time_recipe_create_1_stage.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.main_text
                    )
                )
                textview_set_time_recipe_create_1_stage.text = it
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

    fun scrollUp() {
        nsv_recipe_create_1_stage.scrollTo(0, 0)
        appBarLayout_recipe_create_1_stage.setExpanded(true, false)
    }
}
