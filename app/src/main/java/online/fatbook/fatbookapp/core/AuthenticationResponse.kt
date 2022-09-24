package online.fatbook.fatbookapp.core

import java.io.Serializable

data class AuthenticationResponse(
    val code: Int?,
    var message: String? = "",
    var vcode: String? = "",
    var email: String? = "",
    var username: String? = ""
) : Serializable {

    /**
     * 401 - api error
     */

    /**
     * 402 - internet connection error
     */

    override fun toString(): String {
        return "{code = $code, message = $message, vCode = $vcode, email = $email, username = $username}"
    }
}