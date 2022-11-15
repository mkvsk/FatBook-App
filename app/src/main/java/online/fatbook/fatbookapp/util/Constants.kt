package online.fatbook.fatbookapp.util

import online.fatbook.fatbookapp.R

object Constants {
    const val FEED_TAG = "feed_tag"
    const val SYMBOL_AT = '@'
    const val PASSWORD_REGEX = "^(?=\\S+\$).{6,20}$"
    const val USERNAME_REGEX = "^[a-z0-9]([._-](?![._-])|[a-z0-9]){3,20}[a-z0-9]$"
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

//    const val ET_BIO_REGEX = "^(?=\\S+\$).{0,635}$"

    val rootDestinations = setOf(R.id.feed_dest, R.id.search_dest, R.id.recipe_first_stage_dest, R.id.notification_dest, R.id.user_profile_dest)
}