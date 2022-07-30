package online.fatbook.fatbookapp.ui.fragment.authentication.signin

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.alert_dialog_title_logout.*
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.android.synthetic.main.fragment_signin_new_pass.*
import kotlinx.android.synthetic.main.fragment_signup_password.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentSigninNewPassBinding
import online.fatbook.fatbookapp.util.Constants
import online.fatbook.fatbookapp.util.hideKeyboard
import java.util.regex.Pattern

class SignInNewPassFragment : Fragment() {
    private var binding: FragmentSigninNewPassBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSigninNewPassBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_signin_new_pass_button_next.setOnClickListener {
            hideKeyboard(fragment_signin_new_pass_edittext_repeat_new_password)

            //TODO fix validate
            if (fragment_signin_new_pass_edittext_new_password.text.toString().contentEquals(
                    fragment_signin_new_pass_edittext_repeat_new_password.text.toString()
                )
            ) {
                showErrorMessage("equals")
                if (passwordValidate()) {
                    showErrorMessage("valid")
                } else if (!passwordValidate()) {
                    showErrorMessage("INVALID")
                }
            } else {
                showErrorMessage("passwords is not match")
            }
            /*if(passwordValidate(fragment_signin_new_pass_edittext_new_password.text.toString()) && passwordValidate(fragment_signin_new_pass_edittext_repeat_new_password.text.toString())) {
                if(fragment_signin_new_pass_edittext_new_password.text.toString() == fragment_signin_new_pass_edittext_repeat_new_password.text.toString()) {
                    //TODO call api
                } else {
                    showErrorMessage(getString(R.string.dialog_sigbib_new_pass_passwords_not_match))
                }
            } else {
                showErrorMessage(getString(R.string.dialog_signin_new_pass))
            }*/

        }

        fragment_signin_new_pass_edittext_new_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length in 6..20) {
                    checkEditTextIsFilled(fragment_signin_new_pass_edittext_new_password, true)
                } else {
                    checkEditTextIsFilled(fragment_signin_new_pass_edittext_new_password, false)
                }
                enableButtonNext(
                    fragment_signin_new_pass_edittext_new_password.text.toString(),
                    fragment_signin_new_pass_edittext_repeat_new_password.text.toString()
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        fragment_signin_new_pass_edittext_repeat_new_password.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length in 6..20) {
                    checkEditTextIsFilled(
                        fragment_signin_new_pass_edittext_repeat_new_password,
                        true
                    )
                } else {
                    checkEditTextIsFilled(
                        fragment_signin_new_pass_edittext_repeat_new_password,
                        false
                    )
                }
                enableButtonNext(
                    fragment_signin_new_pass_edittext_new_password.text.toString(),
                    fragment_signin_new_pass_edittext_repeat_new_password.text.toString()
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun showErrorMessage(message: String) {
        fragment_signin_new_pass_kuzya_dialog.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.cat_dialog_invalid_data
            )
        )
        fragment_signin_new_pass_dialog_text.text = message
        fragment_signin_new_pass_dialog_text.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )
    }

    private fun checkEditTextIsFilled(view: View, filled: Boolean) {
        if (filled) {
            view.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext)
        } else {
            view.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext_error)
        }
    }

    private fun enableButtonNext(password: String, repeatPassword: String) {
        fragment_signin_new_pass_button_next.isEnabled =
            password.length in 6..20 && repeatPassword.length in 6..20
    }

    private fun passwordValidate(): Boolean {
        return Pattern.compile(Constants.PASSWORD_REGEX)
            .matcher(fragment_signin_new_pass_edittext_new_password.text.toString()).matches()
    }
}