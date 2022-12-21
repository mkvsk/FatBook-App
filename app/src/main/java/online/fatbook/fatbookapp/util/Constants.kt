package online.fatbook.fatbookapp.util

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import online.fatbook.fatbookapp.R
import java.util.*

object Constants {
    const val FEED_TAG = "feed_tag"
    const val SYMBOL_AT = '@'
    const val PASSWORD_REGEX = "^(?=\\S+\$).{6,20}$"
    const val USERNAME_REGEX = "^(?=.{3,20}\$)(?![_.])(?!.*[_.]{2})[a-z0-9._]+(?<![_.])\$"
    const val USER_PRETTY_WEBSITE_REGEX = "^(http[s]?://www\\.|http[s]?://|www\\.)"

    const val REFRESH_TOKEN = "refresh_token"
    const val SP_TAG = "shared_preferences"
    const val SP_TAG_PASSWORD = "password"
    const val SP_TAG_USERNAME = "username"
    const val SP_TAG_DARK_MODE = "dark_mode"
    const val SP_TAG_DARK_MODE_CHANGED = "dark_mode_changed"
    const val SP_TAG_BACK_STACK = "back_stack"
    const val SP_TAG_CLOSE_RECIPE_CREATE = "close_recipe_create"

    const val APP_PREFS = "app_prefs"
    const val USER_LOGIN = "user_pid"

    const val TAG_SELECT_ALL_BUTTON = "select_all_button"
//    const val ET_BIO_REGEX = "^(?=\\S+\$).{0,635}$"

    const val MAX_PORTIONS: Int = 20
    const val MIN_PORTIONS: Int = 1

    val MEDIA_TYPE_OCTET_STREAM = "application/octet-stream".toMediaTypeOrNull()

    const val TYPE_RECIPE = "r"
    const val TYPE_USER = "u"

    private val RANDOM: Random = Random()

    const val CDN_FB_BASE_URL = "https://cdn.fatbook.online/"

    fun getImageName(type: String, step: Int): String {
        return if (type == TYPE_RECIPE && step != 0) {
            "step$step/${String.format("%s%s", System.currentTimeMillis(), RANDOM.nextInt(10))}"
        } else {
            String.format("%s%s", System.currentTimeMillis(), RANDOM.nextInt(10))
        }
    }

    fun getUserImageName(): String {
        return String.format("%s%s", System.currentTimeMillis(), RANDOM.nextInt(10))
    }

    fun getRecipeImageName(step: Int): String {
        return if (step == 0) {
            String.format("%s%s", System.currentTimeMillis(), RANDOM.nextInt(10))
        } else {
            "step$step/${String.format("%s%s", System.currentTimeMillis(), RANDOM.nextInt(10))}"
        }
    }

    val rootDestinations = setOf(
            R.id.feed_dest,
            R.id.search_dest,
            R.id.recipe_first_stage_dest,
            R.id.notification_dest,
            R.id.user_profile_dest
    )
}