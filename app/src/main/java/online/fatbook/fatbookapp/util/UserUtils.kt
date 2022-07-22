package online.fatbook.fatbookapp.util

import java.text.SimpleDateFormat
import java.util.*

object UserUtils {
    const val FILL_ADDITIONAL_INFO = "fill_additional_info"
    const val TAG_USER = "user"
    const val TAG_FAT = "fat"
    val regDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    const val APP_PREFS = "app_prefs"
    const val USER_LOGIN = "user_pid"
}