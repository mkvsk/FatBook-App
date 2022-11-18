package online.fatbook.fatbookapp.core

data class DeleteRequest(
        val type: String? = "",
        val id: String? = "",
        val step: String? = "",
        val deleteAll: Boolean? = false
)