package online.fatbook.fatbookapp.core.user

import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject
import java.io.Serializable
import java.time.Duration
import java.time.LocalDateTime

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
    var role: UserRole? = UserRole.USER,
    var recipes: ArrayList<RecipeSimpleObject>? = ArrayList(),
    var recipesForked: ArrayList<RecipeSimpleObject>? = ArrayList(),
    var recipesFavourites: ArrayList<RecipeSimpleObject>? = ArrayList(),
    var listFollowing: ArrayList<UserSimpleObject>? = ArrayList(),
    var listFollowers: ArrayList<UserSimpleObject>? = ArrayList(),
    var recipeAmount: Int? = recipes?.size,
    var followingAmount: Int? = listFollowing?.size,
    var followersAmount: Int? = listFollowers?.size,
    var isSimpleObject: Boolean = recipes == null
) : Serializable {

    val online: Boolean
        get() = if (lastAction.isNullOrEmpty()) {
            false
        } else {
            Duration.between(LocalDateTime.parse(lastAction), LocalDateTime.now()).toMinutes() < 10
        }

    fun convertToSimpleObject(): UserSimpleObject {
        return UserSimpleObject(pid, username, title, profileImage)
    }
}

enum class UserRole {
    ADMIN,
    MOD,
    USER
}
