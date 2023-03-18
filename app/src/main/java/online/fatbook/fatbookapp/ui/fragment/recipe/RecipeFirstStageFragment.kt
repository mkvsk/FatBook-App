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
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.Scene
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingDifficulty
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.databinding.FragmentRecipeFirstStageBinding
import online.fatbook.fatbookapp.ui.activity.MainActivity
import online.fatbook.fatbookapp.ui.adapters.RecipeCookingDifficultyAdapter
import online.fatbook.fatbookapp.ui.listeners.BaseFragmentActionsListener
import online.fatbook.fatbookapp.ui.listeners.OnRecipeDifficultyClickListener
import online.fatbook.fatbookapp.ui.viewmodel.ImageViewModel
import online.fatbook.fatbookapp.ui.viewmodel.RecipeEditViewModel
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.util.*
import online.fatbook.fatbookapp.util.alert_dialog.FBAlertDialogBuilder
import online.fatbook.fatbookapp.util.alert_dialog.FBAlertDialogListener
import java.io.File
import java.time.LocalTime
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RecipeFirstStageFragment : Fragment(), OnRecipeDifficultyClickListener,
    BaseFragmentActionsListener {

    private var _binding: FragmentRecipeFirstStageBinding? = null
    private val binding get() = _binding!!

    private val recipeEditViewModel by lazy { obtainViewModel(RecipeEditViewModel::class.java) }
    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }
    private val imageViewModel by lazy { obtainViewModel(ImageViewModel::class.java) }
    private var adapter: RecipeCookingDifficultyAdapter? = null

    private var chooseImageFromGallery: ActivityResultLauncher<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeFirstStageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("===t=======RecipeCreateFirstStageFragment==========", "onViewCreated")
        //TODO fix SOFT_INPUT_ADJUST_RESIZE
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        //TODO smht
        if (recipeEditViewModel.recipe.value == null) {
            recipeEditViewModel.setRecipe(Recipe())
        }
        if (recipeEditViewModel.recipeStepImages.value == null) {
            recipeEditViewModel.setRecipeStepImages(HashMap())
        }

        setupDifficultyAdapter()
        setupMenu()
        setupImageEditButtons()

        initObservers()
        initViews()
        initListeners()

    }

    private fun setupDifficultyAdapter() {
        val rv = binding.rvSelectDifficultyRecipe1Stage
        rv.layoutManager = getLayoutManager()
        adapter = RecipeCookingDifficultyAdapter()
        adapter!!.setClickListener(this)
        rv.adapter = adapter
    }

    private fun initListeners() {
        binding.edittextTitleRecipe1Stage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkDataTitle()
                if (!s.isNullOrEmpty()) {
                    recipeEditViewModel.recipe.value!!.title =
                        s.toString().replace("\\s+".toRegex(), " ")
                            .trim()
                } else {
                    recipeEditViewModel.recipe.value!!.title = null
                }

                if (recipeEditViewModel.recipe.value!!.title.isNullOrEmpty()) {
                    binding.toolbarRecipe1Stage.title = " "
                } else {
                    binding.toolbarRecipe1Stage.title =
                        recipeEditViewModel.recipe.value!!.title.toString()
                }
                checkEnableMenu()
                Log.d("NEWRECIPE", "title = ${recipeEditViewModel.recipe.value!!.title}")
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.edittextPortionsQtyRecipe1Stage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkDataPortions()
                if (!s.isNullOrEmpty()) {
                    recipeEditViewModel.recipe.value!!.portions = s.toString().toInt()
                    if (binding.edittextPortionsQtyRecipe1Stage.length() > 1) {
                        hideKeyboard()
                    }
                } else {
                    recipeEditViewModel.recipe.value!!.portions = null
                }
                checkEnableMenu()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.textviewSetTimeRecipe1Stage.setOnClickListener {
            showTimePickerDialog()
        }

        binding.switchPrivateRecipeRecipe1Stage.setOnCheckedChangeListener { _, isChecked ->
            recipeEditViewModel.recipe.value!!.isPrivate = isChecked
            if (isChecked) {
                binding.textviewDescriptionPrivateRecipe1Stage.text =
                    getString(R.string.title_recipe_private)
            } else {
                binding.textviewDescriptionPrivateRecipe1Stage.text =
                    getString(R.string.title_recipe_public)
            }
        }

        //TODO fix
        binding.textviewCookingMethodRecipe1Stage.setOnClickListener {
            staticDataViewModel.setLoadCookingMethod(true)
            navigation(false)
        }

        //TODO fix
        binding.textviewCategoryRecipe1Stage.setOnClickListener {
            staticDataViewModel.setLoadCookingMethod(false)
            navigation(false)
        }

    }

    private fun initViews() {
        recipeEditViewModel.recipe.value!!.difficulty?.let { difficulty ->
            adapter!!.selectedDifficulty =
                staticDataViewModel.cookingDifficulties.value!!.find { it.title == difficulty.title }
        }

        if (staticDataViewModel.cookingDifficulties.value.isNullOrEmpty()) {
            loadDifficulty()
        } else {
            adapter?.setData(staticDataViewModel.cookingDifficulties.value)
            adapter?.selectedDifficulty = staticDataViewModel.cookingDifficulties.value!![0]
            if (recipeEditViewModel.recipe.value!!.difficulty == null) {
                recipeEditViewModel.recipe.value!!.difficulty =
                    staticDataViewModel.cookingDifficulties.value!![0]
            }
        }

        if (recipeEditViewModel.recipe.value!!.cookingMethod != null) {
            binding.textviewCookingMethodRecipe1Stage.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.main_text
                )
            )
            binding.textviewCookingMethodRecipe1Stage.text =
                recipeEditViewModel.recipe.value!!.cookingMethod!!.title
        } else {
            binding.textviewCookingMethodRecipe1Stage.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.hint_text
                )
            )
            binding.textviewCookingMethodRecipe1Stage.text =
                getString(R.string.hint_choose_method)
        }

        if (!recipeEditViewModel.recipe.value!!.cookingCategories.isNullOrEmpty()) {
            binding.textviewCategoryRecipe1Stage.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.main_text
                )
            )
            binding.textviewCategoryRecipe1Stage.text =
                recipeEditViewModel.recipe.value!!.cookingCategories!!.joinToString { "${it.title}" }
        } else {
            binding.textviewCategoryRecipe1Stage.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.hint_text
                )
            )
            binding.textviewCategoryRecipe1Stage.text =
                getString(R.string.hint_choose_category)
        }

        recipeEditViewModel.recipe.value!!.isPrivate?.let {
            binding.switchPrivateRecipeRecipe1Stage.isChecked = it
            if (it) {
                binding.textviewDescriptionPrivateRecipe1Stage.text =
                    getString(R.string.title_recipe_private)
            } else {
                binding.textviewDescriptionPrivateRecipe1Stage.text =
                    getString(R.string.title_recipe_public)
            }
        }

    }

    private fun initObservers() {
        recipeEditViewModel.isLoading.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    binding.loader.progressOverlay.visibility = View.VISIBLE
                    binding.toolbarRecipe1Stage.visibility = View.GONE
                }
                false -> {
                    binding.loader.progressOverlay.visibility = View.GONE
                    binding.toolbarRecipe1Stage.visibility = View.VISIBLE
                }
            }
        }

        recipeEditViewModel.isEditMode.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    fillData()
                }
                false -> {
                    binding.toolbarRecipe1Stage.title = "New recipe"
                    toggleImageButtons(false)
                }
            }
        }

        recipeEditViewModel.recipeImage.observe(viewLifecycleOwner) {
            when (it) {
                null -> {
                    TransitionManager.go(Scene(binding.containerLl), AutoTransition())

                    Glide.with(requireContext())
                        .load(R.drawable.default_recipe_image_rv_feed)
                        .into(binding.imageviewPhotoRecipe1Stage)

                    toggleImageButtons(false)
                }
            }
        }

        recipeEditViewModel.isRecipeEditFinished.observe(viewLifecycleOwner) {
            if (it == true) {
                clearForm()
                recipeEditViewModel.setIsRecipeEditFinishedCreated(false)
            }
        }
    }

    private fun fillData() {
        if (recipeEditViewModel.recipe.value!!.image.isNullOrEmpty()) {
            toggleImageButtons(false)
        } else {
            toggleImageButtons(true)
            Glide
                .with(requireContext())
                .load(recipeEditViewModel.recipe.value!!.image)
                .placeholder(AppCompatResources.getDrawable(requireContext(), R.drawable.default_recipe_image_rv_feed))
                .into(binding.imageviewPhotoRecipe1Stage)
        }

        binding.toolbarRecipe1Stage.title = recipeEditViewModel.recipe.value?.title.toString()
        binding.edittextTitleRecipe1Stage.setText(recipeEditViewModel.recipe.value?.title.toString())
        adapter!!.selectedDifficulty = staticDataViewModel.cookingDifficulties.value!!.find {
            it.title == recipeEditViewModel.recipe.value!!.difficulty!!.title
        }
        binding.edittextPortionsQtyRecipe1Stage.setText(recipeEditViewModel.recipe.value!!.portions.toString())
        showCookingTime()
        binding.textviewCookingMethodRecipe1Stage.text =
            recipeEditViewModel.recipe.value!!.cookingMethod!!.title.toString()
        binding.textviewCategoryRecipe1Stage.text =
            recipeEditViewModel.recipe.value!!.cookingCategories!!.joinToString { it.title.toString() }
        binding.switchPrivateRecipeRecipe1Stage.isChecked =
            recipeEditViewModel.recipe.value!!.isPrivate == true

        recipeEditViewModel.setIsLoading(false)
    }

    private fun checkDataTitle() {
        if (binding.edittextTitleRecipe1Stage.text.toString().isNotEmpty()) {
            binding.edittextTitleRecipe1Stage.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_corner_edittext_recipe_create
                )
        } else {
            binding.edittextTitleRecipe1Stage.isFocusable
            binding.edittextTitleRecipe1Stage.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_corner_edittext_recipe_create_stroke
                )
        }
    }

    private fun checkDataPortions() {
        if (binding.edittextPortionsQtyRecipe1Stage.text.toString().isNotEmpty()
            && binding.edittextPortionsQtyRecipe1Stage.text.toString()
                .toInt() != 0
        ) {
            binding.edittextPortionsQtyRecipe1Stage.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_corner_edittext_recipe_create
                )
        } else {
            binding.edittextPortionsQtyRecipe1Stage.isFocusable
            binding.edittextPortionsQtyRecipe1Stage.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_corner_edittext_recipe_create_stroke
                )
        }
    }

    private fun checkEnableMenu() {
        val isEmpty =
            binding.edittextPortionsQtyRecipe1Stage.text.isNullOrEmpty() ||
                    binding.edittextTitleRecipe1Stage.text.isNullOrEmpty() ||
                    binding.edittextPortionsQtyRecipe1Stage.text.toString().toInt() == 0
        binding.toolbarRecipe1Stage.menu.findItem(R.id.menu_create_first_stage_next).isVisible =
            !isEmpty
    }

    private fun toggleImageButtons(isImageExists: Boolean) {
        if (isImageExists) {
            binding.imageviewPhotoRecipe1Stage.isClickable = true
            binding.buttonEditPhotoRecipe1Stage.setImageResource(R.drawable.ic_btn_edit)
            binding.buttonEditPhotoRecipe1Stage.visibility = View.VISIBLE
            binding.buttonDeletePhotoRecipe1Stage.visibility = View.VISIBLE
        } else {
            binding.imageviewPhotoRecipe1Stage.isClickable = false
            binding.buttonEditPhotoRecipe1Stage.setImageResource(R.drawable.ic_btn_add)
            binding.buttonEditPhotoRecipe1Stage.visibility = View.VISIBLE
            binding.buttonDeletePhotoRecipe1Stage.visibility = View.GONE
        }
    }

    private fun setupMenu() {
        binding.toolbarRecipe1Stage.inflateMenu(R.menu.recipe_create_1_stage_menu)
        binding.toolbarRecipe1Stage.setOnMenuItemClickListener(this::onOptionsItemSelected)
        binding.toolbarRecipe1Stage.setNavigationOnClickListener {
            showClearFormDialog()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_create_first_stage_next -> {
                fillRecipeEditModel()
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
            object : FBAlertDialogListener {
                override fun onClick(dialogInterface: DialogInterface) {
                    if (recipeEditViewModel.isEditMode.value == true) {
                        recipeEditViewModel.setEditMode(false)
                        recipeEditViewModel.setRecipe(Recipe())
                        findNavController().popBackStack()
                    }
                    clearForm()
                    dialogInterface.dismiss()
                }
            },
            object : FBAlertDialogListener {
                override fun onClick(dialogInterface: DialogInterface) {
                    dialogInterface.dismiss()
                }
            }).show()
    }

    private fun clearForm() {
        recipeEditViewModel.setRecipe(Recipe())
        recipeEditViewModel.setRecipeImage(null)
        recipeEditViewModel.setRecipeCookingMethod(null)
        recipeEditViewModel.setRecipeCookingCategories(null)
        recipeEditViewModel.setRecipeCookingTimeHours(null)
        recipeEditViewModel.setRecipeCookingTimeMinutes(null)
        recipeEditViewModel.setRecipeIngredients(ArrayList())
        recipeEditViewModel.setRecipeAddIngredient(null)
        recipeEditViewModel.setRecipeAddRecipeIngredient(null)
        recipeEditViewModel.setRecipeStepImages(HashMap())
        recipeEditViewModel.setSelectedCookingStep(null)
        recipeEditViewModel.setSelectedCookingStepPosition(null)
        (requireActivity() as MainActivity).redrawFragment(2)
    }

    private fun setupImageEditButtons() {
        binding.imageviewPhotoRecipe1Stage.setOnClickListener {
            imageViewModel.setImage(binding.imageviewPhotoRecipe1Stage.drawable)
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_image_view_from_first_stage)
        }
        binding.buttonEditPhotoRecipe1Stage.setOnClickListener {
            if (verifyStoragePermissions(requireActivity())) {
                chooseImageFromGallery!!.launch("image/*")
            }
        }
        binding.buttonDeletePhotoRecipe1Stage.setOnClickListener {
            recipeEditViewModel.setRecipeImage(null)
        }
        chooseImageFromGallery =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (verifyStoragePermissions(requireActivity())) {
                    uri?.let {
                        toggleImageButtons(true)
                        val path = FileUtils.getPath(requireContext(), it)
                        recipeEditViewModel.setRecipeImage(path?.let { file -> File(file) })
                        Glide.with(requireContext()).load(uri)
                            .into(binding.imageviewPhotoRecipe1Stage)
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
                staticDataViewModel.setCookingDifficulties(value)
                adapter?.setData(value)
                adapter?.selectedDifficulty = value[0]
                if (recipeEditViewModel.recipe.value!!.difficulty == null) {
                    recipeEditViewModel.recipe.value!!.difficulty = value[0]
                }
                binding.toolbarRecipe1Stage.visibility = View.VISIBLE
                binding.loader.progressOverlay.visibility = View.GONE
            }

            override fun onFailure(value: List<CookingDifficulty>?) {
                loadDifficulty()
            }
        })
    }

    private fun getLayoutManager(): RecyclerView.LayoutManager {
        return FlexboxLayoutManager(context).apply {
            this.flexDirection = FlexDirection.ROW
            this.flexWrap = FlexWrap.WRAP
            this.justifyContent = JustifyContent.SPACE_BETWEEN
        }
    }

    //TODO ref
    private fun fillRecipeEditModel() {
        if (recipeEditViewModel.recipe.value!!.cookingTime.isNullOrEmpty()) {
            recipeEditViewModel.recipe.value!!.cookingTime = "00:15"
            recipeEditViewModel.setRecipeCookingTimeHours(0)
            recipeEditViewModel.setRecipeCookingTimeMinutes(15)
        }
        if (recipeEditViewModel.recipe.value!!.cookingMethod == null) {
            recipeEditViewModel.recipe.value!!.cookingMethod = staticDataViewModel.getOtherMethod()
        }
        if (recipeEditViewModel.recipe.value!!.cookingCategories.isNullOrEmpty()) {
            recipeEditViewModel.recipe.value!!.cookingCategories =
                staticDataViewModel.getOtherCategory()
        }
        if (recipeEditViewModel.recipe.value!!.portions == null) {
            recipeEditViewModel.recipe.value!!.portions = 1
        }
        Log.d("NEW RECIPE:", "${recipeEditViewModel.recipe.value}")
    }

    private fun navigation(nextStep: Boolean) {
        when (nextStep) {
            true -> {
                if (recipeEditViewModel.isEditMode.value == true) {
                    findNavController().navigate(R.id.action_go_to_second_stage_from_edit)
                } else {
                    NavHostFragment.findNavController(this)
                        .navigate(R.id.action_go_to_second_stage_from_first_stage)
                }
            }
            false -> {
                if (recipeEditViewModel.isEditMode.value == true) {
                    findNavController().navigate(R.id.action_go_to_method_category_from_edit)
                } else {
                    NavHostFragment.findNavController(this)
                        .navigate(R.id.action_go_to_method_category_from_first_stage)
                }
            }
        }

//        if (nextStep) {
//            NavHostFragment.findNavController(this)
//                .navigate(R.id.action_go_to_second_stage_from_first_stage)
//        } else {
//                NavHostFragment.findNavController(this)
//                .navigate(R.id.action_go_to_method_category_from_first_stage)
//        }
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
                recipeEditViewModel.setRecipeCookingTimeHours(cookingTime.hour)
                recipeEditViewModel.setRecipeCookingTimeMinutes(cookingTime.minute)
                recipeEditViewModel.recipe.value!!.cookingTime = cookingTime.toString()
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
            if (recipeEditViewModel.recipeCookingTimeHours.value == null || recipeEditViewModel.recipeCookingTimeHours.value == 0) {
                0
            } else {
                recipeEditViewModel.recipeCookingTimeHours.value!!
            }
        picker.minute =
            if (recipeEditViewModel.recipeCookingTimeMinutes.value == null || recipeEditViewModel.recipeCookingTimeMinutes.value == 0) {
                0
            } else {
                recipeEditViewModel.recipeCookingTimeMinutes.value!!
            }
    }

    private fun showCookingTime() {
        if (recipeEditViewModel.recipe.value!!.cookingTime.isNullOrEmpty()) {
            binding.textviewSetTimeRecipe1Stage.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.hint_text
                )
            )
        } else {
            RecipeUtils.prettyCookingTime(recipeEditViewModel.recipe.value!!.cookingTime)?.let {
                binding.textviewSetTimeRecipe1Stage.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.main_text
                    )
                )
                binding.textviewSetTimeRecipe1Stage.text = it
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
        recipeEditViewModel.recipe.value!!.difficulty = difficulty
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
        _binding = null
    }

    override fun onBackPressedBase(): Boolean {
        return false
    }

    override fun scrollUpBase() {
        binding.nsvRecipe1Stage.scrollTo(0, 0)
        binding.appBarLayoutRecipe1Stage.setExpanded(true, false)
    }

    override fun onResume() {
        super.onResume()
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(
            KeyboardActionUtil(
                binding.root, requireActivity()
            ).listenerForAdjustResize
        )
    }

    override fun onPause() {
        super.onPause()
        binding.root.viewTreeObserver.removeOnGlobalLayoutListener(
            KeyboardActionUtil(
                binding.root, requireActivity()
            ).listenerForAdjustResize
        )
    }
}
