package online.fatbook.fatbookapp.core

import java.io.Serializable

class SignInResponse(
    val access_token: String? = "",
    val refresh_token: String? = "",
) : Serializable {

    override fun toString(): String {
        return "{access_token = $access_token, refresh_token = $refresh_token}"
    }

}