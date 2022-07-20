package online.fatbook.fatbookapp.util

import java.util.regex.Pattern

object Constants {
    const val SYMBOL_AT = '@'

    const val PASSWORD_REGEX = "^[a-zA-Z0-9](?=\\\\S+\$).{6,20}$"

    const val USERNAME_REGEX = "^[a-z0-9]([._-](?![._-])|[a-z0-9]){3,20}[a-z0-9]$"

}