package online.fatbook.fatbookapp.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingDifficulty
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.core.recipe.ingredient.unit.IngredientUnit
import online.fatbook.fatbookapp.network.service.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class StaticDataRepository {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    companion object {
        const val TAG = "StaticDataRepository"
    }

    fun getAllCookingMethods(callback: ResultCallback<List<CookingMethod>>) {
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiService().getAllCookingMethods()

            call.enqueue(object : Callback<List<CookingMethod>> {
                override fun onResponse(
                        call: Call<List<CookingMethod>>,
                        response: Response<List<CookingMethod>>
                ) {
                    Log.d(TAG, "GET COOKING METHODS ${response.body().toString()}")
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<List<CookingMethod>>, t: Throwable) {
                    Log.d(TAG, "GET COOKING METHODS error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }

    fun getAllCookingCategories(callback: ResultCallback<List<CookingCategory>>) {
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiService().getAllCookingCategories()

            call.enqueue(object : Callback<List<CookingCategory>> {
                override fun onResponse(
                        call: Call<List<CookingCategory>>,
                        response: Response<List<CookingCategory>>
                ) {
                    Log.d(TAG, "GET COOKING CATEGORIES ${response.body().toString()}")
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<List<CookingCategory>>, t: Throwable) {
                    Log.d(TAG, "GET COOKING CATEGORIES error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }

    fun getAllCookingDifficulties(callback: ResultCallback<List<CookingDifficulty>>) {
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiService().getAllCookingDifficulties()

            call.enqueue(object : Callback<List<CookingDifficulty>> {
                override fun onResponse(
                        call: Call<List<CookingDifficulty>>,
                        response: Response<List<CookingDifficulty>>
                ) {
                    Log.d(TAG, "GET COOKING DIFFICULTIES ${response.body().toString()}")
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<List<CookingDifficulty>>, t: Throwable) {
                    Log.d(TAG, "GET COOKING DIFFICULTIES error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }

    fun getAllIngredients(callback: ResultCallback<List<Ingredient>>) {
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiService().getAllIngredients()

            call.enqueue(object : Callback<List<Ingredient>> {
                override fun onResponse(
                        call: Call<List<Ingredient>>,
                        response: Response<List<Ingredient>>
                ) {
                    Log.d(TAG, "GET ALL INGREDIENTS ${response.body().toString()}")
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<List<Ingredient>>, t: Throwable) {
                    Log.d(TAG, "GET ALL INGREDIENTS error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }

    fun getAllIngredientUnits(callback: ResultCallback<List<IngredientUnit>>) {
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiService().getAllIngredientUnits()

            call.enqueue(object : Callback<List<IngredientUnit>> {
                override fun onResponse(call: Call<List<IngredientUnit>>, response: Response<List<IngredientUnit>>) {
                    Log.d(TAG, "GET ALL UNITS ${response.body().toString()}")
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<List<IngredientUnit>>, t: Throwable) {
                    Log.d(TAG, "GET ALL UNITS error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }
}