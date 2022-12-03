package online.fatbook.fatbookapp.core.recipe

import java.io.File
import java.io.Serializable

data class CookingStep (
    var pid: Long? = null,
    var stepNumber: Int? = null,
    var description: String? = "",
    var image: String? = "",
    var imageFile: File? = null,
    var imageName: String? = ""
) : Serializable
