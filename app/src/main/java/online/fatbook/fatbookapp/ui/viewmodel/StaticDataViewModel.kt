package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.repository.StaticDataRepository
import online.fatbook.fatbookapp.util.ContextHolder

class StaticDataViewModel : ViewModel() {

    val ingredients = MutableLiveData<List<Ingredient>>()
    val cookingMethods = MutableLiveData<List<CookingMethod>>()
    val cookingCategories = MutableLiveData<List<CookingCategory>>()

    val loadCookingMethod = MutableLiveData<Boolean>()

    private val repository by lazy { StaticDataRepository(ContextHolder.get()) }

    fun getAllCookingMethods(callback: ResultCallback<List<CookingMethod>>) {
        repository.getAllCookingMethods(object : ResultCallback<List<CookingMethod>> {
            override fun onResult(value: List<CookingMethod>?) {
                value?.let {
                    callback.onResult(it)
                }
            }

            override fun onFailure(value: List<CookingMethod>?) {
                callback.onFailure(value)
            }
        })
    }

    fun getAllCookingCategories(callback: ResultCallback<List<CookingCategory>>) {
        repository.getAllCookingCategories(object : ResultCallback<List<CookingCategory>> {
            override fun onResult(value: List<CookingCategory>?) {
                value?.let {
                    callback.onResult(it)
                }
            }

            override fun onFailure(value: List<CookingCategory>?) {
                callback.onFailure(value)
            }
        })
    }

    fun getAllIngredients(callback: ResultCallback<List<Ingredient>>) {
        repository.getAllIngredients(object : ResultCallback<List<Ingredient>> {
            override fun onResult(value: List<Ingredient>?) {
                value?.let {
                    callback.onResult(it)
                }
            }

            override fun onFailure(value: List<Ingredient>?) {
                callback.onFailure(value)
            }

        })
    }

}