package online.fatbook.fatbookapp.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class UserRepository {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    fun getUserByUsername(username: String, callback: ResultCallback<User>) =
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiService().getUserByUsername(username)

            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    Log.d("USER LOAD", response.body().toString())
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.d("USER LOAD", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }

    fun updateUser(user: User, callback: ResultCallback<User>) {
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiService().updateUser(user)

            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    Log.d("USER UPDATE", response.body().toString())
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.d("USER UPDATE", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }

            })
        }
    }
}