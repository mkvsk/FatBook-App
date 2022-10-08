package online.fatbook.fatbookapp.core.user

import java.io.Serializable
import java.time.Duration
import java.time.LocalDateTime

data class UserSimpleObject(
    var pid: Long? = null,
    var username: String? = "",
    var title: String? = "",
    var profileImage: String? = "",
    var lastAction: String? = ""
) : Serializable {

    val online: Boolean
        get() = Duration.between(LocalDateTime.parse(lastAction), LocalDateTime.now()).toMinutes() < 10

}