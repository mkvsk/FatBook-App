package online.fatbook.fatbookapp.repository

import android.util.Log
import androidx.annotation.RestrictTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.DeleteRequest
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class RecipeRepository {
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    fun recipeCreate(recipe: Recipe?, callback: ResultCallback<Recipe>) {
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiService().recipeCreate(recipe)

            call.enqueue(object : Callback<Recipe> {
                override fun onResponse(call: Call<Recipe>, response: Response<Recipe>) {
                    Log.d("RECIPE CREATE", response.body().toString())
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<Recipe>, t: Throwable) {
                    Log.d("RECIPE CREATE", "error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }

}