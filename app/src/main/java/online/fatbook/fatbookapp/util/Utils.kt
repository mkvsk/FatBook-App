package online.fatbook.fatbookapp.util

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import online.fatbook.fatbookapp.R

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

    fun EditText.setupClearButtonWithAction() {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val clearIcon = if (editable?.isNotEmpty() == true) R.drawable.ic_clear_text else 0
                setCompoundDrawablesWithIntrinsicBounds(0, 0, clearIcon, 0)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })

        setOnTouchListener(View.OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (this.right - this.compoundPaddingRight)) {
                    this.setText("")
                    return@OnTouchListener true
                }
            }
            return@OnTouchListener false
        })
    }

    fun getCookingStepNumeralIcon(value: Int): Int {
        return when (value) {
            1 -> R.drawable.digit_1
            2 -> R.drawable.digit_2
            3 -> R.drawable.digit_3
            4 -> R.drawable.digit_4
            5 -> R.drawable.digit_5
            6 -> R.drawable.digit_6
            7 -> R.drawable.digit_7
            8 -> R.drawable.digit_8
            9 -> R.drawable.digit_9
            10 -> R.drawable.digit_10
            else -> R.drawable.digit_1
        }
    }
}