package online.fatbook.fatbookapp.ui.fragment.authentication.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_signup_email.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentSignupEmailBinding
import online.fatbook.fatbookapp.ui.viewmodel.SignupViewModel
import online.fatbook.fatbookapp.util.Constants.SYMBOL_AT
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import java.time.LocalDateTime

class SignupEmailFragment : Fragment() {

    private var binding: FragmentSignupEmailBinding? = null
    private val signupViewModel by lazy { obtainViewModel(SignupViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupEmailBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment_signup_email_button_next.setOnClickListener {
            if (emailValidate()) {
                if (emailCheck()) {
                    if (signupViewModel.email.value!! != fragment_signup_email_edittext_email.text.toString()) {
                        signupViewModel.timestampSet.value = false
                        signupViewModel.currentCountdown.value = 0
                        signupViewModel.cancelTimer()
                    }
                    signupViewModel.email.value =
                        fragment_signup_email_edittext_email.text.toString()
                    startTimer()
                    sendVerificationCode(signupViewModel.email.value!!)
                    NavHostFragment.findNavController(this)
                        .navigate(R.id.action_go_to_verification_code)
                } else {
                    hideKeyboard(fragment_signup_email_edittext_email)
                    showErrorMessage(getString(R.string.dialog_email_used_signup_email))
                }
            } else {
                hideKeyboard(fragment_signup_email_edittext_email)
                showErrorMessage(getString(R.string.dialog_wrong_data_signup_email))
            }
        }

        fragment_signup_email_edittext_email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fragment_signup_email_edittext_email.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext)
                if (s != null) {
                    fragment_signup_email_button_next.isEnabled = s.contains(SYMBOL_AT)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun showErrorMessage(message: String) {
        fragment_signup_email_dialog_text.text = message;
        fragment_signup_email_dialog_text.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )
        fragment_signup_email_edittext_email.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext_error)
    }

    private fun startTimer() {
        if (!signupViewModel.timestampSet.value!!) {
            signupViewModel.timestampSet.value = true
            signupViewModel.startTimer(signupViewModel.resendVCTimer.value!!)
        }
    }

    //TODO api call
    private fun sendVerificationCode(value: String) {
        signupViewModel.vCode.value = "123123"
    }

    //TODO api call
    private fun emailCheck(): Boolean {
        return true
    }

    private fun emailValidate(): Boolean {
//        return Patterns.EMAIL_ADDRESS.matcher(fragment_signup_email_edittext_email.text).matches()
        return true
    }
}