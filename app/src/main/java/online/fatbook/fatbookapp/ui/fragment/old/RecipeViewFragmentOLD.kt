package online.fatbook.fatbookapp.ui.fragment.old

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import lombok.extern.java.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.FragmentRecipeViewOldBinding
import online.fatbook.fatbookapp.network.service.RetrofitFactory
import online.fatbook.fatbookapp.ui.adapters.ViewRecipeIngredientAdapter
import online.fatbook.fatbookapp.ui.listeners.OnRecipeRevertDeleteListener
import online.fatbook.fatbookapp.ui.listeners.OnRecipeViewDeleteIngredientListener
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.Constants.TYPE_RECIPE
import online.fatbook.fatbookapp.util.FileUtils
import online.fatbook.fatbookapp.util.KeyboardActionUtil
import online.fatbook.fatbookapp.util.RecipeUtils
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

@Log
class RecipeViewFragmentOLD : Fragment(), OnRecipeViewDeleteIngredientListener {
    private var binding: FragmentRecipeViewOldBinding? = null
    private var recipe: Recipe? = null
    private var user: User? = null
    private var recipeViewModel: RecipeViewModel? = null
    private var userViewModel: UserViewModel? = null
    private var adapter: ViewRecipeIngredientAdapter? = null
    private val onRecipeRevertDeleteListener: OnRecipeRevertDeleteListener? = null
    private var ingredientListTemp: ArrayList<RecipeIngredient>? = null
    private var addIngredient = false
    private var choosePhotoFromGallery: ActivityResultLauncher<String>? = null
    private var recipePhoto: File? = null
    private var selectedImageUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        recipeViewModel = ViewModelProvider(requireActivity()).get(RecipeViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        recipeViewModel!!.selectedRecipe
            .observe(viewLifecycleOwner) { recipe: Recipe? -> this.recipe = recipe }
        userViewModel!!.user.observe(viewLifecycleOwner) { user: User? -> this.user = user }
        loadData()
        setupAdapter()
        showData()
        setupMenu()
        try {
            choosePhotoFromGallery = registerForActivityResult(GetContent()) { uri: Uri? ->
                verifyStoragePermissions(requireActivity())
                if (uri != null) {
                    selectedImageUri = uri
                    val path = FileUtils.getPath(requireContext(), selectedImageUri!!)
                    recipePhoto = File(path)
                    binding!!.imageViewRecipeViewImage.setImageURI(selectedImageUri)
                    binding!!.buttonRecipeViewImageAdd.visibility = View.GONE
                    binding!!.buttonRecipeViewImageChange.visibility = View.VISIBLE
                    binding!!.buttonRecipeViewImageDelete.visibility = View.VISIBLE
                } else {
                    binding!!.buttonRecipeViewImageAdd.visibility = View.VISIBLE
                    binding!!.buttonRecipeViewImageChange.visibility = View.GONE
                    binding!!.buttonRecipeViewImageDelete.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding!!.imageViewRecipeViewFork.setOnClickListener { view1: View? ->
            val tag = binding!!.imageViewRecipeViewFork.tag as String
            when (tag) {
                RecipeUtils.TAG_FORK_CHECKED -> forked(false)
                RecipeUtils.TAG_FORK_UNCHECKED -> forked(true)
            }
        }
        binding!!.imageViewRecipeViewIconBookmarks.setOnClickListener { view1: View? ->
            val tag = binding!!.imageViewRecipeViewIconBookmarks.tag as String
            when (tag) {
                RecipeUtils.TAG_FAVOURITES_CHECKED -> bookmarked(false)
                RecipeUtils.TAG_FAVOURITES_UNCHECKED -> bookmarked(true)
            }
        }
        binding!!.buttonRecipeViewIngredientAdd.setOnClickListener { view1: View? ->
            addIngredient = true
            NavHostFragment.findNavController(this).navigate(R.id.navigation_add_ingredient_old)
        }
        binding!!.buttonRecipeViewImageAdd.setOnClickListener { view1: View? ->
            verifyStoragePermissions(requireActivity())
            choosePhotoFromGallery!!.launch("image/*")
        }
        binding!!.buttonRecipeViewImageChange.setOnClickListener { view1: View? ->
            verifyStoragePermissions(requireActivity())
            choosePhotoFromGallery!!.launch("image/*")
        }
        binding!!.buttonRecipeViewImageDelete.setOnClickListener { view1: View? ->
            selectedImageUri = null
            recipePhoto = null
            recipe!!.image = StringUtils.EMPTY
            showData()
            binding!!.buttonRecipeViewImageAdd.visibility = View.VISIBLE
            binding!!.buttonRecipeViewImageChange.visibility = View.GONE
            binding!!.buttonRecipeViewImageDelete.visibility = View.GONE
        }
    }

    private fun uploadImage() {
        if (recipePhoto != null) {
            try {
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), recipePhoto!!)
                val fileName = "image" + recipePhoto!!.name.substring(
                    recipePhoto!!.name.indexOf('.')
                )
                val file = MultipartBody.Part.createFormData("file", fileName, requestFile)
                RetrofitFactory.apiService()
                    .uploadImage(file, TYPE_RECIPE, recipe!!.identifier)
                    .enqueue(object : Callback<Recipe?> {
                        override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
                            if (response.code() == 200) {
//                                RecipeViewFragment.log.log(Level.INFO, "image add SUCCESS")
                            } else {
//                                RecipeViewFragment.log.log(
//                                    Level.INFO,
//                                    "image add FAILED " + response.code()
//                                )
                            }
                        }

                        override fun onFailure(call: Call<Recipe?>, t: Throwable) {
//                            RecipeViewFragment.log.log(Level.INFO, "image add FAILED")
                        }
                    })
            } catch (e: Exception) {
//                RecipeViewFragment.log.log(Level.INFO, e.toString())
                e.printStackTrace()
            }
        }
    }

