package online.fatbook.fatbookapp.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.lifecycle.ViewModelProvider
import okhttp3.MultipartBody
import okhttp3.RequestBody
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.ActivitySplashBinding
import online.fatbook.fatbookapp.network.LoginResponse
import online.fatbook.fatbookapp.network.service.RetrofitFactory
import online.fatbook.fatbookapp.repository.AuthenticationRepository
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.Constants.SP_TAG
import online.fatbook.fatbookapp.util.Constants.SP_TAG_DARK_MODE
import online.fatbook.fatbookapp.util.Constants.SP_TAG_PASSWORD
import online.fatbook.fatbookapp.util.Constants.SP_TAG_USERNAME
import org.apache.commons.lang3.StringUtils

class SplashActivity : AppCompatActivity() {
    private var _binding: ActivitySplashBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy { AuthenticationRepository() }

    private var username: String? = null
    private var password: String? = null

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadSharedPreferences()
    }

    private fun loadSharedPreferences() {
        sharedPreferences =
            getSharedPreferences(SP_TAG, MODE_PRIVATE)
        setDefaultNightMode(
            if (sharedPreferences.getBoolean(SP_TAG_DARK_MODE, false)) {
                MODE_NIGHT_YES
            } else {
                MODE_NIGHT_NO
            }
        )

        username = sharedPreferences.getString(SP_TAG_USERNAME, StringUtils.EMPTY)
        password = sharedPreferences.getString(SP_TAG_PASSWORD, StringUtils.EMPTY)
        login()
    }

    private fun login() {
        val request: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("username", username.toString())
            .addFormDataPart("password", password.toString()).build()
        repository.login(request, object : ResultCallback<LoginResponse> {
            override fun onResult(value: LoginResponse?) {
                if (value != null) {
                    RetrofitFactory.updateJWT(
                        value.access_token.toString(),
                        value.username.toString()
                    )
                    startMainScreen(value.username.toString(), password.toString())
                } else {
                    logout()
                }
            }

            override fun onFailure(value: LoginResponse?) {
                logout()
            }
        })
    }

    private fun logout() {
        sharedPreferences.edit().putString(SP_TAG_USERNAME, StringUtils.EMPTY)
        sharedPreferences.edit().putString(SP_TAG_PASSWORD, StringUtils.EMPTY)
        startMainScreen("", "")
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