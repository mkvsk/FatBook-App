package online.fatbook.fatbookapp.ui.fragment

import android.Manifest
import androidx.lifecycle.ViewModelProvider.get
import androidx.navigation.NavController.navigate
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.NavController.popBackStack
import androidx.appcompat.app.AppCompatActivity
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.ui.viewmodel.IngredientViewModel
import androidx.navigation.NavController
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import online.fatbook.fatbookapp.util.UserUtils
import online.fatbook.fatbookapp.R
import android.content.SharedPreferences
import com.google.android.material.navigation.NavigationBarView
import online.fatbook.fatbookapp.ui.viewmodel.SignInViewModel
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.content.ContextCompat
import android.content.Intent
import online.fatbook.fatbookapp.ui.activity.PasswordActivity
import android.text.TextWatcher
import android.text.Editable
import androidx.appcompat.content.res.AppCompatResources
import online.fatbook.fatbookapp.ui.activity.LoginActivity
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import online.fatbook.fatbookapp.ui.activity.MainActivity
import online.fatbook.fatbookapp.ui.activity.SignInActivity
import online.fatbook.fatbookapp.ui.activity.WelcomeActivity
import online.fatbook.fatbookapp.ui.activity.SplashActivity
import androidx.activity.result.ActivityResultLauncher
import online.fatbook.fatbookapp.ui.activity.fill_additional_info.FillAdditionalInfoViewModel
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.ActivityResultCallback
import online.fatbook.fatbookapp.ui.activity.fill_additional_info.FillAdditionalInfoActivity
import android.app.Activity
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import online.fatbook.fatbookapp.ui.activity.SkipAdditionalInfoActivity
import android.widget.Toast
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import online.fatbook.fatbookapp.util.RecipeUtils
import android.widget.TextView
import android.widget.ImageButton
import online.fatbook.fatbookapp.ui.listeners.OnRecipeViewDeleteIngredient
import online.fatbook.fatbookapp.ui.listeners.OnAddIngredientItemClickListener
import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.cardview.widget.CardView
import online.fatbook.fatbookapp.ui.listeners.OnRecipeRevertDeleteListener
import online.fatbook.fatbookapp.ui.adapters.RecipeAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import androidx.recyclerview.widget.SimpleItemAnimator
import online.fatbook.fatbookapp.ui.fragment.FeedFragment
import androidx.navigation.fragment.NavHostFragment
import online.fatbook.fatbookapp.util.KeyboardActionUtil
import online.fatbook.fatbookapp.ui.fragment.BookmarksFragment
import online.fatbook.fatbookapp.ui.adapters.ViewRecipeIngredientAdapter
import online.fatbook.fatbookapp.ui.fragment.RecipeViewFragment
import okhttp3.RequestBody
import okhttp3.MultipartBody
import android.widget.FrameLayout
import android.content.DialogInterface
import online.fatbook.fatbookapp.ui.adapters.IngredientsAdapter
import online.fatbook.fatbookapp.ui.fragment.IngredientsFragment
import android.widget.EditText
import android.content.DialogInterface.OnShowListener
import com.google.android.material.appbar.AppBarLayout
import android.graphics.PorterDuff
import androidx.fragment.app.FragmentActivity
import online.fatbook.fatbookapp.ui.fragment.UserProfileFragment
import online.fatbook.fatbookapp.ui.fragment.RecipeCreateFragment
import online.fatbook.fatbookapp.ui.adapters.AddIngredientToRecipeAdapter
import online.fatbook.fatbookapp.ui.fragment.RecipeAddIngredientFragment
import androidx.lifecycle.ViewModel
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.os.Environment
import android.text.TextUtils
import android.content.ContentUris
import android.net.Uri
import android.provider.OpenableColumns
import android.view.*
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import lombok.extern.java.Log
import okhttp3.MediaType
import online.fatbook.fatbookapp.core.*
import online.fatbook.fatbookapp.databinding.FragmentRecipeViewBinding
import online.fatbook.fatbookapp.retrofit.NetworkInfoService
import online.fatbook.fatbookapp.util.FileUtils
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Multipart
import java.io.File
import java.lang.Exception
import java.util.ArrayList
import java.util.logging.Level

