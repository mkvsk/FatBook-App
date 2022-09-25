package online.fatbook.fatbookapp.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.authentication.AuthenticationRequest
import online.fatbook.fatbookapp.core.authentication.AuthenticationResponse
import online.fatbook.fatbookapp.core.authentication.LoginResponse
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class AuthenticationRepository(private val context: Context) {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    fun usernameCheck(username: String, callback: ResultCallback<Boolean>) {
        scope.launch {
            val call = RetrofitFactory.apiServiceClient().usernameCheck(username)

            call.enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    Log.d("USERNAME CHECK", response.body().toString())
                    callback.onResult(response.body())
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.d("USERNAME CHECK", "error")
                    t.printStackTrace()
                }
            })
        }
    }

    fun emailCheck(email: String, callback: ResultCallback<AuthenticationResponse>) {
        scope.launch {
            val call = RetrofitFactory.apiServiceClient().emailCheck(email)

            call.enqueue(object : Callback<AuthenticationResponse> {
                override fun onResponse(
                    call: Call<AuthenticationResponse>, response: Response<AuthenticationResponse>
                ) {
                    Log.d("EMAIL CHECK", response.body().toString())
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                    Log.d("EMAIL CHECK", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }

    fun login(request: RequestBody, callback: ResultCallback<LoginResponse>) {
        scope.launch {
            val call = RetrofitFactory.apiServiceClient().login(request)

            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>, response: Response<LoginResponse>
                ) {
                    Log.d("LOGIN", response.body().toString())
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.d("LOGIN", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }

    fun register(request: AuthenticationRequest, callback: ResultCallback<AuthenticationResponse>) {
        scope.launch {
            val call = RetrofitFactory.apiServiceClient().register(request)

            call.enqueue(object : Callback<AuthenticationResponse> {
                override fun onResponse(
                    call: Call<AuthenticationResponse>, response: Response<AuthenticationResponse>
                ) {
                    Log.d("REGISTER", response.body().toString())
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                    Log.d("REGISTER", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }

    fun confirmVCode(
        vCode: String, email: String, callback: ResultCallback<AuthenticationResponse>
    ) {
        scope.launch {
            val call = RetrofitFactory.apiServiceClient().confirmVCode(email, vCode)

            call.enqueue(object : Callback<AuthenticationResponse> {
                override fun onResponse(
                    call: Call<AuthenticationResponse>, response: Response<AuthenticationResponse>
                ) {
                    Log.d("VCODE CONFIRM", response.body().toString())
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                    Log.d("VCODE CONFIRM", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }
}