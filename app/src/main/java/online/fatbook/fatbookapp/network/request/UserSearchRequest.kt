package online.fatbook.fatbookapp.network.request

import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingDifficulty
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import java.io.Serializable

class UserSearchRequest(
    var searchString: String = "",
) : Serializable {

    override fun toString(): String {
        return searchString
    }

}