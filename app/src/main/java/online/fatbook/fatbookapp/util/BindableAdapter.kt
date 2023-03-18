package online.fatbook.fatbookapp.util

import online.fatbook.fatbookapp.core.user.User

interface BindableAdapter <T> {
    fun setData(data: List<T>?)
    fun setData(data: List<T>?, user: User?) {}
}