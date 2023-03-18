package online.fatbook.fatbookapp.ui.staticdata.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.network.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingDifficulty
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.core.recipe.ingredient.unit.IngredientUnit
import online.fatbook.fatbookapp.ui.staticdata.repository.StaticDataRepository
import org.apache.commons.lang3.StringUtils

class StaticDataViewModel : ViewModel() {

    companion object {
        private const val TAG = "StaticDataViewModel"
    }

    private val repository by lazy { StaticDataRepository() }

    private val _ingredients = MutableLiveData<List<Ingredient>>()
    val ingredients: LiveData<List<Ingredient>> get() = _ingredients

    fun setIngredients(value: List<Ingredient>) {
        _ingredients.value = value
    }

    private val _cookingMethods = MutableLiveData<List<CookingMethod>>()
    val cookingMethods: LiveData<List<CookingMethod>> get() = _cookingMethods

    fun setCookingMethods(value: List<CookingMethod>) {
        _cookingMethods.value = value
    }

    private val _cookingCategories = MutableLiveData<List<CookingCategory>>()
    val cookingCategories: LiveData<List<CookingCategory>> get() = _cookingCategories

    fun setCookingCategories(value: List<CookingCategory>) {
        _cookingCategories.value = value
    }

    private val _cookingDifficulties = MutableLiveData<List<CookingDifficulty>>()
    val cookingDifficulties: LiveData<List<CookingDifficulty>> get() = _cookingDifficulties

    fun setCookingDifficulties(value: List<CookingDifficulty>) {
        _cookingDifficulties.value = value
    }

    private val _ingredientUnits = MutableLiveData<List<IngredientUnit>>()
    val ingredientUnits: LiveData<List<IngredientUnit>> get() = _ingredientUnits

    fun setIngredientUnits(value: List<IngredientUnit>) {
        _ingredientUnits.value = value
    }

    private val _unitG = MutableLiveData<IngredientUnit>()
    val unitG: LiveData<IngredientUnit> get() = _unitG

    fun setUnitG(value: IngredientUnit) {
        _unitG.value = value
    }

    private val _unitML = MutableLiveData<IngredientUnit>()
    val unitML: LiveData<IngredientUnit> get() = _unitML

    fun setUnitML(value: IngredientUnit) {
        _unitML.value = value
    }

    private val _methodOther = MutableLiveData<CookingMethod>()
    val methodOther: LiveData<CookingMethod> get() = _methodOther

    fun setMethodOther(value: CookingMethod) {
        _methodOther.value = value
    }

    private val _categoryOther = MutableLiveData<CookingCategory>()
    val categoryOther: LiveData<CookingCategory> get() = _categoryOther

    fun setCategoryOther(value: CookingCategory) {
        _categoryOther.value = value
    }

    private val _loadCookingMethod = MutableLiveData<Boolean>()
    val loadCookingMethod: LiveData<Boolean> get() = _loadCookingMethod

    fun setLoadCookingMethod(value: Boolean) {
        _loadCookingMethod.value = value
    }

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