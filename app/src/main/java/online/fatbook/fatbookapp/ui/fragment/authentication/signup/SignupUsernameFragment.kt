package online.fatbook.fatbookapp.ui.fragment.authentication.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_signup_username.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.AuthenticationRequest
import online.fatbook.fatbookapp.core.AuthenticationResponse
import online.fatbook.fatbookapp.databinding.FragmentSignupUsernameBinding
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.Constants.USERNAME_REGEX
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils
import java.util.regex.Pattern

class SignupUsernameFragment : Fragment() {
    private var binding: FragmentSignupUsernameBinding? = null

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupUsernameBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addObservers()

        fragment_signup_username_button_next.setOnClickListener {
            if (usernameValidate()) {
                authViewModel.usernameCheck(fragment_signup_username_edittext_username.text.toString())
            } else {
                hideKeyboard(fragment_signup_username_edittext_username)
                showErrorMessage(getString(R.string.dialog_signup_username_invalid))
            }
        }

        fragment_signup_username_edittext_username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fragment_signup_username_edittext_username.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext)
                fragment_signup_username_button_next.isEnabled = s.toString().length in 3..20
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    fragment_signup_username_dialog_text.text =
                        getString(R.string.dialog_signup_username)
                    fragment_signup_username_dialog_text.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.main_text
                        )
                    )
                }
//                fragment_signup_username_edittext_username.setText(s.toString().lowercase())
            }

        })
    }

    private fun addObservers() {
        authViewModel.usernameAvailable.observe(viewLifecycleOwner) {
            if (it!!) {
                authViewModel.username.value =
                    fragment_signup_username_edittext_username.text.toString()
                createNewUser()
            } else {
                hideKeyboard(fragment_signup_username_edittext_username)
                showErrorMessage(getString(R.string.dialog_signup_username_unavailable))
            }
        }
    }

    private fun showErrorMessage(message: String) {
        fragment_signup_username_button_next.isEnabled = false
        fragment_signup_username_dialog_text.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )
        fragment_signup_username_dialog_text.text = message;
        fragment_signup_username_edittext_username.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext_error)
    }

    private fun createNewUser() {
        progressbarLayout_signup_username.visibility = View.VISIBLE
        authViewModel.signUp(
            AuthenticationRequest(
                authViewModel.username.value,
                authViewModel.password.value,
                authViewModel.userEmail.value
            ), object : ResultCallback<AuthenticationResponse> {
                override fun onResult(value: AuthenticationResponse?) {
                    progressbarLayout_signup_username.visibility = View.GONE
                    value?.let {
                        when (it.code) {
                            0 -> {
                                navigateToAccountCreated()
                            }
                            4 -> {
                                showErrorMessage(getString(R.string.dialog_signup_email_error))
                            }
                            5 -> {
                                showErrorMessage(getString(R.string.dialog_signup_username_unavailable))
                            }
                            else -> {
                                showErrorMessage(getString(R.string.dialog_signup_error))
                            }
                        }
                    }

                }
            }
        )
    }

    private fun navigateToAccountCreated() {
        authViewModel.userEmail.value = StringUtils.EMPTY
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_go_to_account_created)
    }

    private fun usernameValidate(): Boolean {
        return Pattern.compile(USERNAME_REGEX)
            .matcher(fragment_signup_username_edittext_username.text).matches()
//        return true

    }
}