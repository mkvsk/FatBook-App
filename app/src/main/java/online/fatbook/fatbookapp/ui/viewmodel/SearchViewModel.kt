package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingDifficulty
import online.fatbook.fatbookapp.core.recipe.CookingMethod

class SearchViewModel : ViewModel() {
    var selectedCategories = MutableLiveData<ArrayList<CookingCategory>>()
    var selectedMethods = MutableLiveData<ArrayList<CookingMethod>>()
    var selectedDifficulties = MutableLiveData<ArrayList<CookingDifficulty>>()

    var categories = MutableLiveData<ArrayList<CookingCategory>>()
    var methods = MutableLiveData<ArrayList<CookingMethod>>()
    var difficulties = MutableLiveData<ArrayList<CookingDifficulty>>()
}