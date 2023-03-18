package online.fatbook.fatbookapp.network.request

class UserUpdateRequest(
        var username: String? = "",
        var title: String? = "",
        var website: String? = "",
        var bio: String? = "",
        var image: String? = ""
)