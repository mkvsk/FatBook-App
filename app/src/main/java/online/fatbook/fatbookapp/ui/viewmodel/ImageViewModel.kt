package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.MultipartBody
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.repository.ImageServiceRepository

class ImageViewModel : ViewModel() {

    private val repository by lazy { ImageServiceRepository() }

    var image = MutableLiveData<Any?>()

    var userImageToUpload = MutableLiveData<Any?>()
    var userImageToDelete = MutableLiveData<String?>()

    fun uploadImage(file: MultipartBody.Part?, type: String, id: String, step: String, callback: ResultCallback<Any>) {
        repository.upload(file, type, id, step, object : ResultCallback<Any> {
            override fun onResult(value: Any?) {
                callback.onResult(value)
            }

            override fun onFailure(value: Any?) {
                callback.onFailure(value)
            }
        })
    }
}