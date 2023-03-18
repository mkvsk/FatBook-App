package online.fatbook.fatbookapp.network.request

import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingDifficulty
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import java.io.Serializable

class SearchRequest(
    var searchString: String = "",
    var methods: ArrayList<CookingMethod> = ArrayList(),
    var categories: ArrayList<CookingCategory> = ArrayList(),
    var difficulties: ArrayList<CookingDifficulty> = ArrayList(),
    var kcal: Int = 0
) : Serializable {

    override fun toString(): String {
        return "{methods: $methods, categories: $categories, difficulties: $difficulties, kcal: $kcal"
    }

}