package online.fatbook.fatbookapp.core.recipe

import android.os.Parcel
import android.os.Parcelable
import online.fatbook.fatbookapp.core.user.UserSimpleObject
import online.fatbook.fatbookapp.util.AppInfo
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
) : Parcelable {

    val ingredientsStr: String
        get() = when (AppInfo.locale.language.uppercase()) {
            Locale.RU.name -> ingredientsLocalizedMap[Locale.RU] ?: ""
            Locale.EN.name -> ingredientsLocalizedMap[Locale.EN] ?: ""
            else -> ""
        }

    constructor(parcel: Parcel) : this(
        pid = parcel.readValue(Long::class.java.classLoader) as? Long,
        title = parcel.readString(),
        user = parcel.readValue(UserSimpleObject::class.java.classLoader) as? UserSimpleObject,
        image = parcel.readString(),
        forks = parcel.readValue(Int::class.java.classLoader) as? Int,
        createDate = parcel.readString(),
        identifier = parcel.readValue(Long::class.java.classLoader) as? Long,
        difficulty = parcel.readValue(CookingDifficulty::class.java.classLoader) as? CookingDifficulty,
        cookingTime = parcel.readString(),
        kcalPerPortion = parcel.readValue(Double::class.java.classLoader) as? Double,
        commentQty = parcel.readValue(Int::class.java.classLoader) as? Int,
        ingredientQty = parcel.readValue(Int::class.java.classLoader) as? Int,
        isPrivate = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        ingredientsLocalizedMap = parcel.readBundle(Map::class.java.classLoader) as Map<Locale, String>
//        (parcel.readValue(Map::class.java.classLoader) as? EnumMap<Locale, String>)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(pid)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeValue(forks)
        parcel.writeString(createDate)
        parcel.writeValue(identifier)
        parcel.writeString(cookingTime)
        parcel.writeValue(kcalPerPortion)
        parcel.writeValue(commentQty)
        parcel.writeValue(ingredientQty)
        parcel.writeValue(isPrivate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecipeSimpleObject> {
        override fun createFromParcel(parcel: Parcel): RecipeSimpleObject {
            return RecipeSimpleObject(parcel)
        }

        override fun newArray(size: Int): Array<RecipeSimpleObject?> {
            return arrayOfNulls(size)
        }
    }
}

