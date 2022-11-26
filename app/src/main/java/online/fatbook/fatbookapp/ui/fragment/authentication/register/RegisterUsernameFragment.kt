package online.fatbook.fatbookapp.ui.fragment.authentication.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_register_username.*
import kotlinx.android.synthetic.main.include_progress_overlay_auth.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.network.AuthenticationRequest
import online.fatbook.fatbookapp.network.AuthenticationResponse
import online.fatbook.fatbookapp.databinding.FragmentRegisterUsernameBinding
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.Constants.USERNAME_REGEX
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import java.util.regex.Pattern

class RegisterUsernameFragment : Fragment() {

    private var reconnectCount = 1
    private var isReconnectCancelled = false

    private var binding: FragmentRegisterUsernameBinding? = null
    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterUsernameBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_register_username_button_next.setOnClickListener {
            if (usernameValidate()) {
                authViewModel.username.value =
                    fragment_register_username_edittext_username.text.toString()
                isReconnectCancelled = false
                createNewUser()
            } else {
                hideKeyboard(fragment_register_username_edittext_username)
                showErrorMessage(getString(R.string.dialog_register_username_invalid), true)
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
                            requireContext(), R.color.main_text
                        )
                    )
                }
            }
        })

        handleBackPressed()
    }

    private fun createNewUser() {
        Log.d("REGISTER attempt", reconnectCount.toString())
        progress_overlay_auth.visibility = View.VISIBLE
        hideKeyboard(fragment_register_username_edittext_username)
        authViewModel.register(
            AuthenticationRequest(
            authViewModel.username.value,
            authViewModel.password.value,
            authViewModel.userEmail.value
        ), object : ResultCallback<AuthenticationResponse> {
            override fun onResult(value: AuthenticationResponse?) {
                progress_overlay_auth.visibility = View.GONE
                value?.let {
                    when (it.code) {
                        0 -> {
                            if (!isReconnectCancelled) {
                                navigateToAccountCreated()
                            }
                        }
                        4 -> {
                            hideKeyboard(fragment_register_username_edittext_username)
                            showErrorMessage(
                                getString(R.string.dialog_register_email_error), true
                            )
                        }
                        5 -> {
                            hideKeyboard(fragment_register_username_edittext_username)
                            showErrorMessage(
                                getString(R.string.dialog_register_username_unavailable), true
                            )
                        }
                        else -> {
                            hideKeyboard(fragment_register_username_edittext_username)
                            showErrorMessage(getString(R.string.dialog_register_error), true)
                        }
                    }
                }
            }

            override fun onFailure(value: AuthenticationResponse?) {
                if (!isReconnectCancelled) {
                    if (reconnectCount < 6) {
                        reconnectCount++
                        createNewUser()
                    } else {
                        hideKeyboard(fragment_register_username_edittext_username)
                        showErrorMessage(getString(R.string.dialog_register_error), false)
                        progress_overlay_auth.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun showErrorMessage(message: String, dyeEditText: Boolean) {
        fragment_register_username_button_next.isEnabled = false
        fragment_register_username_dialog_text.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.dialogErrorMess_text
            )
        )
        fragment_register_username_dialog_text.text = message
        if (dyeEditText) {
            fragment_register_username_edittext_username.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext_error)
        }
    }

    private fun showDefaultMessage(message: String) {
        fragment_register_username_button_next.isEnabled = false
        fragment_register_username_dialog_text.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.dialogErrorMess_text
            )
        )
        fragment_register_username_dialog_text.text = message;
        fragment_register_username_edittext_username.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext)
    }

    private fun navigateToAccountCreated() {
        NavHostFragment.findNavController(this).navigate(R.id.action_go_to_account_created)
    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (progress_overlay_auth.visibility == View.VISIBLE) {
                        progress_overlay_auth.visibility = View.GONE
                        showDefaultMessage(getString(R.string.dialog_register_email_error))
                        isReconnectCancelled = true
                    } else {
                        popBackStack()
                    }
                }
            })
    }

    private fun popBackStack() {
        NavHostFragment.findNavController(this).popBackStack()
    }

    private fun usernameValidate(): Boolean {
        return Pattern.compile(USERNAME_REGEX)
            .matcher(fragment_register_username_edittext_username.text).matches()
    }
}