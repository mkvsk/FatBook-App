package online.fatbook.fatbookapp.core

import java.util.*

data class Token(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Int
) {
    var user: String = ""
    var dateCreation: Calendar? = null

    fun tokenWithType() = "$tokenType $accessToken"
}