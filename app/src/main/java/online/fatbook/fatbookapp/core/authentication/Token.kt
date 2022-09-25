package online.fatbook.fatbookapp.core.authentication

import java.io.Serializable
import java.util.*

data class Token(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Int
) : Serializable {
    var user: String = ""
    var dateCreation: Calendar? = null

    fun tokenWithType() = "$tokenType $accessToken"
}