    private fun forked(value: Boolean) {
        toggleForks(value)
        recipeForked(recipe, value)
    }

    private fun recipeForked(_recipe: Recipe?, fork: Boolean) {
        RetrofitFactory.apiService().recipeForked(user!!.pid, _recipe!!.pid, fork)
            .enqueue(object : Callback<Recipe?> {
                override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
//                    RecipeViewFragment.log.log(Level.INFO, "fork SUCCESS")
                    recipeViewModel!!.setSelectedRecipe(response.body())
                    loadUser()
                }

                override fun onFailure(call: Call<Recipe?>, t: Throwable) {
//                    RecipeViewFragment.log.log(Level.INFO, "fork FAILED")
                }
            })
    }

    private fun bookmarked(value: Boolean) {
        toggleBookmarks(value)
        recipeBookmarked(recipe, value)
    }

    private fun recipeBookmarked(_recipe: Recipe?, bookmark: Boolean) {
        RetrofitFactory.apiService()
            .recipeBookmarked(user!!.pid, _recipe!!.pid, bookmark)
            .enqueue(object : Callback<Recipe?> {
                override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
//                    RecipeViewFragment.log.log(Level.INFO, "bookmark SUCCESS")
                    recipeViewModel!!.setSelectedRecipe(response.body())
                    loadUser()
                }

                override fun onFailure(call: Call<Recipe?>, t: Throwable) {
//                    RecipeViewFragment.log.log(Level.INFO, "bookmark FAILED")
                }
            })
    }

    private fun loadUser() {
        RetrofitFactory.apiService().getUserByUsername(user!!.username)
            .enqueue(object : Callback<User?> {
                override fun onResponse(call: Call<User?>, response: Response<User?>) {
//                    RecipeViewFragment.log.log(Level.INFO, "user load SUCCESS")
                    assert(response.body() != null)
//                    RecipeViewFragment.log.log(Level.INFO, response.body().toString())
                    response.body()?.let { userViewModel!!.setUser(it) }
                    showData()
                }

                override fun onFailure(call: Call<User?>, t: Throwable) {
//                    RecipeViewFragment.log.log(Level.INFO, "user load FAILED")
                }
            })
    }

    private fun showDialogDelete() {
        val msg = resources.getString(R.string.alert_dialog_delete_recipe_message)
        val textViewMsg = TextView(requireContext())
        textViewMsg.text = msg
        textViewMsg.setSingleLine()
        textViewMsg.setTextColor(resources.getColor(R.color.color_blue_grey_600))
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
            LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog_title_recipe, null)
        AlertDialog.Builder(requireContext())
            .setView(container)
            .setCustomTitle(title)
            .setPositiveButton(getString(R.string.alert_dialog_btn_yes)) { dialogInterface: DialogInterface?, i: Int -> deleteRecipe() }
            .setNegativeButton(getString(R.string.alert_dialog_btn_cancel)) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
            .show()
    }

    /*
    private void onConfirmDeleteRecipeClick() {
        Snackbar.make(requireActivity().findViewById(R.id.container), R.string.snackbar_recipe_deleted, Snackbar.LENGTH_SHORT)
                .setAnchorView(requireActivity().findViewById(R.id.bottom_navigation))
                .setAction(R.string.undo_string, view -> {
                    onRecipeRevertDeleteListener.onRecipeRevertDeleteClick(recipe);
                })
                .show();
        closeFragment();
    }

    private void closeFragment() {
        NavHostFragment.findNavController(this).popBackStack();
    }
     */
    private fun setupMenu() {
        val activity = (activity as AppCompatActivity?)!!
        activity.setSupportActionBar(binding!!.toolbarRecipeViewOld)
        binding!!.toolbarRecipeViewOld.setNavigationOnClickListener { view: View? -> navigateBack() }
//        if (recipe!!.author == userViewModel!!.user.value!!.username) {
//            setHasOptionsMenu(true)
//            binding!!.kuzyaRecipeView.visibility = View.GONE
//        } else {
//            setHasOptionsMenu(false)
//            binding!!.kuzyaRecipeView.visibility = View.VISIBLE
//        }
        binding!!.editTextRecipeViewName.isEnabled = false
        binding!!.editTextRecipeViewDescription.isEnabled = false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipe_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        if (addIngredient) {
            toggleEditMode(true)
            addIngredient = false
            recipe!!.ingredients!!.add(recipeViewModel!!.selectedRecipeIngredient.value!!)
            adapter!!.setData(recipe!!.ingredients)
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_recipe_edit -> {
                ingredientListTemp = ArrayList(recipe!!.ingredients!!)
                toggleEditMode(true)
                niceCheck()
                true
            }
            R.id.menu_recipe_save -> {
                if (niceCheck()) {
                    toggleEditMode(false)
                    saveEdit()
                } else {
                    if (StringUtils.isEmpty(binding!!.editTextRecipeViewName.text)) {
                        binding!!.editTextRecipeViewName.setBackgroundResource(R.drawable.round_corner_edittext)
                    }
                    if (StringUtils.isEmpty(binding!!.editTextRecipeViewDescription.text)) {
                        binding!!.editTextRecipeViewDescription.setBackgroundResource(R.drawable.round_corner_edittext)
                    }
                    showDialogEmptyRecipe()
                }
                true
            }
            R.id.menu_recipe_cancel -> {
                toggleEditMode(false)
                cancelEdit()
                true
            }
            R.id.menu_recipe_delete -> {
                showDialogDelete()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun niceCheck(): Boolean {
        return (StringUtils.isNotEmpty(binding!!.editTextRecipeViewName.text.toString())
                && StringUtils.isNotEmpty(binding!!.editTextRecipeViewDescription.text.toString())
                && recipe!!.ingredients!!.isNotEmpty())
    }

    private fun showDialogEmptyRecipe() {
        val msg = resources.getString(R.string.alert_dialog_empty_recipe_message)
        val textViewMsg = TextView(requireContext())
        textViewMsg.text = msg
        textViewMsg.setSingleLine()
        textViewMsg.setTextColor(resources.getColor(R.color.color_blue_grey_600))
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
        val title = LayoutInflater.from(requireContext())
            .inflate(R.layout.alert_dialog_title_empty_recipe, null)
        AlertDialog.Builder(requireContext())
            .setView(container)
            .setCustomTitle(title)
            .setPositiveButton(getString(R.string.alert_dialog_btn_ok)) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
            .show()
    }

    private fun toggleEditMode(allow: Boolean) {
        binding!!.editTextRecipeViewName.isEnabled = allow
        binding!!.editTextRecipeViewDescription.isEnabled = allow
        changeMenuItemsVisibility(!allow, allow, allow, allow)
        adapter!!.setEditMode(allow)
        adapter!!.notifyDataSetChanged()
        if (allow) {
            binding!!.linearlayoutButtonsImage.visibility = View.VISIBLE
            if (recipePhoto == null && StringUtils.isEmpty(recipe!!.image)) {
                binding!!.buttonRecipeViewImageAdd.visibility = View.VISIBLE
                binding!!.buttonRecipeViewImageChange.visibility = View.GONE
                binding!!.buttonRecipeViewImageDelete.visibility = View.GONE
            } else {
                binding!!.buttonRecipeViewImageAdd.visibility = View.GONE
                binding!!.buttonRecipeViewImageChange.visibility = View.VISIBLE
                binding!!.buttonRecipeViewImageDelete.visibility = View.VISIBLE
            }
            binding!!.editTextRecipeViewName.setBackgroundResource(R.drawable.edit_mode_bgr)
            binding!!.editTextRecipeViewDescription.setBackgroundResource(R.drawable.edit_mode_bgr)
        } else {
            binding!!.linearlayoutButtonsImage.visibility = View.GONE
            binding!!.editTextRecipeViewName.setBackgroundResource(R.drawable.round_corner_rect_white)
            binding!!.editTextRecipeViewDescription.setBackgroundResource(R.drawable.round_corner_rect_white)
        }
    }

    private fun saveEdit() {
        recipe!!.title = binding!!.editTextRecipeViewName.text.toString()
        saveRecipe()
        uploadImage()
    }

    private fun deleteRecipe() {
        RetrofitFactory.apiService().recipeDelete(recipe).enqueue(object : Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (response.code() == 200) {
//                    RecipeViewFragment.log.log(Level.INFO, "delete recipe SUCCESS")
                    recipeViewModel!!.setSelectedRecipe(null)
                    navigateBack()
                } else {
//                    RecipeViewFragment.log.log(Level.INFO, "delete recipe FAILED" + response.code())
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
//                RecipeViewFragment.log.log(Level.INFO, "delete recipe FAILED")
            }
        })
    }

    private fun navigateBack() {
        NavHostFragment.findNavController(this).popBackStack()
    }

    private fun saveRecipe() {
        RetrofitFactory.apiService().recipeUpdate(recipe).enqueue(object : Callback<Recipe?> {
            override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
//                RecipeViewFragment.log.log(Level.INFO, "edit recipe save SUCCESS")
            }

            override fun onFailure(call: Call<Recipe?>, t: Throwable) {
//                RecipeViewFragment.log.log(Level.INFO, "edit recipe save FAILED")
                t.printStackTrace()
            }
        })
    }

    private fun cancelEdit() {
        toggleEditMode(false)
        revertData()
    }

    private fun revertData() {
        recipe!!.ingredients = ingredientListTemp
        showData()
        adapter!!.setData(recipe!!.ingredients)
        adapter!!.notifyDataSetChanged()
    }

    private fun changeMenuItemsVisibility(
        edit: Boolean,
        save: Boolean,
        cancel: Boolean,
        delete: Boolean
    ) {
        binding!!.toolbarRecipeViewOld.menu.findItem(R.id.menu_recipe_edit).isVisible = edit
        binding!!.toolbarRecipeViewOld.menu.findItem(R.id.menu_recipe_save).isVisible = save
        binding!!.toolbarRecipeViewOld.menu.findItem(R.id.menu_recipe_cancel).isVisible = cancel
        binding!!.toolbarRecipeViewOld.menu.findItem(R.id.menu_recipe_delete).isVisible = delete
        if (!edit) {
            binding!!.buttonRecipeViewIngredientAdd.visibility = View.VISIBLE
        } else {
            binding!!.buttonRecipeViewIngredientAdd.visibility = View.GONE
        }
    }

    private fun loadData() {
        recipe = recipeViewModel!!.selectedRecipe.value
        user = userViewModel!!.user.value
        if (recipe == null) {
            recipe = Recipe()
        }
    }

    private fun showData() {
        binding!!.editTextRecipeViewName.setText(recipe!!.title)
        if (StringUtils.isNotEmpty(recipe!!.image)) {
            Glide
                .with(requireContext())
                .load(recipe!!.image)
                .into(binding!!.imageViewRecipeViewImage)
        } else {
            binding!!.imageViewRecipeViewImage.setImageDrawable(resources.getDrawable(R.drawable.default_recipe_image_recipe_create_second_stage))
        }
//        binding!!.textViewRecipeViewUsername.text = recipe!!.author
//        binding!!.textViewRecipeViewForksQuantity.text = recipe!!.forks.toString()
//        if (recipe!!.author == user!!.username) {
//            binding!!.imageViewRecipeViewIconBookmarks.visibility = View.INVISIBLE
//        } else {
//            toggleBookmarks(user!!.recipesFavourites!!.contains(recipe!!.identifier))
//        }
//        toggleForks(user!!.recipesForked!!.contains(recipe!!.identifier))
    }

    private fun setupAdapter() {
        val recyclerView = binding!!.rvRecipeViewIngredients
        adapter = ViewRecipeIngredientAdapter()
        adapter!!.setData(recipe!!.ingredients)
        recyclerView.adapter = adapter
    }

    private fun toggleBookmarks(check: Boolean) {
        if (check) {
            binding!!.imageViewRecipeViewIconBookmarks.setImageResource(R.drawable.icon_bookmarks_checked)
            binding!!.imageViewRecipeViewIconBookmarks.tag = RecipeUtils.TAG_FAVOURITES_CHECKED
        } else {
            binding!!.imageViewRecipeViewIconBookmarks.setImageResource(R.drawable.icon_bookmarks_unchecked)
            binding!!.imageViewRecipeViewIconBookmarks.tag = RecipeUtils.TAG_FAVOURITES_UNCHECKED
        }
    }

    private fun toggleForks(selected: Boolean) {
        if (selected) {
            binding!!.imageViewRecipeViewFork.setImageResource(R.drawable.ic_fork_checked)
            binding!!.imageViewRecipeViewFork.tag = RecipeUtils.TAG_FORK_CHECKED
        } else {
            binding!!.imageViewRecipeViewFork.setImageResource(R.drawable.ic_fork_unchecked)
            binding!!.imageViewRecipeViewFork.tag = RecipeUtils.TAG_FORK_UNCHECKED
        }
    }

    override fun onDeleteIngredientClick(recipeIngredient: RecipeIngredient?, position: Int) {
        recipe!!.ingredients!!.remove(recipeIngredient)
        adapter!!.setData(recipe!!.ingredients)
        adapter!!.notifyItemRemoved(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeViewOldBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
        binding!!.root.viewTreeObserver.addOnGlobalLayoutListener(
            KeyboardActionUtil(
                binding!!.root, requireActivity()
            ).listenerForAdjustResize
        )
        if (selectedImageUri != null) {
            binding!!.imageViewRecipeViewImage.setImageURI(selectedImageUri)
        }
    }

    override fun onPause() {
        super.onPause()
        binding!!.root.viewTreeObserver.removeOnGlobalLayoutListener(
            KeyboardActionUtil(
                binding!!.root, requireActivity()
            ).listenerForAdjustResize
        )
    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        fun verifyStoragePermissions(activity: Activity?) {
            val permission = ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
            }
        }
    }
}