package online.fatbook.fatbookapp

import android.app.Application
import online.fatbook.fatbookapp.util.ContextHolder
import online.fatbook.fatbookapp.util.ProgressBarUtil

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ContextHolder.set(this)

    }
}