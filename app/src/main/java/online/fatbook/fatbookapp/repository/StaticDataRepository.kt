package online.fatbook.fatbookapp.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredients
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class StaticDataRepository(private val context: Context) {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    fun getAllCookingMethods(callback: ResultCallback<List<CookingMethod>>) {
        scope.launch {
            val call = RetrofitFactory.apiServiceClient().getAllCookingMethods()

            call.enqueue(object : Callback<List<CookingMethod>> {
                override fun onResponse(
                    call: Call<List<CookingMethod>>,
                    response: Response<List<CookingMethod>>
                ) {
                    Log.d("GET COOKING METHODS", response.body().toString())
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<List<CookingMethod>>, t: Throwable) {
                    Log.d("GET COOKING METHODS", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }

    fun getAllCookingCategories(callback: ResultCallback<List<CookingCategory>>) {
        scope.launch {
            val call = RetrofitFactory.apiServiceClient().getAllCookingCategories()

            call.enqueue(object : Callback<List<CookingCategory>> {
                override fun onResponse(
                    call: Call<List<CookingCategory>>,
                    response: Response<List<CookingCategory>>
                ) {
                    Log.d("GET COOKING CATEGORIES", response.body().toString())
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<List<CookingCategory>>, t: Throwable) {
                    Log.d("GET COOKING CATEGORIES", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }

    fun getAllIngredients(callback: ResultCallback<List<Ingredients>>) {
        scope.launch {
            val call = RetrofitFactory.apiServiceClient().getAllIngredients()

            call.enqueue(object : Callback<List<Ingredients>> {
                override fun onResponse(
                    call: Call<List<Ingredients>>,
                    response: Response<List<Ingredients>>
                ) {
                    Log.d("GET ALL INGREDIENTS", response.body().toString())
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<List<Ingredients>>, t: Throwable) {
                    Log.d("GET ALL INGREDIENTS", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }


}