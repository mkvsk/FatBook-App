package online.fatbook.fatbookapp.network

import java.io.Serializable

class LoginResponse(
    val access_token: String? = "",
    val refresh_token: String? = "",
    val username: String? = ""
) : Serializable {

    override fun toString(): String {
        return "{access_token = $access_token, refresh_token = $refresh_token, username = $username}"
    }

}