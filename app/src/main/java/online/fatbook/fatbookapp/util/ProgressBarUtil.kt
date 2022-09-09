package online.fatbook.fatbookapp.util

import android.view.View
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_main.*

object ProgressBarUtil {

    private lateinit var activity: FragmentActivity

    fun set(v: FragmentActivity) {
        activity = v
    }

    fun showProgressBar() {
        activity.progressbar_main.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        activity.progressbar_main.visibility = View.GONE
    }
}