package online.fatbook.fatbookapp.core.user

import online.fatbook.fatbookapp.core.recipe.Recipe
import java.io.Serializable
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    var recipes: ArrayList<Recipe>? = ArrayList(),
    var recipesForked: ArrayList<Long>? = ArrayList(),
    var recipesFavourites: ArrayList<Long>? = ArrayList(),
    var listFollowing: ArrayList<UserSimpleObject>? = ArrayList(),
    var listFollowers: ArrayList<UserSimpleObject>? = ArrayList(),
    var recipeAmount: Int? = recipes?.size,
    var followingAmount: Int? = listFollowing?.size,
    var followersAmount: Int? = listFollowers?.size,
    var isSimpleObject: Boolean = recipes == null
) : Serializable {

    val online: Boolean
        get() = Duration.between(LocalDateTime.parse(lastAction), LocalDateTime.now()).toMinutes() < 10

    fun convertToSimpleObject(): UserSimpleObject {
        return UserSimpleObject(pid, username, title, profileImage)
    }
}

enum class UserRole {
    ADMIN,
    MOD,
    USER
}
