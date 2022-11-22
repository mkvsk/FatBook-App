package online.fatbook.fatbookapp.core.recipe

import online.fatbook.fatbookapp.util.AppInfo
import java.io.Serializable
import java.util.*

data class RecipeSimpleObject(
        val pid: Long? = null,
        var title: String? = "",
        var author: String? = null,
        var image: String? = "",
        var forks: Int? = 0,
        var createDate: String? = "",
        var identifier: Long? = null,
        var difficulty: CookingDifficulty? = null,
        var cookingTime: String? = "",
        var kcalPerPortion: Double? = 0.0,
        var commentsQtt: Int? = 0,
        var ingredientsLocalizedMap: Map<Locale, String> = EnumMap(Locale::class.java)
) : Serializable {

    val ingredientsStr: String
        get() = when (AppInfo.locale.language) {
            Locale.RU.name -> ingredientsLocalizedMap[Locale.RU] ?: ""
            Locale.ENG.name -> ingredientsLocalizedMap[Locale.ENG] ?: ""
            else -> ""
        }

    fun convertToRecipe(): Recipe {
        return Recipe(pid, title, author, image, forks, createDate, identifier, difficulty)
    }
}

