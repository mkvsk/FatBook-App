package online.fatbook.fatbookapp.util

import android.content.Context
import online.fatbook.fatbookapp.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.OffsetDateTime
import java.util.*
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

object FormatUtils {

    var dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)

    var dateRecipeFormat = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.ENGLISH)

    var timeFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)

    private lateinit var context: Context
    private var currentDate: OffsetDateTime = OffsetDateTime.now()
    private var date: OffsetDateTime = OffsetDateTime.now()
    private var betweenMin: Long = 0
    private var betweenSec: Long = 0

    private final val RANDOM: Random = Random()

    fun setContext(context: Context) {
        this.context = context
    }

    fun prettyCount(number: Int): String {
        val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
        val numValue = number.toLong()
        val value = floor(log10(numValue.toDouble())).toInt()
        val base = value / 3
        return if (value >= 3 && base < suffix.size) {
            DecimalFormat("#0.00").format(
                    numValue / 10.0.pow((base * 3).toDouble())
            ) + suffix[base]
        } else {
            DecimalFormat().format(numValue)
        }
    }

    fun prettyCount(number: Double): String {
        val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
        val value = floor(log10(number)).toInt()
        val base = value / 3
        return if (value >= 3 && base < suffix.size) {
            DecimalFormat("#0.00").format(
                    number / 10.0.pow((base * 3).toDouble())
            ) + suffix[base]
        } else {
            DecimalFormat().format(number)
        }
    }

    fun getCreateDate(dateStr: String): String {
        date = OffsetDateTime.parse(dateStr)
        currentDate = OffsetDateTime.now()
        if (currentDate.year != date.year) {
            return String.format(
                    context.getString(R.string.date_format_d_m_y_h_m),
                    date.dayOfMonth,
                    context.resources.getStringArray(R.array.months)[date.month.ordinal].substring(0, 3),
                    date.year,
                    date.hour,
                    date.minute)
        }
        if (currentDate.dayOfYear - date.dayOfYear == 1) {
            return String.format(context.getString(R.string.date_format_yesterday_h_m), date.hour, date.minute)
        }
        if (currentDate.dayOfYear - date.dayOfYear > 1) {
            return String.format(
                    context.getString(R.string.date_format_d_m_h_m),
                    date.dayOfMonth,
                    context.resources.getStringArray(R.array.months)[date.month.ordinal].substring(0, 3),
                    date.hour,
                    date.minute)
        }
        if (currentDate.dayOfYear == date.dayOfYear) {
            betweenMin = Duration.between(date, currentDate).toMinutes()
            betweenSec = Duration.between(date, currentDate).toSeconds()
            if (betweenMin == 0L) {
                if (betweenSec == 0L) {
                    return context.getString(R.string.date_format_now)
                }
                when (betweenSec) {
                    1L -> return context.getString(R.string.date_format_1_sec_ago)
                    2L -> return context.getString(R.string.date_format_2_sec_ago)
                    3L -> return context.getString(R.string.date_format_3_sec_ago)
                }
                return if (betweenSec % 100 in 5..20) {
                    String.format(context.getString(R.string.date_format_25_sec_ago), betweenSec)
                } else if (betweenSec % 10 == 1L) {
                    String.format(context.getString(R.string.date_format_21_sec_ago), betweenSec)
                } else if (betweenSec % 10 in 2..4) {
                    String.format(context.getString(R.string.date_format_23_sec_ago), betweenSec)
                } else {
                    String.format(context.getString(R.string.date_format_25_sec_ago), betweenSec)
                }
            }
            if (betweenMin in 1..59) {
                when (betweenMin) {
                    1L -> return context.getString(R.string.date_format_1_min_ago)
                    2L -> return context.getString(R.string.date_format_2_min_ago)
                    3L -> return context.getString(R.string.date_format_3_min_ago)
                }
                return if (betweenMin % 100 in 5..20) {
                    String.format(context.getString(R.string.date_format_25_min_ago), betweenMin)
                } else if (betweenMin % 10 == 1L) {
                    String.format(context.getString(R.string.date_format_21_min_ago), betweenMin)
                } else if (betweenMin % 10 in 2..4) {
                    String.format(context.getString(R.string.date_format_23_min_ago), betweenMin)
                } else {
                    String.format(context.getString(R.string.date_format_25_min_ago), betweenMin)
                }
            }
            if (betweenMin in 60..119) {
                return context.getString(R.string.date_format_one_h_ago)
            }
            if (betweenMin in 120..179) {
                return context.getString(R.string.date_format_two_h_ago)
            }
            if (betweenMin in 180..239) {
                return context.getString(R.string.date_format_three_h_ago)
            }
        }
        return String.format(context.getString(R.string.date_format_today_h_m), date.hour, date.minute)
    }

    fun generateRecipeId(): Long {
        return String.format("%s%s", System.currentTimeMillis(), RANDOM.nextInt(10)).toLong()
    }
}