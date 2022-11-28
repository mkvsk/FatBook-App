package online.fatbook.fatbookapp.network

class EditUserRequest(
    var username: String? = "",
    var title: String? = "",
    var website: String? = "",
    var bio: String? = "",
    var image: String? = ""
)