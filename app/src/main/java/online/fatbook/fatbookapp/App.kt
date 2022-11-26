package online.fatbook.fatbookapp

import android.app.Application
import online.fatbook.fatbookapp.util.ContextHolder

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ContextHolder.set(this)
    }
}