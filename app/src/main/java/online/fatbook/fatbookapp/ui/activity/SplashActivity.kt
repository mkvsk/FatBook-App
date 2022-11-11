package online.fatbook.fatbookapp.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import online.fatbook.fatbookapp.databinding.ActivitySplashBinding
import online.fatbook.fatbookapp.ui.fragment.authentication.WelcomeFragment
import online.fatbook.fatbookapp.util.Constants
import online.fatbook.fatbookapp.util.Constants.FEED_TAG
import online.fatbook.fatbookapp.util.Constants.SP_TAG
import online.fatbook.fatbookapp.util.Constants.SP_TAG_DARK_MODE
import online.fatbook.fatbookapp.util.Constants.SP_TAG_PASSWORD
import online.fatbook.fatbookapp.util.Constants.SP_TAG_USERNAME
import org.apache.commons.lang3.StringUtils

class SplashActivity : AppCompatActivity() {
    private var binding: ActivitySplashBinding? = null
    private var username: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        loadSharedPreferences()
    }

    private fun loadSharedPreferences() {
        val sharedPreferences = getSharedPreferences(SP_TAG, MODE_PRIVATE)
        setDefaultNightMode(if (sharedPreferences.getBoolean(SP_TAG_DARK_MODE, false)) {
            MODE_NIGHT_YES
        } else {
            MODE_NIGHT_NO
        })

        username = sharedPreferences.getString(SP_TAG_USERNAME, StringUtils.EMPTY)
        password = sharedPreferences.getString(SP_TAG_PASSWORD, StringUtils.EMPTY)

        Log.d("username", username!!)
        Log.d("password", password!!)

        startMainScreen(username!!, password!!)
    }

    private fun startMainScreen(username: String, password: String) {
//        if (intent.action != null && intent.action != "android.intent.action.MAIN") {
//            mainScreenIntent.action = intent.action
//        }
        val intent: Intent
        if (!StringUtils.isEmpty(username) || !StringUtils.isEmpty(password)) {
            intent = Intent(this@SplashActivity, MainActivity::class.java)
            intent.putExtra(SP_TAG_USERNAME, username)
            intent.putExtra(SP_TAG_PASSWORD, password)
            if (this.intent.action != null && this.intent.action != "android.intent.action.MAIN") {
                intent.action = this.intent.action
            }

        } else {
            intent = Intent(this@SplashActivity, AuthenticationActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}