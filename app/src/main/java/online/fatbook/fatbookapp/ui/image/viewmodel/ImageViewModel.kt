package online.fatbook.fatbookapp.ui.image.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.RequestBody
import online.fatbook.fatbookapp.network.callback.ResultCallback
import online.fatbook.fatbookapp.ui.image.repository.ImageServiceRepository
import java.io.File

class ImageViewModel : ViewModel() {

    companion object {
        private const val TAG = "ImageViewModel"
    }

    private val repository by lazy { ImageServiceRepository() }

    private val _image = MutableLiveData<Any?>()
    val image: LiveData<Any?> get() = _image

    fun setImage(value: Any?) {
        _image.value = value
    }

    private val _userImageToUpload = MutableLiveData<Any?>()
    val userImageToUpload: LiveData<Any?> get() = _userImageToUpload

    fun setUserImageToUpload(value: Any?) {
        _userImageToUpload.value = value
    }

    private val _userImageToDelete = MutableLiveData<String?>()
    val userImageToDelete: LiveData<String?> get() = _userImageToDelete

    fun setUserImageToDelete(value: String?) {
        _userImageToDelete.value = value
    }

    private val _recipeStepImages = MutableLiveData<HashMap<Int, File>?>()
    val recipeStepImages: LiveData<HashMap<Int, File>?> get() = _recipeStepImages

    fun setNewRecipeStepImages(value: HashMap<Int, File>?) {
        _recipeStepImages.value = value
    }

    private val _recipeImageToUpload = MutableLiveData<File?>()
    val recipeImageToUpload: LiveData<File?> get() = _recipeImageToUpload

    fun setNewRecipeImage(value: File?) {
        _recipeImageToUpload.value = value
    }

    private val _imagesToUploadAmount = MutableLiveData<Int>()
    val imagesToUploadAmount: LiveData<Int> get() = _imagesToUploadAmount

    fun setImagesToUploadAmount(value: Int) {
        _imagesToUploadAmount.value = value
    }

    fun upload(url: String, body: RequestBody, callback: ResultCallback<String>) {
        repository.upload(url, body, object : ResultCallback<String> {
            override fun onResult(value: String?) {
                callback.onResult(value)
            }

            override fun onFailure(value: String?) {
                callback.onFailure(value)
            }
        })
    }

    fun delete(url: String, callback: ResultCallback<Boolean>) {
        repository.delete(url, object : ResultCallback<Boolean> {
            override fun onResult(value: Boolean?) {
                callback.onResult(value)
            }

            override fun onFailure(value: Boolean?) {
                callback.onFailure(value)
            }
        })
    }

    fun uploadRecipeImages(images: HashMap<Int, Pair<String, RequestBody?>>, id: String, callback: ResultCallback<Pair<Int, String>>) {
        repository.uploadRecipeImages(images, id, object : ResultCallback<Pair<Int, String>> {
            override fun onResult(value: Pair<Int, String>?) {
                callback.onResult(value)
            }

            override fun onFailure(value: Pair<Int, String>?) {
                callback.onFailure(value)
            }
        })
    }
}