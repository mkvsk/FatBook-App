package online.fatbook.fatbookapp.util

import android.app.Application
import android.content.Context
import android.util.Log

object ContextHolder {
    private lateinit var context: Application

    fun get(): Context {
        return context
    }

    fun set(context: Application) {
        this.context = context
    }
}