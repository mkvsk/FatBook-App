package online.fatbook.fatbookapp.util

import java.text.SimpleDateFormat
import java.util.*

object RecipeUtils {
    const val TAG_BOOKMARKS_CHECKED = "tag_bookmarks_checked"
    const val TAG_BOOKMARKS_UNCHECKED = "tag_bookmarks_unchecked"
    const val TAG_FORK_CHECKED = "tag_fork_checked"
    const val TAG_FORK_UNCHECKED = "tag_fork_unchecked"
    var regDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
    var dateFormat = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.US
    ) //    public String stripIngredientQuantity(double value) {
    //        if (StringUtils.endsWith(String.valueOf(value), "0")) {
    //
    //        }
    //    }
}