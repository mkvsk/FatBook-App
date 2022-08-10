package online.fatbook.fatbookapp.util

import android.text.InputFilter
import android.text.Spanned

class MyTextFilter : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence {
        for (i in start until end) {
            if (Character.isWhitespace(source!![i]) && dstart == 0) {
                return ""
            }
        }

        val stringDest = dest?.toString()
        if (source.toString() == " ") {
            if (stringDest.isNullOrEmpty()) {
                return ""
            } else if (dstart > 0 && stringDest[dstart - 1] == ' '
                || stringDest.length > dstart && stringDest[dstart] == ' '
                || dstart == 0
            ) {
                return ""
            }
        }
        return source!!.toString()
    }
}