package online.fatbook.fatbookapp.core.recipe

import online.fatbook.fatbookapp.core.user.UserSimpleObject
import online.fatbook.fatbookapp.util.AppInfo
import java.io.Serializable
import java.util.*

data class RecipeSimpleObject(
    val pid: Long? = null,
    var title: String? = "",
    var user: UserSimpleObject? = null,
    var image: String? = "",
    var forks: Int? = 0,
    var createDate: String? = "",
    var identifier: Long? = null,
    var difficulty: CookingDifficulty? = null,
    var cookingTime: String? = "",
    var kcalPerPortion: Double? = 0.0,
    var commentQty: Int? = 0,
    var ingredientQty: Int? = 0,
    var isPrivate: Boolean? = false,
    var ingredientsLocalizedMap: Map<Locale, String> = EnumMap(Locale::class.java)
) : Serializable {

    val ingredientsStr: String
        get() = when (AppInfo.locale.language.uppercase()) {
            Locale.RU.name -> ingredientsLocalizedMap[Locale.RU] ?: ""
            Locale.EN.name -> ingredientsLocalizedMap[Locale.EN] ?: ""
            else -> ""
        }
}

