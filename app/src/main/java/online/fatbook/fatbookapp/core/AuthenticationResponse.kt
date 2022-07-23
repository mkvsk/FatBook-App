package online.fatbook.fatbookapp.core

import java.io.Serializable

class AuthenticationResponse : Serializable {

    var code: Int? = null
    var message: String? = null
    var vcode: String? = null
    var email: String? = null
    var username: String? = null

    override fun toString(): String {
        return "{code = $code, message = $message, vCode = $vcode, email = $email, username = $username}"
    }
}