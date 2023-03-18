package online.fatbook.fatbookapp.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.network.request.AuthenticationRequest
import online.fatbook.fatbookapp.network.response.AuthenticationResponse
import online.fatbook.fatbookapp.network.response.LoginResponse
import online.fatbook.fatbookapp.network.service.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class AuthenticationRepository {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    fun emailCheck(email: String, callback: ResultCallback<AuthenticationResponse>) {
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiService().emailCheck(email)

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
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiService().login(request)

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
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiService().register(request)

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
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiService().confirmVCode(email, vCode)

            call.enqueue(object : Callback<AuthenticationResponse> {
                override fun onResponse(
                    call: Call<AuthenticationResponse>, response: Response<AuthenticationResponse>
                ) {
                    Log.d("VCODE CONFIRMATION", response.body().toString())
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                    Log.d("VCODE CONFIRMATION", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }

    fun recoverPassword(identifier: String, callback: ResultCallback<AuthenticationResponse>) {
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiService().recoverPassword(identifier)

            call.enqueue(object : Callback<AuthenticationResponse> {
                override fun onResponse(
                    call: Call<AuthenticationResponse>, response: Response<AuthenticationResponse>
                ) {
                    Log.d("RECOVER", response.body().toString())
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                    Log.d("RECOVER", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }

    fun changePassword(
        username: String, password: String, callback: ResultCallback<AuthenticationResponse>
    ) {
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiService().changePassword(username, password)

            call.enqueue(object : Callback<AuthenticationResponse> {
                override fun onResponse(
                    call: Call<AuthenticationResponse>, response: Response<AuthenticationResponse>
                ) {
                    Log.d("CHANGE", response.body().toString())
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                    Log.d("CHANGE", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }
}