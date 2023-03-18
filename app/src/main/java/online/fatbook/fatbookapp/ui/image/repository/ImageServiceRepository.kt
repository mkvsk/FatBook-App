package online.fatbook.fatbookapp.ui.image.repository

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.RequestBody
import online.fatbook.fatbookapp.network.callback.ResultCallback
import online.fatbook.fatbookapp.network.service.RetrofitFactory
import online.fatbook.fatbookapp.util.Constants.TYPE_RECIPE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class ImageServiceRepository {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private lateinit var url: String

    companion object {
        const val TAG = "ImageServiceRepository"
    }

    fun upload(url: String, body: RequestBody, callback: ResultCallback<String>) {
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.cdnService().upload(url, body)

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d("$TAG IMAGE UPLOAD", response.code().toString())
                    if (response.code() == 201) {
                        callback.onResult(url)
                    } else {
                        callback.onFailure(null)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("$TAG IMAGE UPLOAD", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }

    fun delete(url: String, callback: ResultCallback<Boolean>) {
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.cdnService().delete(url)

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d("$TAG IMAGE DELETE", response.code().toString())
                    if (response.code() == 200 || response.code() == 201) {
                        callback.onResult(true)
                    } else {
                        callback.onFailure(null)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("$TAG IMAGE DELETE", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }


    fun uploadRecipeImages(images: HashMap<Int, Pair<String, RequestBody?>>, id: String, callback: ResultCallback<Pair<Int, String>>) {
        scope.launch {
            images.forEach {
                launch {
                    it.value.let { pair ->
                        pair.second?.let { body ->
                            url = "$TYPE_RECIPE/$id/${pair.first}"
                            RetrofitFactory.cdnService().upload(url, body).enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    Log.d("$TAG RECIPE IMAGE UPLOAD", response.code().toString())
                                    if (response.code() == 201) {
                                        callback.onResult(Pair(it.key, it.value.first))
                                    } else {
                                        callback.onFailure(Pair(it.key, it.value.first))
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    Log.d("$TAG RECIPE IMAGE UPLOAD", "error")
                                    t.printStackTrace()
                                    callback.onFailure(Pair(it.key, it.value.first))
                                }
                            })
                        }
                    }
                }
            }
        }
    }

}