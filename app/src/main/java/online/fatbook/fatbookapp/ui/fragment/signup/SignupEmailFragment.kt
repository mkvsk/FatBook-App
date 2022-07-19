package online.fatbook.fatbookapp.ui.fragment.signup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_signup_email.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentSignupEmailBinding
import online.fatbook.fatbookapp.ui.viewmodel.SignupViewModel
import online.fatbook.fatbookapp.util.obtainViewModel
import java.lang.Exception
import java.util.regex.Pattern

class SignupEmailFragment : Fragment() {

    private var binding: FragmentSignupEmailBinding? = null

    private val signupViewModel by lazy { obtainViewModel(SignupViewModel::class.java) }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupEmailBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_signup_email_button_next.setOnClickListener {
            if (emailValidate()) {
                if (emailCheck()) {
                    signupViewModel.email.value =
                        fragment_signup_email_edittext_email.text.toString()
                    sendVerificationCode(signupViewModel.email.value!!)
                    NavHostFragment.findNavController(this)
                        .navigate(R.id.action_go_to_verification_code)
                } else {
                    //TODO почта уже используется
                }
            } else {
                fragment_signup_email_dialog_text.text =
                    getString(R.string.dialog_wrong_data_signup_email)
                fragment_signup_email_dialog_text.setTextColor(Color.parseColor("#FF4081"))
                fragment_signup_email_edittext_email.text = null
                fragment_signup_email_edittext_email.clearFocus()
                fragment_signup_email_edittext_email.background = resources.getDrawable(R.drawable.round_corner_edittext_error)


                //TODO почта инвалиндая
            }
        }

        fragment_signup_email_edittext_email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 3) {
                    fragment_signup_email_button_next.visibility = View.VISIBLE
                } else {
                    fragment_signup_email_button_next.visibility = View.INVISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    //TODO api call
    private fun sendVerificationCode(value: String) {
        signupViewModel.VCCode.value = "111111"
        /* if (!signupViewModel.isVCSend.value!!) {

         }*/
    }

    //TODO api call
    private fun emailCheck(): Boolean {
        return true
    }

    private fun emailValidate(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(fragment_signup_email_edittext_email.text).matches()
//        return true
    }
}