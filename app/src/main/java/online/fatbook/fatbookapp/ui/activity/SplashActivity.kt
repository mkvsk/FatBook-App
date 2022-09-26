package online.fatbook.fatbookapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import online.fatbook.fatbookapp.databinding.ActivitySplashBinding
import online.fatbook.fatbookapp.util.Constants.FEED_TAG
import online.fatbook.fatbookapp.util.Constants.SP_TAG
import online.fatbook.fatbookapp.util.Constants.SP_TAG_USERNAME
import online.fatbook.fatbookapp.util.Constants.SP_TAG_PASSWORD
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

    private fun startMainScreen(username: String, password: String) {
        val mainScreenIntent = Intent(this@SplashActivity, MainActivity::class.java)
        if (intent.action != null && intent.action != "android.intent.action.MAIN") {
            mainScreenIntent.action = intent.action
        }
        if (!StringUtils.isEmpty(username) || !StringUtils.isEmpty(password)) {
            mainScreenIntent.putExtra(FEED_TAG, true)
            mainScreenIntent.putExtra(SP_TAG_USERNAME, username)
            mainScreenIntent.putExtra(SP_TAG_PASSWORD, password)
        } else {
            mainScreenIntent.putExtra(FEED_TAG, false)
        }
        startActivity(mainScreenIntent)
        finish()
    }

    private fun loadSharedPreferences() {
        val sharedPreferences = getSharedPreferences(SP_TAG, MODE_PRIVATE)

        username = sharedPreferences.getString(SP_TAG_USERNAME, StringUtils.EMPTY)
        password = sharedPreferences.getString(SP_TAG_PASSWORD, StringUtils.EMPTY)

        Log.d("username", username!!)
        Log.d("password", password!!)

        startMainScreen(username!!, password!!)
//        val handler = Handler()
//        handler.postDelayed({
//            startMainScreen(username!!, password!!)
//        }, 1500)
    }
}