package online.fatbook.fatbookapp.ui.adapters

import online.fatbook.fatbookapp.core.user.User

interface BindableAdapter <T> {
    fun setData(data: List<T>?)
    fun setData(data: List<T>?, user: User?) {}
}