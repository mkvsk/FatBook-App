package online.fatbook.fatbookapp.core.user

import java.io.Serializable

data class UserSimpleObject(
    var pid: Long? = null,
    var username: String? = "",
    var title: String? = "",
    var profileImage: String? = ""
) : Serializable