package online.fatbook.fatbookapp.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.User
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class UserRepository(private val context: Context) {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    fun getUserByUsername(username: String, callback: ResultCallback<User>) =
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiServiceClient().getUser(username)

            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    Log.d("LOAD USER", response.body().toString())
                    callback.onResult(response.body())
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.d("LOAD USER", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
}