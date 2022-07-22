package online.fatbook.fatbookapp.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.User
import online.fatbook.fatbookapp.databinding.ActivitySplashBinding
import online.fatbook.fatbookapp.repository.UserRepository
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.Constants.ACCESS_TOKEN
import online.fatbook.fatbookapp.util.ContextHolder
import online.fatbook.fatbookapp.util.UserUtils
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {
    private var binding: ActivitySplashBinding? = null
    private var username: String? = null
    private var accessToken: String? = null

    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        loadSharedPreferences()

//        userViewModel.user.observe(this) { user: User? ->
//            val handler = Handler()
//            handler.postDelayed({
//                val intent: Intent = if (StringUtils.isEmpty(username)) {
//                    Intent(this, WelcomeActivity::class.java)
//                } else {
//                    Intent(this, MainActivity::class.java)
//                }
//                intent.putExtra(UserUtils.TAG_USER, user)
//                startActivity(intent)
//                finish()
//            }, 1)
//        }
        binding!!.buttonSplashRetry.setOnClickListener {
            binding!!.textViewSplashError.visibility = View.GONE
            binding!!.buttonSplashRetry.visibility = View.GONE
            loadUserData(username!!)
        }
    }

    private fun loadSharedPreferences() {
        val sharedPreferences = getSharedPreferences(UserUtils.APP_PREFS, MODE_PRIVATE)

        username = sharedPreferences.getString(UserUtils.USER_LOGIN, StringUtils.EMPTY)
        accessToken = sharedPreferences.getString(ACCESS_TOKEN, StringUtils.EMPTY)

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(accessToken)) {
            //showWelcomeFragment
        } else {
            userViewModel.getUserByUsername(username!!)
        }
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