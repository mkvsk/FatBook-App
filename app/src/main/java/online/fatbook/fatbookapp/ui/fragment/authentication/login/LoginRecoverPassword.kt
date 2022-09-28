package online.fatbook.fatbookapp.ui.fragment.authentication.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_login_recover_pass.*
import kotlinx.android.synthetic.main.include_progress_overlay_auth.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.authentication.AuthenticationResponse
import online.fatbook.fatbookapp.databinding.FragmentLoginRecoverPassBinding
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel

class LoginRecoverPassword : Fragment() {

    private var reconnectCount = 1
    private var isReconnectCancelled = false

    private var binding: FragmentLoginRecoverPassBinding? = null
    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginRecoverPassBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackPressed()
        if (!authViewModel.recoverIdentifier.value.isNullOrEmpty()) {
            fragment_login_recover_pass_edittext_username.setText(authViewModel.recoverIdentifier.value)
            enableButtonNext(fragment_login_recover_pass_edittext_username.text.toString())
        }
        fragment_login_recover_pass_edittext_username.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length in 3..20) {
                    checkEditTextIsFilled(fragment_login_recover_pass_edittext_username, true)
                } else {
                    checkEditTextIsFilled(fragment_login_recover_pass_edittext_username, false)
                }
                enableButtonNext(fragment_login_recover_pass_edittext_username.text.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        fragment_login_recover_pass_button_next.setOnClickListener {
            if (authViewModel.recoverIdentifier.value!! != fragment_login_recover_pass_edittext_username.text.toString()) {
                authViewModel.isTimerRunning.value = false
                authViewModel.currentCountdown.value = 0
                authViewModel.cancelTimer()
                isReconnectCancelled = false
                recoverPassword(fragment_login_recover_pass_edittext_username.text.toString())
            } else {
                if (authViewModel.isTimerRunning.value == false) {
                    recoverPassword(fragment_login_recover_pass_edittext_username.text.toString())
                } else {
                    navigateToVerificationCode()
                }
            }
        }
    }

    private fun recoverPassword(identifier: String) {
        Log.d("RECOVER PASSWORD attempt", reconnectCount.toString())
        progress_overlay_auth.visibility = View.VISIBLE
        hideKeyboard(fragment_login_recover_pass_edittext_username)
        authViewModel.recoverPassword(identifier, object : ResultCallback<AuthenticationResponse> {
            override fun onResult(value: AuthenticationResponse?) {
                progress_overlay_auth.visibility = View.GONE
                if (!isReconnectCancelled) {
                    when (value!!.code) {
                        0 -> {
                            if (!authViewModel.isTimerRunning.value!!) {
                                authViewModel.isTimerRunning.value = true
                                authViewModel.startTimer(authViewModel.resendVCTimer.value!!)
                            }
                            authViewModel.recoverIdentifier.value = identifier
                            authViewModel.recoverEmail.value = value.email
                            authViewModel.recoverUsername.value = value.username
                            authViewModel.vCode.value = value.vcode
                            Log.d("CODE ======================= ", value.vcode!!)
                            Toast.makeText(requireContext(), value.vcode, Toast.LENGTH_LONG)
                                .show()
                            navigateToVerificationCode()
                        }
                        6 -> {
                            showErrorMessage(getString(R.string.dialog_recover_pass_user_not_found))
                        }
                        else -> showErrorMessage(getString(R.string.dialog_register_error))
                    }
                }
            }

            override fun onFailure(value: AuthenticationResponse?) {
                if (!isReconnectCancelled) {
                    if (reconnectCount < 6) {
                        reconnectCount++
                        recoverPassword(identifier)
                    } else {
                        showErrorMessage(getString(R.string.dialog_register_error))
                        hideKeyboard(fragment_login_recover_pass_edittext_username)
                        progress_overlay_auth.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun navigateToVerificationCode() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_go_to_login_recover_pass_vcode_from_recover_pass)
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

    private fun enableButtonNext(username: String) {
        fragment_login_recover_pass_button_next.isEnabled = username.length in 3..20
    }

    private fun showErrorMessage(message: String) {
        fragment_login_recover_pass_dialog_text.text = message
        fragment_login_recover_pass_dialog_text.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.dialogErrorMess_text
            )
        )
//        fragment_login_recover_pass_edittext_username.background =
//            ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext_error)
    }

    private fun showDefaultMessage(message: String) {
        fragment_login_recover_pass_dialog_text.text = message
        fragment_login_recover_pass_dialog_text.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.main_text
            )
        )
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
}