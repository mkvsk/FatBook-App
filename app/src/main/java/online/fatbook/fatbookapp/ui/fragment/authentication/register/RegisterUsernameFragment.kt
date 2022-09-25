package online.fatbook.fatbookapp.ui.fragment.authentication.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_register_username.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.authentication.AuthenticationRequest
import online.fatbook.fatbookapp.core.authentication.AuthenticationResponse
import online.fatbook.fatbookapp.databinding.FragmentRegisterUsernameBinding
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.Constants.USERNAME_REGEX
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import java.util.regex.Pattern

class RegisterUsernameFragment : Fragment() {
    private var binding: FragmentRegisterUsernameBinding? = null

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterUsernameBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addObservers()
        fragment_register_username_button_next.setOnClickListener {
            if (usernameValidate()) {
                authViewModel.usernameCheck(fragment_register_username_edittext_username.text.toString())
            } else {
                hideKeyboard(fragment_register_username_edittext_username)
                showErrorMessage(getString(R.string.dialog_register_username_invalid))
            }
        }

        fragment_register_username_edittext_username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fragment_register_username_edittext_username.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext)
                fragment_register_username_button_next.isEnabled = s.toString().length in 3..20
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    fragment_register_username_dialog_text.text =
                        getString(R.string.dialog_register_username)
                    fragment_register_username_dialog_text.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.main_text
                        )
                    )
                }
//                fragment_register_username_edittext_username.setText(s.toString().lowercase())
            }

        })
    }

    private fun addObservers() {
        authViewModel.usernameAvailable.observe(viewLifecycleOwner) {
            if (it!!) {
                authViewModel.username.value =
                    fragment_register_username_edittext_username.text.toString()
                createNewUser()
            } else {
                hideKeyboard(fragment_register_username_edittext_username)
                showErrorMessage(getString(R.string.dialog_register_username_unavailable))
            }
        }
    }

    private fun showErrorMessage(message: String) {
        fragment_register_username_button_next.isEnabled = false
        fragment_register_username_dialog_text.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )
        fragment_register_username_dialog_text.text = message;
        fragment_register_username_edittext_username.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext_error)
    }

    private fun createNewUser() {
        progressbarLayout_register_username.visibility = View.VISIBLE
        authViewModel.register(
            AuthenticationRequest(
                authViewModel.username.value,
                authViewModel.password.value,
                authViewModel.userEmail.value
            ), object : ResultCallback<AuthenticationResponse> {
                override fun onResult(value: AuthenticationResponse?) {
                    progressbarLayout_register_username.visibility = View.GONE
                    value?.let {
                        when (it.code) {
                            0 -> {
                                navigateToAccountCreated()
                            }
                            4 -> {
                                showErrorMessage(getString(R.string.dialog_register_email_error))
                            }
                            5 -> {
                                showErrorMessage(getString(R.string.dialog_register_username_unavailable))
                            }
                            else -> {
                                showErrorMessage(getString(R.string.dialog_register_error))
                            }
                        }
                    }

                }

                override fun onFailure(value: AuthenticationResponse?) {

                }
            }
        )
    }

    private fun navigateToAccountCreated() {
//        authViewModel.userEmail.value = StringUtils.EMPTY
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_go_to_account_created)
    }

    private fun usernameValidate(): Boolean {
        return Pattern.compile(USERNAME_REGEX)
            .matcher(fragment_register_username_edittext_username.text).matches()
//        return true

    }
}