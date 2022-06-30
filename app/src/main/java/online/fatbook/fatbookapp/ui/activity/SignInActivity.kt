package online.fatbook.fatbookapp.ui.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import lombok.extern.java.Log
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.User
import online.fatbook.fatbookapp.databinding.ActivitySignInBinding
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import online.fatbook.fatbookapp.ui.viewmodel.SignInViewModel
import online.fatbook.fatbookapp.util.UserUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.logging.Level

class SignInActivity : AppCompatActivity() {
    private var binding: ActivitySignInBinding? = null
    private var signInViewModel: SignInViewModel? = null
    var isKeyboardVisible = false
    val listener = OnGlobalLayoutListener {
        val rectangle = Rect()
        val contentView: View = binding!!.root
        contentView.getWindowVisibleDisplayFrame(rectangle)
        val screenHeight = contentView.rootView.height
        val keypadHeight = screenHeight - rectangle.bottom
        val isKeyboardNowVisible = keypadHeight > screenHeight * 0.15
        if (isKeyboardVisible != isKeyboardNowVisible) {
            if (isKeyboardNowVisible) {
                binding!!.textViewSignInVersion.visibility = View.GONE
                binding!!.textViewSignInCopyright.visibility = View.GONE
                binding!!.textViewSignInTagline.visibility = View.GONE
                binding!!.textViewSignInAppLabel.visibility = View.GONE
            } else {
                binding!!.textViewSignInVersion.visibility = View.VISIBLE
                binding!!.textViewSignInCopyright.visibility = View.VISIBLE
                binding!!.textViewSignInTagline.visibility = View.VISIBLE
                binding!!.textViewSignInAppLabel.visibility = View.VISIBLE
            }
        }
        isKeyboardVisible = isKeyboardNowVisible
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        signInViewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        signInViewModel!!.user.observe(this) { _user: User? ->
            if (_user != null) {
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.putExtra(UserUtils.TAG_USER, _user)
                startActivity(intent)
                finishAffinity()
            } else {
                binding!!.editTextSignInLogin.background = AppCompatResources.getDrawable(
                    this,
                    R.drawable.round_corner_edittext_login_error
                )
                binding!!.editTextSignInPassword.background = AppCompatResources.getDrawable(
                    this,
                    R.drawable.round_corner_edittext_login_error
                )
            }
        }
        binding!!.buttonSignIn.setOnClickListener { view: View? ->
            validateLogin(
                binding!!.editTextSignInLogin.text.toString().trim { it <= ' ' },
                binding!!.editTextSignInPassword.text.toString()
            )
        }
        binding!!.editTextSignInLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                binding!!.editTextSignInLogin.background = AppCompatResources.getDrawable(
                    baseContext, R.drawable.round_corner_edittext_login
                )
                binding!!.editTextSignInPassword.background = AppCompatResources.getDrawable(
                    baseContext, R.drawable.round_corner_edittext_login
                )
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding!!.editTextSignInPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                binding!!.editTextSignInLogin.background = AppCompatResources.getDrawable(
                    baseContext, R.drawable.round_corner_edittext_login
                )
                binding!!.editTextSignInPassword.background = AppCompatResources.getDrawable(
                    baseContext, R.drawable.round_corner_edittext_login
                )
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun validateLogin(login: String, fat: String) {
        RetrofitFactory.apiServiceClient().signIn(login, fat).enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
//                SignInActivity.log.log(Level.INFO, "fat: " + response.code())
                if (response.code() == 200) {
                    signInViewModel!!.user.value = response.body()
                } else {
                    signInViewModel!!.user.value = null
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
//                SignInActivity.log.log(Level.INFO, "fat: FAILED $t")
                signInViewModel!!.user.value = null
            }
        })
    }

    override fun onResume() {
        super.onResume()
        binding!!.root.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    override fun onPause() {
        super.onPause()
        binding!!.root.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }
}