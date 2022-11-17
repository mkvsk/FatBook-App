package online.fatbook.fatbookapp.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class ImageServiceRepository {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    fun upload(file: MultipartBody.Part?, type: String, id: String, step: String, callback: ResultCallback<String>) =
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.imgService().imgUpload(file, type, id, step)

            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("USER IMAGE UPLOAD", response.body().toString())
                    if (response.code() == 200) {
                        callback.onResult(response.body())
                    } else {
                        callback.onFailure(null)
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("USER IMAGE UPLOAD", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }

}