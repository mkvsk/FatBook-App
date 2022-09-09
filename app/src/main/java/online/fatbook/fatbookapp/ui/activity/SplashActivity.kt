package online.fatbook.fatbookapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import online.fatbook.fatbookapp.databinding.ActivitySplashBinding
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
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

//        binding!!.buttonSplashRetry.setOnClickListener {
//            binding!!.textViewSplashError.visibility = View.GONE
//            binding!!.buttonSplashRetry.visibility = View.GONE
//            loadUserData(username!!)
//        }
    }

    private fun startMainScreen(launchFeed: Boolean) {
        val mainScreenIntent = Intent(this@SplashActivity, MainActivity::class.java)
        if (intent.action != null && intent.action != "android.intent.action.MAIN") {
            mainScreenIntent.action = intent.action
        }
        mainScreenIntent.putExtra(FEED_TAG, launchFeed)
        startActivity(mainScreenIntent)
        finish()
    }

    private fun loadSharedPreferences() {
        val sharedPreferences = getSharedPreferences(SP_TAG, MODE_PRIVATE)

        username = sharedPreferences.getString(SP_TAG_USERNAME, StringUtils.EMPTY)
        password = sharedPreferences.getString(SP_TAG_PASSWORD, StringUtils.EMPTY)

        Log.d("username", username!!)
        Log.d("password", password!!)

        val handler = Handler()
        handler.postDelayed({
            startMainScreen(StringUtils.isEmpty(username) || StringUtils.isEmpty(password))
        }, 1500)
    }

    private fun loadUserData(username: String) {
//        val call = RetrofitFactory.apiServiceClient().getUser(login)
//
//        call.enqueue(object : Callback<User> {
//            override fun onResponse(call: Call<User>, response: Response<User>) {
//                Log.d("LOAD USER", response.body().toString())
//                userViewModel.user.value = response.body()
//                callback.onResult(response.body())
//            }
//
//            override fun onFailure(call: Call<User>, t: Throwable) {
//                Log.d("LOAD USER", "error")
//                t.printStackTrace()
//                if (t.toString().contains("ConnectException")) {
//                    binding!!.textViewSplashError.text =
//                        getString(R.string.splash_no_connection_error_client)
//                } else {
//                    binding!!.textViewSplashError.text =
//                        getString(R.string.splash_no_connection_error_api)
//                }
//                binding!!.textViewSplashError.visibility = View.VISIBLE
//                binding!!.buttonSplashRetry.visibility = View.VISIBLE
//            }
//        })
    }
}