package online.fatbook.fatbookapp.callback

import java.io.Serializable

interface ResultCallback<T>: Serializable {
    fun onResult(value: T?)
}