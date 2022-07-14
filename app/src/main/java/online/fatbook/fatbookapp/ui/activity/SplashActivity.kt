package online.fatbook.fatbookapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import lombok.extern.java.Log
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.User
import online.fatbook.fatbookapp.databinding.ActivitySplashBinding
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.UserUtils
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.logging.Level

class SplashActivity : AppCompatActivity() {
    private var binding: ActivitySplashBinding? = null
    private var userViewModel: UserViewModel? = null
    private var userLogin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        val sharedPreferences = getSharedPreferences(UserUtils.APP_PREFS, MODE_PRIVATE)
        userLogin = sharedPreferences.getString(UserUtils.USER_LOGIN, StringUtils.EMPTY)
        if (StringUtils.isEmpty(userLogin)) {
            userViewModel!!.user.value = User()
        } else {
            loadUserData(userLogin)
        }
        userViewModel!!.user.observe(this) { user: User? ->
            val handler = Handler()
            handler.postDelayed({
                val intent: Intent = if (StringUtils.isEmpty(userLogin)) {
                    Intent(this, WelcomeActivity::class.java)
                } else {
                    Intent(this, MainActivity::class.java)
                }
                intent.putExtra(UserUtils.TAG_USER, user)
                startActivity(intent)
                finish()
            }, 1)
        }
        binding!!.buttonSplashRetry.setOnClickListener {
            binding!!.textViewSplashError.visibility = View.GONE
            binding!!.buttonSplashRetry.visibility = View.GONE
            loadUserData(userLogin)
        }
    }

    private fun loadUserData(login: String?) {
        RetrofitFactory.apiServiceClient().getUser(login).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
//                SplashActivity.log.log(
//                    Level.INFO,
//                    "" + response.code() + " found user: " + response.body()
//                )
                userViewModel!!.user.value = response.body()
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
//                SplashActivity.log.log(Level.INFO, "load user ERROR")
                if (t.toString().contains("ConnectException")) {
                    binding!!.textViewSplashError.text =
                        getString(R.string.splash_no_connection_error_client)
                } else {
                    binding!!.textViewSplashError.text =
                        getString(R.string.splash_no_connection_error_api)
                }
                binding!!.textViewSplashError.visibility = View.VISIBLE
                binding!!.buttonSplashRetry.visibility = View.VISIBLE
            }
        })
    }
}