package online.fatbook.fatbookapp.ui.user.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.RequestBody.Companion.asRequestBody
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.FragmentEditProfileBinding
import online.fatbook.fatbookapp.network.callback.ResultCallback
import online.fatbook.fatbookapp.network.request.UserUpdateRequest
import online.fatbook.fatbookapp.ui.image.viewmodel.ImageViewModel
import online.fatbook.fatbookapp.ui.user.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.Constants.CDN_FB_BASE_URL
import online.fatbook.fatbookapp.util.Constants.MEDIA_TYPE_OCTET_STREAM
import online.fatbook.fatbookapp.util.Constants.TYPE_USER
import online.fatbook.fatbookapp.util.Constants.getUserImageName
import online.fatbook.fatbookapp.util.FileUtils
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils
import java.io.File

class EditUserProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }
    private val imageViewModel by lazy { obtainViewModel(ImageViewModel::class.java) }

    private var bioTextLength: Int = 0
    private var chooseImageFromGallery: ActivityResultLauncher<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility =
            View.GONE
        setupMenu()
        setupImageEditButtons()
        drawData(userViewModel.user.value!!)
        binding.profileTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length == binding.profileTitle.filters.filterIsInstance<InputFilter.LengthFilter>()
                        .firstOrNull()?.max!!
                ) {
                    hideKeyboard(binding.profileTitle)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.profileWebsite.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        binding.profileBio.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                bioTextLength =
                    binding.profileBio.filters.filterIsInstance<InputFilter.LengthFilter>()
                        .firstOrNull()?.max!!
                bioTextLength -= s.toString().length
                binding.bioLength.text = bioTextLength.toString()
                if (bioTextLength == 0) {
                    hideKeyboard(binding.profileBio)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun setupMenu() {
        binding.toolbar.inflateMenu(R.menu.edit_user_profile_menu)
        binding.toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected)
        binding.toolbar.setNavigationOnClickListener {
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
        hideKeyboard(binding.profileBio)
        if (imageViewModel.userImageToUpload.value != null) {
            if (!userViewModel.user.value!!.profileImage.isNullOrEmpty()) {
                imageViewModel.setUserImageToDelete(userViewModel.user.value!!.profileImage)
                deleteImage(true)
            } else {
                uploadImage()
            }
        } else if (imageViewModel.userImageToDelete.value != null) {
            deleteImage(false)
        } else {
            updateUser()
        }
    }

    private fun uploadImage() {
        val imageNameWithType = with(imageViewModel.userImageToUpload.value) {
            this as File
            getUserImageName() + this.name.substring(this.name.indexOf('.'))
        }
        val url = "$TYPE_USER/${userViewModel.user.value!!.username}/$imageNameWithType"
        val body = imageViewModel.userImageToUpload.value.let {
            (it as File).asRequestBody(MEDIA_TYPE_OCTET_STREAM)
        }
        imageViewModel.upload(url, body, object : ResultCallback<String> {
            override fun onResult(value: String?) {
                userViewModel.user.value!!.profileImage = "$CDN_FB_BASE_URL$value"
                updateUser()
            }

            override fun onFailure(value: String?) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.title_error_image_upload),
                    Toast.LENGTH_LONG
                ).show()
                updateUser()
            }
        })
    }

    private fun deleteImage(uploadAfter: Boolean) {
        val url = "$TYPE_USER/${userViewModel.user.value!!.username}/"
        imageViewModel.delete(url, object : ResultCallback<Boolean> {
            override fun onResult(value: Boolean?) {
                userViewModel.user.value!!.profileImage = ""
                if (uploadAfter) {
                    uploadImage()
                } else {
                    updateUser()
                }
            }

            override fun onFailure(value: Boolean?) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.title_error_image_delete),
                    Toast.LENGTH_LONG
                ).show()
                updateUser()
            }
        })
    }

    private fun updateUser() {
        val request = UserUpdateRequest(
            userViewModel.user.value!!.username,
            binding.profileTitle.text.toString().replace("\\s+".toRegex(), " "),
            binding.profileWebsite.text.toString().replace("\\s+".toRegex(), " "),
            binding.profileBio.text.toString().replace("\\s+".toRegex(), " "),
            userViewModel.user.value!!.profileImage
        )
        userViewModel.updateUser(request, object : ResultCallback<User> {
            override fun onResult(value: User?) {
                userViewModel.setUser(value!!)
                popBackStack()
            }

            override fun onFailure(value: User?) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.title_error_user_update),
                    Toast.LENGTH_LONG
                ).show()
                popBackStack()
            }
        })
    }

    private fun drawData(user: User) {
        if (user.title.isNullOrEmpty()) {
            binding.profileTitle.setText(StringUtils.EMPTY)
        } else {
            binding.profileTitle.setText(user.title)
        }

        if (user.website.isNullOrEmpty()) {
            binding.profileWebsite.setText(StringUtils.EMPTY)
        } else {
            binding.profileWebsite.setText(user.website)
        }

        if (user.bio.isNullOrEmpty()) {
            binding.profileBio.setText(StringUtils.EMPTY)
            binding.bioLength.text =
                (binding.profileBio.filters.filterIsInstance<InputFilter.LengthFilter>()
                    .firstOrNull()?.max!!).toString()
        } else {
            binding.profileBio.setText(user.bio)
            binding.bioLength.text =
                (binding.profileBio.filters.filterIsInstance<InputFilter.LengthFilter>()
                    .firstOrNull()?.max!!).minus(
                        user.bio!!.length
                    ).toString()
        }

        if (imageViewModel.userImageToUpload.value == null) {
            toggleImageButtons(!user.profileImage.isNullOrEmpty())
            Glide
                .with(requireContext())
                .load(user.profileImage)
                .placeholder(requireContext().getDrawable(R.drawable.ic_default_userphoto))
                .into(binding.userPhoto)
        } else {
            toggleImageButtons(true)
            Glide
                .with(requireContext())
                .load(imageViewModel.userImageToUpload.value)
                .placeholder(requireContext().getDrawable(R.drawable.ic_default_userphoto))
                .into(binding.userPhoto)
        }
    }

    private fun setupImageEditButtons() {
        binding.userPhoto.setOnClickListener {
            imageViewModel.setImage(binding.userPhoto.drawable)
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_image_view_from_edit_profile)
        }
        binding.editPhoto.setOnClickListener {
            if (verifyStoragePermissions(requireActivity())) {
                chooseImageFromGallery!!.launch("image/*")
            }
        }
        binding.deletePhoto.setOnClickListener {
            imageViewModel.setUserImageToDelete(userViewModel.user.value!!.profileImage)
            imageViewModel.setUserImageToUpload(null)
            toggleImageButtons(false)
            binding.userPhoto.setImageDrawable(requireContext().getDrawable(R.drawable.ic_default_userphoto))
        }
        chooseImageFromGallery =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (verifyStoragePermissions(requireActivity())) {
                    uri?.let {
                        toggleImageButtons(true)
                        val path = FileUtils.getPath(requireContext(), it)
                        imageViewModel.setUserImageToUpload(path?.let { file -> File(file) })
                        Glide.with(requireContext()).load(uri).into(binding.userPhoto)
                    }
                }
            }
    }

    private fun toggleImageButtons(isImageExists: Boolean) {
        if (isImageExists) {
            binding.userPhoto.isClickable = true
            binding.editPhoto.setImageResource(R.drawable.ic_btn_edit)
            binding.editPhoto.visibility = View.VISIBLE
            binding.deletePhoto.visibility = View.VISIBLE
        } else {
            binding.userPhoto.isClickable = false
            binding.editPhoto.setImageResource(R.drawable.ic_btn_add)
            binding.editPhoto.visibility = View.VISIBLE
            binding.deletePhoto.visibility = View.INVISIBLE
        }
    }

    private fun verifyStoragePermissions(requireActivity: FragmentActivity): Boolean {
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
            return false
        } else {
            return true
        }
    }

    fun requestPermission() {
        val permissionsStorage = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        val requestExternalStorage = 1
        val permission = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsStorage,
                requestExternalStorage
            )
        }
    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        private const val TAG = "EditUserProfileFragment"
    }

    private fun popBackStack() {
        findNavController().popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        imageViewModel.setUserImageToDelete(null)
        imageViewModel.setUserImageToUpload(null)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility =
            View.VISIBLE
        _binding = null
    }

}
