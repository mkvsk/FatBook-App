package online.fatbook.fatbookapp.util

object Constants {
    const val FEED_TAG = "feed_tag"
    const val SYMBOL_AT = '@'
    const val PASSWORD_REGEX = "^(?=\\S+\$).{6,20}$"
    const val USERNAME_REGEX = "^[a-z0-9]([._-](?![._-])|[a-z0-9]){3,20}[a-z0-9]$"
    const val REFRESH_TOKEN = "refresh_token"
    const val SP_TAG = "shared_preferences"
    const val SP_TAG_PASSWORD = "password"
    const val SP_TAG_USERNAME = "username"
//    const val SP_TAG_DARK_MODE = false
//    const val ET_BIO_REGEX = "^(?=\\S+\$).{0,635}$"
}