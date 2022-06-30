package online.fatbook.fatbookapp.ui.activity.fill_additional_info

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.get
import online.fatbook.fatbookapp.core.User
import online.fatbook.fatbookapp.databinding.ActivityFillAdditionalInfoBinding
import online.fatbook.fatbookapp.util.FileUtils
import online.fatbook.fatbookapp.util.UserUtils
import java.io.File

class FillAdditionalInfoActivity : AppCompatActivity() {
    private var binding: ActivityFillAdditionalInfoBinding? = null
    private var choosePhotoFromGallery: ActivityResultLauncher<String>? = null
    private var user: User? = null
    private var userProfilePhoto: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFillAdditionalInfoBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        val viewModel = ViewModelProvider(this).get(
            FillAdditionalInfoViewModel::class.java
        )
        val introduceIntent = intent
        user = introduceIntent.getSerializableExtra(UserUtils.TAG_USER) as User?
        binding!!.buttonFillAddSave.setOnClickListener { view: View? ->
            user.setName(binding!!.editTextFillAddName.text.toString())
            //            user.setBirthday();
            user.setBio(binding!!.editTextFillAddBio.text.toString())
            viewModel.saveUser(binding!!.root, user, userProfilePhoto)
        }
        choosePhotoFromGallery = registerForActivityResult(
            GetContent()
        ) { uri: Uri ->
            verifyStoragePermissions(this)
            val path = FileUtils.getPath(this, uri)
            userProfilePhoto = File(path)
            binding!!.imagebuttonFillAddPhoto.setImageURI(uri)
        }
        binding!!.imagebuttonFillAddPhoto.setOnClickListener { view: View? ->
            verifyStoragePermissions(this)
            choosePhotoFromGallery!!.launch("image/*")
        }
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