package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingDifficulty
import online.fatbook.fatbookapp.core.recipe.CookingMethod

class SearchViewModel : ViewModel() {
    var categories = MutableLiveData<ArrayList<CookingCategory>>()
    var methods = MutableLiveData<ArrayList<CookingMethod>>()
    var difficulties = MutableLiveData<ArrayList<CookingDifficulty>>()

    var selectedCategories = MutableLiveData<ArrayList<CookingCategory>>()
    var selectedmethods = MutableLiveData<ArrayList<CookingMethod>>()
    var selecteddifficulties = MutableLiveData<ArrayList<CookingDifficulty>>()
}