@Log
class RecipeViewFragment : Fragment(), OnRecipeViewDeleteIngredient {
    private var binding: FragmentRecipeViewBinding? = null
    private var recipe: Recipe? = null
    private var user: User? = null
    private var recipeViewModel: RecipeViewModel? = null
    private var userViewModel: UserViewModel? = null
    private var adapter: ViewRecipeIngredientAdapter? = null
    private val onRecipeRevertDeleteListener: OnRecipeRevertDeleteListener? = null
    private var ingredientListTemp: List<RecipeIngredient>? = null
    private var addIngredient = false
    private var choosePhotoFromGallery: ActivityResultLauncher<String>? = null
    private var recipePhoto: File? = null
    private var selectedImageUri: Uri? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        recipeViewModel = ViewModelProvider(requireActivity()).get(RecipeViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        recipeViewModel.getSelectedRecipe()
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
                RecipeUtils.TAG_BOOKMARKS_CHECKED -> bookmarked(false)
                RecipeUtils.TAG_BOOKMARKS_UNCHECKED -> bookmarked(true)
            }
        }
        binding!!.buttonRecipeViewIngredientAdd.setOnClickListener { view1: View? ->
            addIngredient = true
            NavHostFragment.findNavController(this).navigate(R.id.navigation_add_ingredient)
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
            recipe.setImage(StringUtils.EMPTY)
            showData()
            binding!!.buttonRecipeViewImageAdd.visibility = View.VISIBLE
            binding!!.buttonRecipeViewImageChange.visibility = View.GONE
            binding!!.buttonRecipeViewImageDelete.visibility = View.GONE
        }
    }

    private fun uploadImage() {
        if (recipePhoto != null) {
            try {
                val requestFile = RequestBody.create(MediaType.parse("image/*"), recipePhoto)
                val fileName = "image" + recipePhoto!!.name.substring(
                    recipePhoto!!.name.indexOf('.')
                )
                val file = MultipartBody.Part.createFormData("file", fileName, requestFile)
                RetrofitFactory.apiServiceClient()
                    .uploadImage(file, FileUtils.TAG_RECIPE, recipe.getIdentifier())
                    .enqueue(object : Callback<Recipe?> {
                        override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
                            if (response.code() == 200) {
                                RecipeViewFragment.log.log(Level.INFO, "image add SUCCESS")
                            } else {
                                RecipeViewFragment.log.log(
                                    Level.INFO,
                                    "image add FAILED " + response.code()
                                )
                            }
                        }

                        override fun onFailure(call: Call<Recipe?>, t: Throwable) {
                            RecipeViewFragment.log.log(Level.INFO, "image add FAILED")
                        }
                    })
            } catch (e: Exception) {
                RecipeViewFragment.log.log(Level.INFO, e.toString())
                e.printStackTrace()
            }
        }
    }

    private fun forked(value: Boolean) {
        toggleForks(value)
        recipeForked(recipe, value)
    }

    private fun recipeForked(_recipe: Recipe?, fork: Boolean) {
        RetrofitFactory.apiServiceClient().recipeForked(user.getPid(), _recipe.getPid(), fork)
            .enqueue(object : Callback<Recipe?> {
                override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
                    RecipeViewFragment.log.log(Level.INFO, "fork SUCCESS")
                    recipeViewModel!!.setSelectedRecipe(response.body())
                    loadUser()
                }

                override fun onFailure(call: Call<Recipe?>, t: Throwable) {
                    RecipeViewFragment.log.log(Level.INFO, "fork FAILED")
                }
            })
    }

    private fun bookmarked(value: Boolean) {
        toggleBookmarks(value)
        recipeBookmarked(recipe, value)
    }

    private fun recipeBookmarked(_recipe: Recipe?, bookmark: Boolean) {
        RetrofitFactory.apiServiceClient()
            .recipeBookmarked(user.getPid(), _recipe.getPid(), bookmark)
            .enqueue(object : Callback<Recipe?> {
                override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
                    RecipeViewFragment.log.log(Level.INFO, "bookmark SUCCESS")
                    recipeViewModel!!.setSelectedRecipe(response.body())
                    loadUser()
                }

                override fun onFailure(call: Call<Recipe?>, t: Throwable) {
                    RecipeViewFragment.log.log(Level.INFO, "bookmark FAILED")
                }
            })
    }

    private fun loadUser() {
        RetrofitFactory.apiServiceClient().getUser(user.getLogin())
            .enqueue(object : Callback<User?> {
                override fun onResponse(call: Call<User?>, response: Response<User?>) {
                    RecipeViewFragment.log.log(Level.INFO, "user load SUCCESS")
                    assert(response.body() != null)
                    RecipeViewFragment.log.log(Level.INFO, response.body().toString())
                    userViewModel!!.setUser(response.body())
                    showData()
                }

                override fun onFailure(call: Call<User?>, t: Throwable) {
                    RecipeViewFragment.log.log(Level.INFO, "user load FAILED")
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
        activity.setSupportActionBar(binding!!.toolbarRecipeView)
        binding!!.toolbarRecipeView.setNavigationOnClickListener { view: View? -> navigateBack() }
        if (recipe.getAuthor() == userViewModel!!.user.value.getLogin()) {
            setHasOptionsMenu(true)
            binding!!.kuzyaRecipeView.visibility = View.GONE
        } else {
            setHasOptionsMenu(false)
            binding!!.kuzyaRecipeView.visibility = View.VISIBLE
        }
        binding!!.editTextRecipeViewName.isEnabled = false
        binding!!.editTextRecipeViewDescription.isEnabled = false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipe_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        if (addIngredient) {
            toggleEditMode(true)
            addIngredient = false
            recipe.getIngredients().add(recipeViewModel!!.selectedRecipeIngredient.value)
            adapter!!.setData(recipe.getIngredients())
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_recipe_edit -> {
                ingredientListTemp = ArrayList(recipe.getIngredients())
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
                && !recipe.getIngredients().isEmpty())
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
            if (recipePhoto == null && StringUtils.isEmpty(recipe.getImage())) {
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
        recipe.setName(binding!!.editTextRecipeViewName.text.toString())
        recipe.setDescription(binding!!.editTextRecipeViewDescription.text.toString())
        saveRecipe()
        uploadImage()
    }

    private fun deleteRecipe() {
        RetrofitFactory.apiServiceClient().recipeDelete(recipe).enqueue(object : Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (response.code() == 200) {
                    RecipeViewFragment.log.log(Level.INFO, "delete recipe SUCCESS")
                    recipeViewModel!!.setSelectedRecipe(null)
                    navigateBack()
                } else {
                    RecipeViewFragment.log.log(Level.INFO, "delete recipe FAILED" + response.code())
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                RecipeViewFragment.log.log(Level.INFO, "delete recipe FAILED")
            }
        })
    }

    private fun navigateBack() {
        NavHostFragment.findNavController(this).popBackStack()
    }

    private fun saveRecipe() {
        RetrofitFactory.apiServiceClient().recipeUpdate(recipe).enqueue(object : Callback<Recipe?> {
            override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
                RecipeViewFragment.log.log(Level.INFO, "edit recipe save SUCCESS")
            }

            override fun onFailure(call: Call<Recipe?>, t: Throwable) {
                RecipeViewFragment.log.log(Level.INFO, "edit recipe save FAILED")
                t.printStackTrace()
            }
        })
    }

    private fun cancelEdit() {
        toggleEditMode(false)
        revertData()
    }

    private fun revertData() {
        recipe.setIngredients(ingredientListTemp)
        showData()
        adapter!!.setData(recipe.getIngredients())
        adapter!!.notifyDataSetChanged()
    }

    private fun changeMenuItemsVisibility(
        edit: Boolean,
        save: Boolean,
        cancel: Boolean,
        delete: Boolean
    ) {
        binding!!.toolbarRecipeView.menu.findItem(R.id.menu_recipe_edit).isVisible = edit
        binding!!.toolbarRecipeView.menu.findItem(R.id.menu_recipe_save).isVisible = save
        binding!!.toolbarRecipeView.menu.findItem(R.id.menu_recipe_cancel).isVisible = cancel
        binding!!.toolbarRecipeView.menu.findItem(R.id.menu_recipe_delete).isVisible = delete
        if (!edit) {
            binding!!.buttonRecipeViewIngredientAdd.visibility = View.VISIBLE
        } else {
            binding!!.buttonRecipeViewIngredientAdd.visibility = View.GONE
        }
    }

    private fun loadData() {
        recipe = recipeViewModel.getSelectedRecipe().value
        user = userViewModel!!.user.value
        if (recipe == null) {
            recipe = Recipe()
        }
    }

    private fun showData() {
        binding!!.editTextRecipeViewName.setText(recipe.getName())
        if (StringUtils.isNotEmpty(recipe.getImage())) {
            Glide
                .with(requireContext())
                .load(recipe.getImage())
                .into(binding!!.imageViewRecipeViewImage)
        } else {
            binding!!.imageViewRecipeViewImage.setImageDrawable(resources.getDrawable(R.drawable.image_recipe_default))
        }
        binding!!.textViewRecipeViewUsername.text = recipe.getAuthor()
        binding!!.textViewRecipeViewForksQuantity.text = Integer.toString(recipe.getForks())
        binding!!.editTextRecipeViewDescription.setText(recipe.getDescription())
        if (recipe.getAuthor() == user.getLogin()) {
            binding!!.imageViewRecipeViewIconBookmarks.visibility = View.INVISIBLE
        } else {
            toggleBookmarks(user.getRecipesBookmarked().contains(recipe.getIdentifier()))
        }
        toggleForks(user.getRecipesForked().contains(recipe.getIdentifier()))
    }

    private fun setupAdapter() {
        val recyclerView = binding!!.rvRecipeViewIngredients
        adapter = ViewRecipeIngredientAdapter(binding!!.root.context, recipe.getIngredients(), this)
        recyclerView.adapter = adapter
    }

    private fun toggleBookmarks(check: Boolean) {
        if (check) {
            binding!!.imageViewRecipeViewIconBookmarks.setImageResource(R.drawable.icon_bookmarks_checked)
            binding!!.imageViewRecipeViewIconBookmarks.tag = RecipeUtils.TAG_BOOKMARKS_CHECKED
        } else {
            binding!!.imageViewRecipeViewIconBookmarks.setImageResource(R.drawable.icon_bookmarks_unchecked)
            binding!!.imageViewRecipeViewIconBookmarks.tag = RecipeUtils.TAG_BOOKMARKS_UNCHECKED
        }
    }

    private fun toggleForks(selected: Boolean) {
        if (selected) {
            binding!!.imageViewRecipeViewFork.setImageResource(R.drawable.icon_fork_checked)
            binding!!.imageViewRecipeViewFork.tag = RecipeUtils.TAG_FORK_CHECKED
        } else {
            binding!!.imageViewRecipeViewFork.setImageResource(R.drawable.icon_fork_unchecked)
            binding!!.imageViewRecipeViewFork.tag = RecipeUtils.TAG_FORK_UNCHECKED
        }
    }

    override fun onDeleteIngredientClick(recipeIngredient: RecipeIngredient?, position: Int) {
        recipe.getIngredients().remove(recipeIngredient)
        adapter!!.setData(recipe.getIngredients())
        adapter!!.notifyItemRemoved(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeViewBinding.inflate(inflater, container, false)
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