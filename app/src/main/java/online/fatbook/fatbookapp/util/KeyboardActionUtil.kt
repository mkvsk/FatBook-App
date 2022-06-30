package online.fatbook.fatbookapp.util

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import online.fatbook.fatbookapp.R

class KeyboardActionUtil {
    private val view: View
    private val activity: Activity
    private var isKeyboardVisible = false
    private var viewToHide: View? = null
    val listenerForAdjustResize = OnGlobalLayoutListener {
        val rectangle = Rect()
        val contentView = view
        contentView.getWindowVisibleDisplayFrame(rectangle)
        val screenHeight = contentView.rootView.height
        val keypadHeight = screenHeight - rectangle.bottom
        val isKeyboardNowVisible = keypadHeight > screenHeight * 0.15
        if (isKeyboardVisible != isKeyboardNowVisible) {
            if (isKeyboardNowVisible) {
                activity.findViewById<View>(R.id.bottom_navigation).visibility = View.GONE
            } else {
                activity.findViewById<View>(R.id.bottom_navigation).visibility = View.VISIBLE
            }
        }
        isKeyboardVisible = isKeyboardNowVisible
    }
    val listenerForAdjustPan = OnGlobalLayoutListener {
        val rectangle = Rect()
        val contentView = view
        contentView.getWindowVisibleDisplayFrame(rectangle)
        val screenHeight = contentView.rootView.height
        val keypadHeight = screenHeight - rectangle.bottom
        val isKeyboardNowVisible = keypadHeight > screenHeight * 0.15
        if (isKeyboardVisible != isKeyboardNowVisible) {
            if (isKeyboardNowVisible) {
                viewToHide!!.visibility = View.VISIBLE
            } else {
                viewToHide!!.visibility = View.GONE
            }
        }
        isKeyboardVisible = isKeyboardNowVisible
    }

    constructor(view: View, activity: Activity) {
        this.view = view
        this.activity = activity
    }

    constructor(view: View, activity: Activity, viewToHide: View?) {
        this.view = view
        this.activity = activity
        this.viewToHide = viewToHide
    }
}