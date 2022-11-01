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

//    const val ET_BIO_REGEX = "^(?=\\S+\$).{0,635}$"

    val rootDestinations = setOf(R.id.feed_dest, R.id.search_dest, R.id.recipe_create_first_stage_dest, R.id.notification_dest, R.id.user_profile_dest)
}