package online.fatbook.fatbookapp.ui.search.repository

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import online.fatbook.fatbookapp.network.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject
import online.fatbook.fatbookapp.core.user.UserSimpleObject
import online.fatbook.fatbookapp.network.request.RecipeSearchRequest
import online.fatbook.fatbookapp.network.request.UserSearchRequest
import online.fatbook.fatbookapp.network.service.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class SearchRepository : ViewModel() {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    companion object {
        const val TAG = "SearchRepository"
    }

    fun searchRecipe(request: RecipeSearchRequest, callback: ResultCallback<List<RecipeSimpleObject>>) {
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiService().searchRecipe(request)

            call.enqueue(object : Callback<List<RecipeSimpleObject>> {
                override fun onResponse(call: Call<List<RecipeSimpleObject>>, response: Response<List<RecipeSimpleObject>>) {
                    Log.d(TAG, "SEARCH RECIPE ${response.body().toString()}")
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<List<RecipeSimpleObject>>, t: Throwable) {
                    Log.d(TAG, "SEARCH RECIPE error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }

    fun searchUser(request: UserSearchRequest, callback: ResultCallback<List<UserSimpleObject>>) {
        scope.launch(Dispatchers.IO) {
            val call = RetrofitFactory.apiService().searchUser(request)

            call.enqueue(object : Callback<List<UserSimpleObject>> {
                override fun onResponse(call: Call<List<UserSimpleObject>>, response: Response<List<UserSimpleObject>>) {
                    Log.d(TAG, "SEARCH USER ${response.body().toString()}")
                    if (response.body() == null) {
                        callback.onFailure(null)
                    } else {
                        callback.onResult(response.body())
                    }
                }

                override fun onFailure(call: Call<List<UserSimpleObject>>, t: Throwable) {
                    Log.d(TAG, "SEARCH USER error")
                    t.printStackTrace()
                    callback.onFailure(null)
                }
            })
        }
    }

}