package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingDifficulty
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.core.recipe.ingredient.unit.IngredientUnit
import online.fatbook.fatbookapp.repository.StaticDataRepository
import org.apache.commons.lang3.StringUtils

class StaticDataViewModel : ViewModel() {

    val ingredients = MutableLiveData<List<Ingredient>>()
    val cookingMethods = MutableLiveData<List<CookingMethod>>()
    val cookingCategories = MutableLiveData<List<CookingCategory>>()
    val cookingDifficulties = MutableLiveData<List<CookingDifficulty>>()
    val ingredientUnits = MutableLiveData<List<IngredientUnit>>()

    val unitG = MutableLiveData<IngredientUnit>()
    val unitML = MutableLiveData<IngredientUnit>()
    val methodOther = MutableLiveData<CookingMethod>()
    val categoryOther = MutableLiveData<CookingCategory>()

    val loadCookingMethod = MutableLiveData<Boolean>()

    private val repository by lazy { StaticDataRepository() }

    fun getAllCookingMethods(callback: ResultCallback<List<CookingMethod>>) {
        repository.getAllCookingMethods(object : ResultCallback<List<CookingMethod>> {
            override fun onResult(value: List<CookingMethod>?) {
                callback.onResult(value)
            }

            override fun onFailure(value: List<CookingMethod>?) {
                callback.onFailure(value)
            }
        })
    }

    fun getAllCookingCategories(callback: ResultCallback<List<CookingCategory>>) {
        repository.getAllCookingCategories(object : ResultCallback<List<CookingCategory>> {
            override fun onResult(value: List<CookingCategory>?) {
                callback.onResult(value)
            }

            override fun onFailure(value: List<CookingCategory>?) {
                callback.onFailure(value)
            }
        })
    }

    fun getAllCookingDifficulties(callback: ResultCallback<List<CookingDifficulty>>) {
        repository.getAllCookingDifficulties(object : ResultCallback<List<CookingDifficulty>> {
            override fun onResult(value: List<CookingDifficulty>?) {
                callback.onResult(value)
            }

            override fun onFailure(value: List<CookingDifficulty>?) {
                callback.onFailure(value)
            }

        })
    }

    fun getAllIngredients(callback: ResultCallback<List<Ingredient>>) {
        repository.getAllIngredients(object : ResultCallback<List<Ingredient>> {
            override fun onResult(value: List<Ingredient>?) {
                callback.onResult(value)
            }

            override fun onFailure(value: List<Ingredient>?) {
                callback.onFailure(value)
            }

        })
    }

    fun getAllIngredientUnits(callback: ResultCallback<List<IngredientUnit>>) {
        repository.getAllIngredientUnits(object : ResultCallback<List<IngredientUnit>> {
            override fun onResult(value: List<IngredientUnit>?) {
                callback.onResult(value)
            }

            override fun onFailure(value: List<IngredientUnit>?) {
                callback.onFailure(value)
            }
        })
    }

    fun getOtherMethod(): CookingMethod? {
        var method: CookingMethod? = null
        cookingMethods.value?.let { list ->
            method = list.first { StringUtils.equalsIgnoreCase(it.title, "other") }
        }
        return method
    }

    fun getOtherCategory(): ArrayList<CookingCategory> {
        val categories: ArrayList<CookingCategory> = ArrayList()
        cookingCategories.value?.let { list ->
            val category = list.first {
                StringUtils.equalsIgnoreCase(it.title, "other")
            }
            category.let {
                categories.add(it)
            }
        }
        return categories
    }

}