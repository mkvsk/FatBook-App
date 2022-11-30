package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingDifficulty
import online.fatbook.fatbookapp.core.recipe.CookingMethod

class SearchViewModel : ViewModel() {

    companion object {
        private const val TAG = "SearchViewModel"
    }

    private val _selectedCategories = MutableLiveData<ArrayList<CookingCategory>>()
    val selectedCategories: LiveData<ArrayList<CookingCategory>> get() = _selectedCategories

    fun setSelectedCategories(value: ArrayList<CookingCategory>) {
        _selectedCategories.value = value
    }

    private val _selectedMethods = MutableLiveData<ArrayList<CookingMethod>>()
    val selectedMethods: LiveData<ArrayList<CookingMethod>> get() = _selectedMethods

    fun setSelectedMethods(value: ArrayList<CookingMethod>) {
        _selectedMethods.value = value
    }

    private val _selectedDifficulties = MutableLiveData<ArrayList<CookingDifficulty>>()
    val selectedDifficulties: LiveData<ArrayList<CookingDifficulty>> get() = _selectedDifficulties

    fun setSelectedDifficulties(value: ArrayList<CookingDifficulty>) {
        _selectedDifficulties.value = value
    }

    private val _categories = MutableLiveData<ArrayList<CookingCategory>>()
    val categories: LiveData<ArrayList<CookingCategory>> get() = _categories

    fun setCategories(value: ArrayList<CookingCategory>) {
        _categories.value = value
    }

    private val _methods = MutableLiveData<ArrayList<CookingMethod>>()
    val methods: LiveData<ArrayList<CookingMethod>> get() = _methods

    fun setMethods(value: ArrayList<CookingMethod>) {
        _methods.value = value
    }

    private val _difficulties = MutableLiveData<ArrayList<CookingDifficulty>>()
    val difficulties: LiveData<ArrayList<CookingDifficulty>> get() = _difficulties

    fun setDifficulties(value: ArrayList<CookingDifficulty>) {
        _difficulties.value = value
    }

}