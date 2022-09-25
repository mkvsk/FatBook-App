package online.fatbook.fatbookapp.core.authentication

import java.io.Serializable

data class AuthenticationRequest(
    val username: String? = "",
    val password: String? = "",
    val email: String? = ""
) : Serializable {

    override fun toString(): String {
        return "{username = $username, password = $password, email = $email}"
    }
}