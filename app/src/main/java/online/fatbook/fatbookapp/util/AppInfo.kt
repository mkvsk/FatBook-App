package online.fatbook.fatbookapp.util

import android.os.Build
import android.os.LocaleList
import android.provider.Settings
import java.util.*


object AppInfo {

    val appName = ContextHolder.get().applicationName()
    val appVersion = getVersionName()
    val deviceType = getDeviceName()
    val deviceId: String =
        Settings.Secure.getString(ContextHolder.get().contentResolver, Settings.Secure.ANDROID_ID)
    val osVersion = Build.VERSION.RELEASE
    val acceptLanguage = getLanguage()

    const val osType = "Android"

    private fun getVersionName(): String {
        return ContextHolder.get().packageManager.getPackageInfo(
            ContextHolder.get().packageName,
            0
        ).versionName
    }

    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.lowercase(Locale.getDefault())
                .startsWith(manufacturer.lowercase(Locale.getDefault()))
        ) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }


    private fun capitalize(s: String?): String {
        if (s == null || s.isEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            first.uppercaseChar().toString() + s.substring(1)
        }
    }

    private fun getLanguage(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getDefault().toLanguageTags()
        } else {
            Locale.getDefault().language
        }
    }

}