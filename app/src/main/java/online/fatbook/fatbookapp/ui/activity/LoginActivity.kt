package online.fatbook.fatbookapp.ui.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.Role
import online.fatbook.fatbookapp.core.User
import online.fatbook.fatbookapp.databinding.ActivityLoginBinding
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import online.fatbook.fatbookapp.ui.viewmodel.SignInViewModel
import online.fatbook.fatbookapp.util.UserUtils
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LoginActivity : AppCompatActivity() {
    private var binding: ActivityLoginBinding? = null
    private var signInViewModel: SignInViewModel? = null
    private var btnNextClicked = false
    private var isKeyboardVisible = false
    val listener = OnGlobalLayoutListener {
        val rectangle = Rect()
        val contentView: View = binding!!.root
        contentView.getWindowVisibleDisplayFrame(rectangle)
        val screenHeight = contentView.rootView.height
        val keypadHeight = screenHeight - rectangle.bottom
        val isKeyboardNowVisible = keypadHeight > screenHeight * 0.15
        if (isKeyboardVisible != isKeyboardNowVisible) {
            if (isKeyboardNowVisible) {
                binding!!.textViewLoginVersion.visibility = View.GONE
                binding!!.textViewLoginCopyright.visibility = View.GONE
                binding!!.textViewLoginTagline.visibility = View.GONE
            } else {
                binding!!.textViewLoginVersion.visibility = View.VISIBLE
                binding!!.textViewLoginCopyright.visibility = View.VISIBLE
                binding!!.textViewLoginTagline.visibility = View.VISIBLE
            }
        }
        isKeyboardVisible = isKeyboardNowVisible
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.buttonLoginNext.isEnabled = false
        binding!!.buttonLoginNext.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.color_blue_grey_200)
        signInViewModel = ViewModelProvider(this)[SignInViewModel::class.java]
        signInViewModel!!.isLoginAvailable.observe(this) { available: Boolean? ->
            if (available!!) {
                toggleViews(true)
                if (btnNextClicked) {
                    val intent = Intent(application, PasswordActivity::class.java)
                    val user = User()
                    user.login = binding!!.editTextLogin.text.toString()
                    user.role = Role.USER
                    user.birthday = StringUtils.EMPTY
                    user.recipes = ArrayList()
                    user.recipesForked = ArrayList()
                    user.recipesBookmarked = ArrayList()
                    user.regDate = UserUtils.regDateFormat.format(Date())
                    intent.putExtra(UserUtils.TAG_USER, user)
                    startActivity(intent)
                    btnNextClicked = false
                }
            } else {
                toggleViews(false)
                btnNextClicked = false
            }
        }
        binding!!.editTextLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                validateLogin(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding!!.buttonLoginNext.setOnClickListener { view: View? ->
            btnNextClicked = true
            validateLogin(binding!!.editTextLogin.text.toString())
        }
    }

    private fun toggleViews(enable: Boolean) {
        if (enable) {
            binding!!.imageViewLoginIconAccepted.visibility = View.VISIBLE
            binding!!.editTextLogin.background =
                AppCompatResources.getDrawable(baseContext, R.drawable.round_corner_edittext_login)
            binding!!.buttonLoginNext.isEnabled = true
            binding!!.buttonLoginNext.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.color_pink_a200)
        } else {
            binding!!.imageViewLoginIconAccepted.visibility = View.INVISIBLE
            binding!!.editTextLogin.background =
                AppCompatResources.getDrawable(
                    baseContext,
                    R.drawable.round_corner_edittext_error
                )
            binding!!.buttonLoginNext.isEnabled = false
            binding!!.buttonLoginNext.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.color_blue_grey_200)
        }
    }

    private fun validateLogin(login: String) {
        if (login.matches(Regex("[a-zA-Z0-9]+")) && login.length >= 4) {
//            LoginActivity.log.log(Level.INFO, "login valid")
            loginCheckForCreation(login)
        } else {
//            LoginActivity.log.log(Level.INFO, "login invalid")
            toggleViews(false)
        }
    }

    private fun loginCheckForCreation(login: String) {
        RetrofitFactory.apiServiceClient().loginCheck(login).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
//                LoginActivity.log.log(
//                    Level.INFO,
//                    "login check SUCCESS: " + response.code() + " = " + response.body()
//                )
                if (response.code() == 200) {
                    signInViewModel!!.isLoginAvailable.value = response.body()
                } else {
                    signInViewModel!!.isLoginAvailable.value = false
                }
                if (response.body()!!) {
//                    LoginActivity.log.log(Level.INFO, "login available")
                } else {
//                    LoginActivity.log.log(Level.INFO, "login unavailable")
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
//                LoginActivity.log.log(Level.INFO, "login check FAILED")
                t.printStackTrace()
                signInViewModel!!.isLoginAvailable.value = false
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