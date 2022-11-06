package online.fatbook.fatbookapp.util

import android.content.Context
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.ingredient.IngredientUnit
import java.text.SimpleDateFormat
import java.util.*

object RecipeUtils {

    private lateinit var context: Context

    const val TAG_BOOKMARKS_CHECKED = "tag_bookmarks_checked"
    const val TAG_BOOKMARKS_UNCHECKED = "tag_bookmarks_unchecked"
    const val TAG_FORK_CHECKED = "tag_fork_checked"
    const val TAG_FORK_UNCHECKED = "tag_fork_unchecked"

    /**
     * Default cooking time in minutes
     */
    private const val defaultCookingTime = 15L
    const val defaultCookingTimeInMilliseconds = defaultCookingTime * 60 * 1000

    var dateFormat2 = SimpleDateFormat(
        "yyyy-MM-dd", Locale.US
    )

    fun setContext(context: Context) {
        this.context = context
    }

    fun getAllAvailableUnits(): ArrayList<IngredientUnit> {
        val list = ArrayList<IngredientUnit>()
        list.add(IngredientUnit.KG)
        list.add(IngredientUnit.GRAM)
        list.add(IngredientUnit.PCS)
        list.add(IngredientUnit.TEA_SPOON)
        list.add(IngredientUnit.TABLE_SPOON)
        list.add(IngredientUnit.ML)
        list.add(IngredientUnit.L)
        list.add(IngredientUnit.CUP)
        return list
    }

    fun prettyCookingTime(cookingTime: String?): String? {
        return if (cookingTime == null) {
            null
        } else {
            val date = FormatUtils.timeFormat.parse(cookingTime)
            val cal = Calendar.getInstance()
            var result = ""
            date?.let {
                cal.time = it
                val hours = cal.get(Calendar.HOUR_OF_DAY)
                val minutes = cal.get(Calendar.MINUTE)
                if (hours == 0 && minutes == 0) {
                    result = context.resources.getString(R.string.default_cooking_time)
                }
                if (hours != 0 && minutes != 0) {
                    result = String.format(
                        context.resources.getString(R.string.time_picker_format_h_min),
                        hours,
                        minutes
                    )
                }
                if (hours != 0 && minutes == 0) {
                    result = String.format(
                        context.resources.getString(R.string.time_picker_format_h),
                        hours,
                        minutes
                    )
                }
                if (hours == 0 && minutes != 0) {
                    result = String.format(
                        context.resources.getString(R.string.time_picker_format_min),
                        minutes
                    )
                }
            }
            result
        }
    }
}