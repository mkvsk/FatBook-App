package online.fatbook.fatbookapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.core.recipe.Difficulty

class SearchViewModel : ViewModel() {
    var categories = MutableLiveData<List<CookingCategory>>()
    var methods = MutableLiveData<List<CookingMethod>>()
    var difficulty = MutableLiveData<List<Difficulty>>()
}