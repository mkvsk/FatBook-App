package online.fatbook.fatbookapp.network

import java.io.Serializable

data class AuthenticationResponse(
    val code: Int?,
    var message: String? = "",
    var vcode: String? = "",
    var email: String? = "",
    var username: String? = ""
) : Serializable {

    override fun toString(): String {
        return "{code = $code, message = $message, vCode = $vcode, email = $email, username = $username}"
    }
}