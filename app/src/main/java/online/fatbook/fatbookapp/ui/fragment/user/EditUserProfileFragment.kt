package online.fatbook.fatbookapp.ui.fragment.user

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_user_profile.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.FragmentEditProfileBinding
import online.fatbook.fatbookapp.ui.viewmodel.ImageViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.FileUtils
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils
import java.io.File

class EditUserProfileFragment : Fragment() {

    private var binding: FragmentEditProfileBinding? = null

    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }
    private val imageViewModel by lazy { obtainViewModel(ImageViewModel::class.java) }

    private var bioTextLength: Int = 0
    private var chooseImageFromGallery: ActivityResultLauncher<String>? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        setupImageEditButtons()
        drawData(userViewModel.user.value!!)
        edittext_profile_title.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length == edittext_profile_title.filters.filterIsInstance<InputFilter.LengthFilter>()
                                .firstOrNull()?.max!!
                ) {
                    hideKeyboard(edittext_profile_title)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        edittext_profile_website.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        edittext_profile_bio.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                bioTextLength =
                        edittext_profile_bio.filters.filterIsInstance<InputFilter.LengthFilter>()
                                .firstOrNull()?.max!!
                bioTextLength -= s.toString().length
                textview_bio_length.text = bioTextLength.toString()
                if (bioTextLength == 0) {
                    hideKeyboard(edittext_profile_bio)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun setupMenu() {
        toolbar_edit_userprofile.inflateMenu(R.menu.edit_user_profile_menu)
        toolbar_edit_userprofile.setOnMenuItemClickListener(this::onOptionsItemSelected)
        toolbar_edit_userprofile.setNavigationOnClickListener {
            popBackStack()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit_user_profile_save -> {
                saveUserProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveUserProfile() {
        val user = userViewModel.user.value
        user?.let {
            it.title = edittext_profile_title.text.toString().replace("\\s+".toRegex(), " ")
            it.website = edittext_profile_website.text.toString().replace("\\s+".toRegex(), " ")
            it.bio = edittext_profile_bio.text.toString().replace("\\s+".toRegex(), " ")
        }
        hideKeyboard(edittext_profile_bio)
        Log.d(TAG, "saveUserProfile: ${imageViewModel.userImageToUpload.value}")
        Log.d(TAG, "saveUserProfile: ${imageViewModel.userImageToDelete.value}")
        Log.d(TAG, "saveUserProfile: ${userViewModel.user.value}")
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageViewModel.userImageToUpload.value!! as File)
        val fileName = "testImage"
        val file = MultipartBody.Part.createFormData("file", fileName, requestFile)
        imageViewModel.uploadImage(file, "u", userViewModel.user.value!!.username!!, "", object : ResultCallback<Any> {
            override fun onResult(value: Any?) {
                println()
            }

            override fun onFailure(value: Any?) {
                println()
            }
        })
//        popBackStack()
    }

    private fun drawData(user: User) {
        if (user.title.isNullOrEmpty()) {
            edittext_profile_title.setText(StringUtils.EMPTY)
        } else {
            edittext_profile_title.setText(user.title)
        }

        if (user.website.isNullOrEmpty()) {
            edittext_profile_website.setText(StringUtils.EMPTY)
        } else {
            edittext_profile_website.setText(user.website)
        }

        if (user.bio.isNullOrEmpty()) {
            edittext_profile_bio.setText(StringUtils.EMPTY)
        } else {
            edittext_profile_bio.setText(user.bio)
        }

        if (imageViewModel.userImageToUpload.value == null) {
            if (user.profileImage.isNullOrEmpty()) {
                toggleImageButtons(false)
                imageview_userphoto_edit_userprofile.setImageDrawable(requireContext().getDrawable(R.drawable.ic_default_userphoto))
            } else {
                toggleImageButtons(true)
                Glide.with(requireContext()).load(user.profileImage)
                        .into(imageview_userphoto_edit_userprofile)
            }
        } else {
            Glide.with(requireContext()).load(imageViewModel.userImageToUpload.value)
                    .into(imageview_userphoto_edit_userprofile)
        }
    }

    private fun setupImageEditButtons() {
        imageview_userphoto_edit_userprofile.setOnClickListener {
            imageViewModel.image.value = imageview_userphoto_edit_userprofile.drawable
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_image_view_from_edit_profile)
        }
        button_edit_photo.setOnClickListener {
            if (verifyStoragePermissions(requireActivity())) {
                chooseImageFromGallery!!.launch("image/*")
            }
        }
        button_delete_photo.setOnClickListener {
            imageViewModel.userImageToDelete.value = userViewModel.user.value!!.profileImage
            imageViewModel.userImageToUpload.value = null
            toggleImageButtons(false)
            Glide.with(requireContext()).load(R.drawable.ic_default_userphoto)
                    .into(imageview_userphoto_edit_userprofile)
        }
        chooseImageFromGallery =
                registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                    if (verifyStoragePermissions(requireActivity())) {
                        uri?.let {
                            toggleImageButtons(true)
                            val path = FileUtils.getPath(requireContext(), it)
                            imageViewModel.userImageToUpload.value = path?.let { file -> File(file) }
                            Glide.with(requireContext()).load(uri)
                                    .into(imageview_userphoto_edit_userprofile)
                        }
                    }
                }
    }

    private fun toggleImageButtons(isImageExists: Boolean) {
        if (isImageExists) {
            imageview_userphoto_edit_userprofile.isClickable = true
            button_edit_photo.setImageResource(R.drawable.ic_btn_edit)
            button_edit_photo.visibility = View.VISIBLE
            button_delete_photo.visibility = View.VISIBLE
        } else {
            imageview_userphoto_edit_userprofile.isClickable = false
            button_edit_photo.setImageResource(R.drawable.ic_btn_add)
            button_edit_photo.visibility = View.VISIBLE
            button_delete_photo.visibility = View.INVISIBLE
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
        private const val TAG = "EditUserProfileFragment"
    }

//    private fun handleBackPressed() {
//        requireActivity().onBackPressedDispatcher.addCallback(
//                viewLifecycleOwner,
//                object : OnBackPressedCallback(true) {
//                    override fun handleOnBackPressed() {
//                        if (progress_overlay.visibility == View.VISIBLE) {
//                            progress_overlay.visibility = View.GONE
////                        isReconnectCancelled = true
//                        } else {
//                            popBackStack()
//                        }
//                    }
//                })
//    }

    private fun popBackStack() {
        NavHostFragment.findNavController(this).popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        imageViewModel.userImageToDelete.value = null
        imageViewModel.userImageToUpload.value = null
        Log.d(TAG, "onDestroy: cleared images")
    }
}
