package online.fatbook.fatbookapp.core.recipe

import android.os.Parcel
import android.os.Parcelable
import online.fatbook.fatbookapp.core.user.UserSimpleObject
import java.io.Serializable

data class RecipeComment(
    var pid: Long? = null,
    var user: UserSimpleObject? = null,
    var comment: String? = "",
    var timestamp: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(UserSimpleObject::class.java.classLoader) as? UserSimpleObject,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(pid)
        parcel.writeString(comment)
        parcel.writeString(timestamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecipeComment> {
        override fun createFromParcel(parcel: Parcel): RecipeComment {
            return RecipeComment(parcel)
        }

        override fun newArray(size: Int): Array<RecipeComment?> {
            return arrayOfNulls(size)
        }
    }
}