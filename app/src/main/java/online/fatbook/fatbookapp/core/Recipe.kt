package online.fatbook.fatbookapp.core

import java.io.Serializable

class Recipe() : Serializable {

    constructor(name: String?) : this() {
        this.name = name
    }

    constructor(name: String?, forks: Int?) : this() {
        this.name = name
        this.forks = forks
    }

    constructor(name: String?, forks: Int?, author: String?) : this() {
        this.name = name
        this.forks = forks
        this.author = author
    }

    constructor(
        pid: Long?,
        name: String?,
        description: String?,
        author: String?,
        ingredients: ArrayList<RecipeIngredient>?,
        image: String?,
        forks: Int?,
        createDate: String?,
        identifier: Long?
    ) : this() {
        this.pid = pid
        this.name = name
        this.description = description
        this.author = author
        this.ingredients = ingredients
        this.image = image
        this.forks = forks
        this.createDate = createDate
        this.identifier = identifier
    }

    var pid: Long? = null
    var name: String? = ""
    var description: String? = ""
    var author: String? = null
    var ingredients: ArrayList<RecipeIngredient>? = ArrayList()
    var image: String? = ""
    var forks: Int? = 0
    var createDate: String? = ""
    var identifier: Long? = 0L
    var recipePortionFats: Double? = 0.0

    //новые поля
    //recipe step
}