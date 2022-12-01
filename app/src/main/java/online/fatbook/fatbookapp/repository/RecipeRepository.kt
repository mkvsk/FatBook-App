package online.fatbook.fatbookapp.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.network.service.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class RecipeRepository {
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    fun recipeCreate(recipe: Recipe, callback: ResultCallback<Void>) {
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiService().recipeCreate(recipe)

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d("RECIPE CREATE", response.body().toString())
                    if (response.code() != 200) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("RECIPE CREATE", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }

    fun getRecipeById(id: Long, callback: ResultCallback<Recipe>) {
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiService().getRecipeById(id)

            call.enqueue(object : Callback<Recipe> {
                override fun onResponse(call: Call<Recipe>, response: Response<Recipe>) {
                    Log.d("SELECTED RECIPE LOAD", response.body().toString())
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<Recipe>, t: Throwable) {
                    Log.d("SELECTED RECIPE LOAD", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }

}