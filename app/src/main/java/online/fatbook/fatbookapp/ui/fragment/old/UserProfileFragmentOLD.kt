package online.fatbook.fatbookapp.ui.fragment.old

import android.Manifest
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.PorterDuff
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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import lombok.extern.java.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.FragmentUserProfileOldBinding
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import online.fatbook.fatbookapp.ui.activity.SplashActivity
import online.fatbook.fatbookapp.ui.adapters.RecipeAdapter
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.Constants
import online.fatbook.fatbookapp.util.FileUtils
import online.fatbook.fatbookapp.util.KeyboardActionUtil
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

@Log
class UserProfileFragmentOLD : Fragment(), OnRecipeClickListener {
    private var binding: FragmentUserProfileOldBinding? = null
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
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        recipeViewModel = ViewModelProvider(requireActivity())[RecipeViewModel::class.java]
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
                    if (StringUtils.isEmpty(user!!.profileImage)) {
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
        binding!!.buttonUserProfileAddPhoto.setOnClickListener {
            verifyStoragePermissions(requireActivity())
            choosePhotoFromGallery!!.launch("image/*")
        }
        binding!!.buttonUserProfileChangePhoto.setOnClickListener {
            verifyStoragePermissions(requireActivity())
            choosePhotoFromGallery!!.launch("image/*")
        }
        binding!!.buttonUserProfileDeletePhoto.setOnClickListener {
            selectedImageUri = null
            userPhoto = null
            user!!.profileImage = (StringUtils.EMPTY)
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
        RetrofitFactory.apiService().getUserByUsername(user!!.username)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
//                    UserProfileFragment.log.log(
//                        Level.INFO,
//                        "" + response.code() + " found user: " + response.body()
//                    )
                    userViewModel!!.user.value = response.body()
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
//                    UserProfileFragment.log.log(Level.INFO, "load user ERROR")
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
        adapter = RecipeAdapter()
        adapter!!.setData(user!!.recipes, user)
        recyclerView.adapter = adapter
    }

    private fun fillUserProfile() {
        binding!!.toolbarUserProfile.title = user!!.username
        binding!!.editTextProfileName.setText(user!!.title)
        binding!!.editTextProfileBio.setText(user!!.bio)
        adapter!!.setData(user!!.recipes, user)
        adapter!!.notifyDataSetChanged()
    }

    private fun updateImage() {
        if (StringUtils.isNotEmpty(user!!.profileImage)) {
            Glide
                .with(
                    layoutInflater
                        .context
                )
                .load(user!!.profileImage)
                .into(binding!!.imageViewProfilePhoto)
            Glide
                .with(
                    layoutInflater
                        .context
                )
                .load(user!!.profileImage)
                .into(binding!!.imageViewUserProfilePhotoBgr)
        } else {
            binding!!.imageViewProfilePhoto.setImageDrawable(resources.getDrawable(R.drawable.ic_default_recipe_image))
            binding!!.imageViewUserProfilePhotoBgr.setImageDrawable(resources.getDrawable(R.drawable.user_profile_default_bgr))
        }
    }

    private fun setupMenu() {
        val activity = (activity as AppCompatActivity?)!!
        activity.setSupportActionBar(binding!!.toolbarUserProfile)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.user_profile_menu_old, menu)
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
            .setPositiveButton(getString(R.string.alert_dialog_btn_yes)) { _: DialogInterface?, _: Int ->
                val sharedPreferences = requireActivity().getSharedPreferences(
                    Constants.APP_PREFS,
                    Context.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                editor.putString(Constants.USER_LOGIN, StringUtils.EMPTY)
                editor.apply()
                startActivity(Intent(requireActivity(), SplashActivity::class.java))
                requireActivity().finish()
            }
            .setNegativeButton(getString(R.string.alert_dialog_btn_cancel)) { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
            .show()
    }

    private fun editMode(allow: Boolean) {
        changeMenuItemsVisibility(!allow, allow, allow)
        binding!!.editTextProfileName.isEnabled = allow
        binding!!.editTextProfileBio.isEnabled = allow
        if (allow) {
            binding!!.linearlayoutButtonsUserImage.visibility = View.VISIBLE
            if (userPhoto == null && StringUtils.isEmpty(user!!.profileImage)) {
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
        user!!.title = binding!!.editTextProfileName.text.toString()
        user!!.bio = binding!!.editTextProfileBio.text.toString()
        saveUser()
    }

    private fun saveUser() {
        RetrofitFactory.apiService().updateUser(user).enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                if (response.code() == 200) {
//                    UserProfileFragment.log.log(Level.INFO, "user update SUCCESS")
                    if (response.body() != null) {
//                        UserProfileFragment.log.log(Level.INFO, response.body().toString())
                    }
                    updateImage = false
                    userViewModel!!.user.value = response.body()
                    if (userPhoto != null) {
                        uploadImage()
                    }
                } else {
//                    UserProfileFragment.log.log(Level.INFO, "user update FAILED")
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
//                UserProfileFragment.log.log(Level.INFO, "user update FAILED")
            }
        })
    }

    private fun uploadImage() {
        try {
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), userPhoto!!)
            val fileName = "image" + userPhoto!!.name.substring(userPhoto!!.name.indexOf('.'))
            val file = MultipartBody.Part.createFormData("file", fileName, requestFile)
            RetrofitFactory.apiService()
                .uploadUserImage(file, FileUtils.TYPE_USER, user!!.username)
                .enqueue(object : Callback<User?> {
                    override fun onResponse(call: Call<User?>, response: Response<User?>) {
                        if (response.code() == 200) {
//                            UserProfileFragment.log.log(Level.INFO, "image add SUCCESS")
                            userViewModel!!.user.value = response.body()
                        } else {
//                            UserProfileFragment.log.log(
//                                Level.INFO,
//                                "image add FAILED " + response.code()
//                            )
                        }
                    }

                    override fun onFailure(call: Call<User?>, t: Throwable) {
//                        UserProfileFragment.log.log(Level.INFO, "image add FAILED")
                    }
                })
        } catch (e: Exception) {
//            UserProfileFragment.log.log(Level.INFO, "image add FAILED")
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
        val recipe = user!!.recipes!![position]
        recipeViewModel!!.selectedRecipe.value = recipe
        recipeViewModel!!.selectedRecipePosition.value = position
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_go_to_recipe_view_from_user_profile_old)
    }

    override fun onBookmarksClick(recipe: Recipe?, add: Boolean, adapterPosition: Int) {
        println("Stub!")
    }

    override fun onForkClicked(recipe: Recipe?, fork: Boolean, position: Int) {
        recipeForked(recipe, fork, position)
    }

    private fun recipeForked(recipe: Recipe?, fork: Boolean, position: Int) {
        RetrofitFactory.apiService().recipeForked(user!!.pid, recipe!!.pid, fork)
            .enqueue(object : Callback<Recipe?> {
                override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
//                    UserProfileFragment.log.log(Level.INFO, "fork SUCCESS")
                    recipeViewModel!!.selectedRecipe.value = response.body()
                    loadUser()
                }

                override fun onFailure(call: Call<Recipe?>, t: Throwable) {
//                    UserProfileFragment.log.log(Level.INFO, "fork FAILED")
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
    ): View {
        binding = FragmentUserProfileOldBinding.inflate(inflater, container, false)
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