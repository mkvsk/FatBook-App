package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.MultipartBody
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.DeleteRequest
import online.fatbook.fatbookapp.repository.ImageServiceRepository

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

    fun uploadImage(
        file: MultipartBody.Part?,
        type: String,
        id: String,
        step: String,
        callback: ResultCallback<String>
    ) {
        repository.upload(file, type, id, step, object : ResultCallback<String> {
            override fun onResult(value: String?) {
                callback.onResult(value)
            }

            override fun onFailure(value: String?) {
                callback.onFailure(value)
            }
        })
    }

    fun deleteImage(request: DeleteRequest, callback: ResultCallback<Void>) {
        repository.delete(request, object : ResultCallback<Void> {
            override fun onResult(value: Void?) {
                callback.onResult(value)
            }

            override fun onFailure(value: Void?) {
                callback.onFailure(value)
            }
        })
    }
}