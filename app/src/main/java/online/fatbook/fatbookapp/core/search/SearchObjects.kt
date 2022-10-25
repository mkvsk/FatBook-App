package online.fatbook.fatbookapp.core.search

import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.core.recipe.Difficulty
import java.io.Serializable

data class SearchObjects(
    var categories: List<CookingCategory?> = ArrayList(),
    var methods: List<CookingMethod?> = ArrayList(),
    var difficulty: List<Difficulty?> = ArrayList()
) : Serializable
