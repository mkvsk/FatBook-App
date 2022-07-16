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
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.User
import online.fatbook.fatbookapp.databinding.ActivityPasswordBinding
import online.fatbook.fatbookapp.util.UserUtils

class PasswordActivity : AppCompatActivity() {
    private var binding: ActivityPasswordBinding? = null
    private var user: User? = null
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
                binding!!.textViewPasswordVersion.visibility = View.GONE
                binding!!.textViewPasswordCopyright.visibility = View.GONE
                binding!!.textViewPasswordTagline.visibility = View.GONE
            } else {
                binding!!.textViewPasswordVersion.visibility = View.VISIBLE
                binding!!.textViewPasswordCopyright.visibility = View.VISIBLE
                binding!!.textViewPasswordTagline.visibility = View.VISIBLE
            }
        }
        isKeyboardVisible = isKeyboardNowVisible
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        user = intent.getSerializableExtra(UserUtils.TAG_USER) as User?
        binding!!.buttonPasswordNext.isEnabled = false
        binding!!.buttonPasswordNext.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.color_blue_grey_200)
        binding!!.editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                toggleViews(validateFat(charSequence.toString()))
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding!!.buttonPasswordNext.setOnClickListener { view: View? ->
            if (validateFat(
                    binding!!.editTextPassword.text.toString()
                )
            ) {
                val intent = Intent(this, SkipAdditionalInfoActivity::class.java)
                intent.putExtra(UserUtils.TAG_USER, user)
                intent.putExtra(UserUtils.TAG_FAT, binding!!.editTextPassword.text.toString())
                startActivity(intent)
            } else {
                toggleViews(false)
            }
        }
    }

    private fun toggleViews(enable: Boolean) {
        if (enable) {
            binding!!.imageViewPasswordIconAccepted.visibility = View.VISIBLE
            binding!!.editTextPassword.background =
                AppCompatResources.getDrawable(baseContext, R.drawable.round_corner_edittext_login)
            binding!!.buttonPasswordNext.isEnabled = true
            binding!!.buttonPasswordNext.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.color_pink_a200)
        } else {
            binding!!.imageViewPasswordIconAccepted.visibility = View.INVISIBLE
            binding!!.editTextPassword.background =
                AppCompatResources.getDrawable(
                    baseContext,
                    R.drawable.round_corner_edittext_error
                )
            binding!!.buttonPasswordNext.isEnabled = false
            binding!!.buttonPasswordNext.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.color_blue_grey_200)
        }
    }

    override fun onResume() {
        super.onResume()
        binding!!.root.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    override fun onPause() {
        super.onPause()
        binding!!.root.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }

    /**
     * https://mkyong.com/regular-expressions/how-to-validate-password-with-regular-expression/
     *
     *
     * Password must contain at least one digit [0-9].
     * Password must contain at least one lowercase Latin character [a-z].
     * Password must contain at least one uppercase Latin character [A-Z].
     * Password must contain at least one special character like ! @ # & ( ).
     * Password must contain a length of at least 8 characters and a maximum of 20 characters.
     */
    private fun validateFat(fat: String): Boolean {
        return fat.length >= 6 && fat.length <= 16
        //        return fat.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$")
    }
}