package online.fatbook.fatbookapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.ActivitySplashBinding
import online.fatbook.fatbookapp.util.Constants.SP_TAG
import online.fatbook.fatbookapp.util.Constants.SP_TAG_DARK_MODE
import online.fatbook.fatbookapp.util.Constants.SP_TAG_PASSWORD
import online.fatbook.fatbookapp.util.Constants.SP_TAG_USERNAME
import org.apache.commons.lang3.StringUtils

class SplashActivity : AppCompatActivity() {
    private var _binding: ActivitySplashBinding? = null
    private val binding get() = _binding!!

    private var username: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        login()
    }

    private fun login() {
        //login
        username
        password
        val user = User()
        startMainScreen(user)
    }

    private fun startMainScreen(user: User) {

    }

    private fun startMainScreen(username: String, password: String) {
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}