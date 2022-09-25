package online.fatbook.fatbookapp.core.user

import online.fatbook.fatbookapp.core.recipe.Recipe
import java.io.Serializable

data class User(
    var pid: Long? = null,
    var username: String? = "",
    var bio: String? = "",
    var title: String? = "",
    var website: String? = "",
    var profileImage: String? = "",
    var email: String? = "",
    var regDate: String? = "",
    var lastAction: String? = "",
    var recipeAmount: Int? = 0,
    var followingAmount: Int? = 0,
    var followersAmount: Int? = 0,
    var role: UserRole? = UserRole.USER,
    var recipes: ArrayList<Recipe>? = ArrayList(),
    var recipesForked: ArrayList<Long>? = ArrayList(),
    var recipesFavourites: ArrayList<Long>? = ArrayList(),
    var listFollowing: ArrayList<UserSimpleObject>? = ArrayList(),
    var listFollowers: ArrayList<UserSimpleObject>? = ArrayList(),

    //TODO lastAction не более 5 минут назад
    var online: Boolean? = false
) : Serializable {

    fun convertToSimpleObject(): UserSimpleObject {
        return UserSimpleObject(pid, username, title, profileImage)
    }
}

enum class UserRole {
    ADMIN,
    MOD,
    USER
}
