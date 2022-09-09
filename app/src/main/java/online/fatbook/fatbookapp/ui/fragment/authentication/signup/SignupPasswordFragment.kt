package online.fatbook.fatbookapp.ui.fragment.authentication.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_signup_password.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentSignupPasswordBinding
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.Constants.PASSWORD_REGEX
import online.fatbook.fatbookapp.util.ProgressBarUtil.hideProgressBar
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import java.util.regex.Pattern

class SignupPasswordFragment : Fragment() {
    private var binding: FragmentSignupPasswordBinding? = null

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupPasswordBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideProgressBar()
        fragment_signup_password_button_next.setOnClickListener {
            if (passwordValidate()) {
                authViewModel.password.value =
                    fragment_signup_password_edittext_password.text.toString()
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_signup_username)
            } else {
                hideKeyboard(fragment_signup_password_edittext_password)
                showErrorMessage()
            }
        }

        fragment_signup_password_edittext_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fragment_signup_password_edittext_password.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext)
                fragment_signup_password_button_next.isEnabled = s.toString().length in 6..16
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun showErrorMessage() {
        fragment_signup_password_button_next.isEnabled = false
        fragment_signup_password_dialog_text.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )
        fragment_signup_password_edittext_password.text = null
        fragment_signup_password_edittext_password.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext_error)
    }

    private fun passwordValidate(): Boolean {
        return true
//        return Pattern.compile(PASSWORD_REGEX).matcher(fragment_signup_password_edittext_password.text.toString()).matches()
    }

}