package online.fatbook.fatbookapp.util

import android.content.Context
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

object Utils {

    private lateinit var context: Context

    fun setContext(context: Context) {
        this.context = context
    }

    fun getCircularProgressDrawable(): CircularProgressDrawable {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        return circularProgressDrawable
    }

}