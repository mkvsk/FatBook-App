package online.fatbook.fatbookapp.core

import java.io.Serializable

class Ingredient() : Serializable {
    constructor(pid: Long?, name: String?) : this() {
        this.pid = pid
        this.name = name
    }

    var pid: Long? = null
    var name: String? = null
}