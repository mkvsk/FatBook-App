package online.fatbook.fatbookapp.util

object Constants {
    const val SYMBOL_AT = '@'
    const val PASSWORD_REGEX = "^(?=\\S+\$).{6,20}$"
    const val USERNAME_REGEX = "^[a-z0-9]([._-](?![._-])|[a-z0-9]){3,20}[a-z0-9]$"
    const val ACCESS_TOKEN = "access_token"
    const val REFRESH_TOKEN = "refresh_token"
    const val USERNAME = "username"
//    const val ET_BIO_REGEX = "^(?=\\S+\$)$"
}