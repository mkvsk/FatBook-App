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
import com.google.android.material.navigation.NavigationBarView
import online.fatbook.fatbookapp.ui.viewmodel.SignInViewModel
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.content.ContextCompat
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
import android.content.*
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
import android.net.Uri
import android.provider.OpenableColumns
import android.view.*
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import lombok.extern.java.Log
import okhttp3.MediaType
import online.fatbook.fatbookapp.core.*
import online.fatbook.fatbookapp.databinding.FragmentUserProfileBinding
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
import java.util.logging.Level

@Log
class UserProfileFragment : Fragment(), OnRecipeClickListener {
    private var binding: FragmentUserProfileBinding? = null
    private var user: User? = null
    private var userViewModel: UserViewModel? = null
    private var adapter: RecipeAdapter? = null
    private var choosePhotoFromGallery: ActivityResultLauncher<String>? = null
    private var userPhoto: File? = null
    private var selectedImageUri: Uri? = null
    private var recipeViewModel: RecipeViewModel? = null
    private var updateImage = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        (binding!!.toolbarUserProfile.layoutParams as AppBarLayout.LayoutParams).scrollFlags = 0
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        recipeViewModel = ViewModelProvider(requireActivity()).get(RecipeViewModel::class.java)
        setupMenu()
        user = userViewModel!!.user.value
        if (userViewModel!!.user.value == null) {
            loadUser()
        }
        userViewModel!!.user.observe(viewLifecycleOwner) { _user: User? ->
            binding!!.swipeRefreshUserProfile.isRefreshing = false
            user = _user
            fillUserProfile()
            if (updateImage) {
                updateImage()
            }
        }
        setupAdapter()
        setupSwipeRefresh()
        binding!!.toolbarUserProfile.overflowIcon!!
            .setColorFilter(
                resources.getColor(R.color.color_blue_grey_600),
                PorterDuff.Mode.MULTIPLY
            )
        try {
            choosePhotoFromGallery = registerForActivityResult(GetContent()) { uri: Uri? ->
                verifyStoragePermissions(requireActivity())
                if (uri != null) {
                    selectedImageUri = uri
                    val path = FileUtils.getPath(requireContext(), selectedImageUri!!)
                    userPhoto = File(path)
                    binding!!.imageViewProfilePhoto.setImageURI(selectedImageUri)
                    binding!!.imageViewUserProfilePhotoBgr.setImageURI(selectedImageUri)
                    binding!!.buttonUserProfileAddPhoto.visibility = View.GONE
                    binding!!.buttonUserProfileChangePhoto.visibility = View.VISIBLE
                    binding!!.buttonUserProfileDeletePhoto.visibility = View.VISIBLE
                } else {
                    if (StringUtils.isEmpty(user.getImage())) {
                        binding!!.buttonUserProfileAddPhoto.visibility = View.VISIBLE
                        binding!!.buttonUserProfileChangePhoto.visibility = View.GONE
                        binding!!.buttonUserProfileDeletePhoto.visibility = View.GONE
                    } else {
                        binding!!.buttonUserProfileAddPhoto.visibility = View.GONE
                        binding!!.buttonUserProfileChangePhoto.visibility = View.VISIBLE
                        binding!!.buttonUserProfileDeletePhoto.visibility = View.VISIBLE
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding!!.buttonUserProfileAddPhoto.setOnClickListener { view1: View? ->
            verifyStoragePermissions(requireActivity())
            choosePhotoFromGallery!!.launch("image/*")
        }
        binding!!.buttonUserProfileChangePhoto.setOnClickListener { view1: View? ->
            verifyStoragePermissions(requireActivity())
            choosePhotoFromGallery!!.launch("image/*")
        }
        binding!!.buttonUserProfileDeletePhoto.setOnClickListener { view1: View? ->
            selectedImageUri = null
            userPhoto = null
            user.setImage(StringUtils.EMPTY)
            fillUserProfile()
            if (updateImage) {
                updateImage()
            }
            binding!!.buttonUserProfileAddPhoto.visibility = View.VISIBLE
            binding!!.buttonUserProfileChangePhoto.visibility = View.GONE
            binding!!.buttonUserProfileDeletePhoto.visibility = View.GONE
        }
    }

    private fun verifyStoragePermissions(requireActivity: FragmentActivity) {
        val permission = ActivityCompat.checkSelfPermission(
            requireActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    private fun loadUser() {
        RetrofitFactory.apiServiceClient().getUser(user.getLogin())
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    UserProfileFragment.log.log(
                        Level.INFO,
                        "" + response.code() + " found user: " + response.body()
                    )
                    userViewModel!!.setUser(response.body())
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    UserProfileFragment.log.log(Level.INFO, "load user ERROR")
                }
            })
    }

    private fun setupSwipeRefresh() {
        editMode(false)
        binding!!.swipeRefreshUserProfile.setColorSchemeColors(resources.getColor(R.color.color_pink_a200))
        updateImage = true
        binding!!.swipeRefreshUserProfile.setOnRefreshListener { loadUser() }
    }

    private fun setupAdapter() {
        val recyclerView = binding!!.rvUserRecipe
        adapter = RecipeAdapter(binding!!.root.context, user.getRecipes(), user, this)
        recyclerView.adapter = adapter
    }

    private fun fillUserProfile() {
        binding!!.toolbarUserProfile.title = user.getLogin()
        binding!!.editTextProfileName.setText(user.getName())
        binding!!.editTextProfileBio.setText(user.getBio())
        adapter!!.setData(user.getRecipes(), user)
        adapter!!.notifyDataSetChanged()
    }

    private fun updateImage() {
        if (StringUtils.isNotEmpty(user.getImage())) {
            Glide
                .with(
                    layoutInflater
                        .context
                )
                .load(user.getImage())
                .into(binding!!.imageViewProfilePhoto)
            Glide
                .with(
                    layoutInflater
                        .context
                )
                .load(user.getImage())
                .into(binding!!.imageViewUserProfilePhotoBgr)
        } else {
            binding!!.imageViewProfilePhoto.setImageDrawable(resources.getDrawable(R.drawable.image_recipe_default))
            binding!!.imageViewUserProfilePhotoBgr.setImageDrawable(resources.getDrawable(R.drawable.user_profile_default_bgr))
        }
    }

    private fun setupMenu() {
        val activity = (activity as AppCompatActivity?)!!
        activity.setSupportActionBar(binding!!.toolbarUserProfile)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.user_profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_user_profile_edit -> {
                editMode(true)
                true
            }
            R.id.menu_user_profile_save -> {
                editMode(false)
                confirmEdit()
                true
            }
            R.id.menu_user_profile_cancel -> {
                editMode(false)
                revertEdit()
                true
            }
            R.id.menu_user_profile_app_info -> {
                showAppInfoDialog()
                true
            }
            R.id.menu_user_profile_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAppInfoDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.dialog_app_info)
        builder.setPositiveButton(getString(R.string.alert_dialog_btn_close)) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
        builder.show()
    }

    private fun logout() {
        val msg = resources.getString(R.string.alert_dialog_logout_message)
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
            LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog_title_logout, null)
        AlertDialog.Builder(requireContext()).setView(container).setCustomTitle(title)
            .setPositiveButton(getString(R.string.alert_dialog_btn_yes)) { dialogInterface: DialogInterface?, i: Int ->
                val sharedPreferences = requireActivity().getSharedPreferences(
                    UserUtils.APP_PREFS,
                    Context.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                editor.putString(UserUtils.USER_LOGIN, StringUtils.EMPTY)
                editor.apply()
                startActivity(Intent(requireActivity(), SplashActivity::class.java))
                requireActivity().finish()
            }
            .setNegativeButton(getString(R.string.alert_dialog_btn_cancel)) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
            .show()
    }

    private fun editMode(allow: Boolean) {
        changeMenuItemsVisibility(!allow, allow, allow)
        binding!!.editTextProfileName.isEnabled = allow
        binding!!.editTextProfileBio.isEnabled = allow
        if (allow) {
            binding!!.linearlayoutButtonsUserImage.visibility = View.VISIBLE
            if (userPhoto == null && StringUtils.isEmpty(user.getImage())) {
                binding!!.buttonUserProfileAddPhoto.visibility = View.VISIBLE
                binding!!.buttonUserProfileChangePhoto.visibility = View.GONE
                binding!!.buttonUserProfileDeletePhoto.visibility = View.GONE
            } else {
                binding!!.buttonUserProfileAddPhoto.visibility = View.GONE
                binding!!.buttonUserProfileChangePhoto.visibility = View.VISIBLE
                binding!!.buttonUserProfileDeletePhoto.visibility = View.VISIBLE
            }
            binding!!.imageViewUserProfilePhotoBgr.visibility = View.INVISIBLE
            binding!!.editTextProfileName.setBackgroundResource(R.drawable.edit_mode_bgr)
            binding!!.editTextProfileBio.setBackgroundResource(R.drawable.edit_mode_bgr)
        } else {
            binding!!.linearlayoutButtonsUserImage.visibility = View.GONE
            binding!!.imageViewUserProfilePhotoBgr.visibility = View.VISIBLE
            binding!!.editTextProfileName.setBackgroundResource(R.drawable.round_corner_rect_white)
            binding!!.editTextProfileBio.setBackgroundResource(R.drawable.round_corner_rect_white)
        }
    }

    private fun revertEdit() {
        loadUser()
        updateImage = true
    }

    private fun confirmEdit() {
        val strName = binding!!.editTextProfileName.text.toString()
        binding!!.editTextProfileName.setText(strName.trim { it <= ' ' })
        val strBio = binding!!.editTextProfileBio.text.toString()
        binding!!.editTextProfileBio.setText(strBio.replace("\n", " ").trim { it <= ' ' })
        user.setName(binding!!.editTextProfileName.text.toString())
        user.setBio(binding!!.editTextProfileBio.text.toString())
        saveUser()
    }

    private fun saveUser() {
        RetrofitFactory.apiServiceClient().userUpdate(user).enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                if (response.code() == 200) {
                    UserProfileFragment.log.log(Level.INFO, "user update SUCCESS")
                    if (response.body() != null) {
                        UserProfileFragment.log.log(Level.INFO, response.body().toString())
                    }
                    updateImage = false
                    userViewModel!!.setUser(response.body())
                    if (userPhoto != null) {
                        uploadImage()
                    }
                } else {
                    UserProfileFragment.log.log(Level.INFO, "user update FAILED")
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                UserProfileFragment.log.log(Level.INFO, "user update FAILED")
            }
        })
    }

    private fun uploadImage() {
        try {
            val requestFile = RequestBody.create(MediaType.parse("image/*"), userPhoto)
            val fileName = "image" + userPhoto!!.name.substring(userPhoto!!.name.indexOf('.'))
            val file = MultipartBody.Part.createFormData("file", fileName, requestFile)
            RetrofitFactory.apiServiceClient()
                .uploadUserImage(file, FileUtils.TAG_USER, user.getLogin())
                .enqueue(object : Callback<User?> {
                    override fun onResponse(call: Call<User?>, response: Response<User?>) {
                        if (response.code() == 200) {
                            UserProfileFragment.log.log(Level.INFO, "image add SUCCESS")
                            userViewModel!!.setUser(response.body())
                        } else {
                            UserProfileFragment.log.log(
                                Level.INFO,
                                "image add FAILED " + response.code()
                            )
                        }
                    }

                    override fun onFailure(call: Call<User?>, t: Throwable) {
                        UserProfileFragment.log.log(Level.INFO, "image add FAILED")
                    }
                })
        } catch (e: Exception) {
            UserProfileFragment.log.log(Level.INFO, "image add FAILED")
            e.printStackTrace()
        }
    }

    private fun changeMenuItemsVisibility(edit: Boolean, save: Boolean, cancel: Boolean) {
        if (binding!!.toolbarUserProfile.menu.findItem(R.id.menu_user_profile_edit) != null) {
            binding!!.toolbarUserProfile.menu.findItem(R.id.menu_user_profile_edit).isVisible = edit
            binding!!.toolbarUserProfile.menu.findItem(R.id.menu_user_profile_save).isVisible = save
            binding!!.toolbarUserProfile.menu.findItem(R.id.menu_user_profile_cancel).isVisible =
                cancel
        }
    }

    override fun onRecipeClick(position: Int) {
        val recipe = user.getRecipes()[position]
        recipeViewModel!!.setSelectedRecipe(recipe)
        recipeViewModel!!.setSelectedRecipePosition(position)
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_go_to_recipe_view_from_user_profile)
    }

    override fun onBookmarksClick(recipe: Recipe?, add: Boolean, adapterPosition: Int) {
        println("Stub!")
    }

    override fun onForkClicked(recipe: Recipe?, fork: Boolean, position: Int) {
        recipeForked(recipe, fork, position)
    }

    private fun recipeForked(recipe: Recipe?, fork: Boolean, position: Int) {
        RetrofitFactory.apiServiceClient().recipeForked(user.getPid(), recipe.getPid(), fork)
            .enqueue(object : Callback<Recipe?> {
                override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
                    UserProfileFragment.log.log(Level.INFO, "fork SUCCESS")
                    recipeViewModel!!.setSelectedRecipe(response.body())
                    loadUser()
                }

                override fun onFailure(call: Call<Recipe?>, t: Throwable) {
                    UserProfileFragment.log.log(Level.INFO, "fork FAILED")
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
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
            binding!!.imageViewProfilePhoto.setImageURI(selectedImageUri)
            binding!!.imageViewUserProfilePhotoBgr.setImageURI(selectedImageUri)
        }
    }

    override fun onPause() {
        super.onPause()
        binding!!.root.viewTreeObserver.removeOnGlobalLayoutListener(
            KeyboardActionUtil(
                binding!!.root, requireActivity()
            ).listenerForAdjustResize
        )
    } //    private void setupKeyboardOpenListener() {

    //        keyboardVisibilityListener = () -> {
    //            final Rect rectangle = new Rect();
    //            final View contentView = binding.getRoot();
    //            contentView.getWindowVisibleDisplayFrame(rectangle);
    //            int screenHeight = contentView.getRootView().getHeight();
    //            int keypadHeight = screenHeight - rectangle.bottom;
    //            boolean isKeyboardNowVisible = keypadHeight > screenHeight * 0.15;
    //
    //            if (isKeyboardVisible != isKeyboardNowVisible) {
    //                if (isKeyboardNowVisible) {
    //                    requireActivity().findViewById(R.id.bottom_navigation).setVisibility(View.GONE);
    //                } else {
    //                    requireActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
    //                }
    //            }
    //            isKeyboardVisible = isKeyboardNowVisible;
    //        };
    //    }
    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}