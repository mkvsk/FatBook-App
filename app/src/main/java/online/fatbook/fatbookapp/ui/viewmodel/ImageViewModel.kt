package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.MultipartBody
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.DeleteRequest
import online.fatbook.fatbookapp.repository.ImageServiceRepository

class ImageViewModel : ViewModel() {

    private val repository by lazy { ImageServiceRepository() }

    var image = MutableLiveData<Any?>()

    var userImageToUpload = MutableLiveData<Any?>()
    var userImageToDelete = MutableLiveData<String?>()

    fun uploadImage(file: MultipartBody.Part?, type: String, id: String, step: String, callback: ResultCallback<String>) {
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
        repository.delete(request, object : ResultCallback<Void>{
            override fun onResult(value: Void?) {
                callback.onResult(value)
            }

            override fun onFailure(value: Void?) {
                callback.onFailure(value)
            }
        })
    }
}