package online.fatbook.fatbookapp.util.touchview

import android.view.MotionEvent
import android.view.View

interface OnTouchImageViewListener {
    fun onMove()
    fun onMove(v: View, event: MotionEvent)
}