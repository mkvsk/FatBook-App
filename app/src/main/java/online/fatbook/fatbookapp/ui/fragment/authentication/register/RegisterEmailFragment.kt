package online.fatbook.fatbookapp.ui.fragment.authentication.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_register_email.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.AuthenticationResponse
import online.fatbook.fatbookapp.databinding.FragmentRegisterEmailBinding
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.Constants.SYMBOL_AT
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel

class RegisterEmailFragment : Fragment() {

    private var binding: FragmentRegisterEmailBinding? = null
    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterEmailBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment_register_email_button_next.setOnClickListener {
            if (emailValidate(fragment_register_email_edittext_email.text.toString())) {
                if (authViewModel.userEmail.value!! != fragment_register_email_edittext_email.text.toString()) {
                    authViewModel.isTimerRunning.value = false
                    authViewModel.currentCountdown.value = 0
                    authViewModel.cancelTimer()
                    emailCheck(fragment_register_email_edittext_email.text.toString())
                } else {
                    navigateToVerificationCode()
                }
            } else {
                hideKeyboard(fragment_register_email_edittext_email)
                showErrorMessage(getString(R.string.dialog_wrong_data_register_email))
            }
        }

        fragment_register_email_edittext_email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fragment_register_email_edittext_email.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext)
                if (s != null) {
                    fragment_register_email_button_next.isEnabled = s.contains(SYMBOL_AT)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        handleBackPressed()
    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (progressbar_register_email.visibility == View.VISIBLE) {
                        progressbar_register_email.visibility = View.GONE
                    } else {
                        showDefaultMessage(getString(R.string.dialog_register_email_error))
                        popBackStack()
                    }
                }
            })
    }

    private fun popBackStack() {
        NavHostFragment.findNavController(this).popBackStack()
    }

    private fun showErrorMessage(message: String) {
        fragment_register_email_dialog_text.text = message;
        fragment_register_email_dialog_text.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )
        fragment_register_email_edittext_email.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext_error)
    }

    private fun showDefaultMessage(message: String) {
        fragment_register_email_dialog_text.text = message;
        fragment_register_email_dialog_text.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.main_text
            )
        )
        fragment_register_email_edittext_email.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext)
    }

    private fun emailValidate(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    private fun emailCheck(email: String) {
        progressbar_register_email.visibility = View.VISIBLE
        hideKeyboard(fragment_register_email_edittext_email)
        authViewModel.emailCheck(email, object : ResultCallback<AuthenticationResponse> {
            override fun onResult(value: AuthenticationResponse?) {
                when (value?.code) {
                    0 -> {
                        authViewModel.userEmail.value = value.email
                        if (!authViewModel.isTimerRunning.value!!) {
                            authViewModel.isTimerRunning.value = true
                            authViewModel.startTimer(authViewModel.resendVCTimer.value!!)
                        }
                        authViewModel.vCode.value = value.vcode
                        Log.d("CODE ======================= ", value.vcode!!)
                        Toast.makeText(requireContext(), value.vcode, Toast.LENGTH_LONG).show()
                        navigateToVerificationCode()
                    }
                    4 -> {
                        hideKeyboard(fragment_register_email_edittext_email)
                        showErrorMessage(getString(R.string.dialog_email_used_register_email))
                        progressbar_register_email.visibility = View.GONE
                    }
                    else -> {
                        hideKeyboard(fragment_register_email_edittext_email)
                        showErrorMessage(getString(R.string.dialog_register_error))
                        progressbar_register_email.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(value: AuthenticationResponse?) {
                hideKeyboard(fragment_register_email_edittext_email)
                showErrorMessage(getString(R.string.dialog_register_error))
                progressbar_register_email.visibility = View.GONE

//                emailCheck(email)
            }
        })
    }

    private fun navigateToVerificationCode() {
        NavHostFragment.findNavController(this).navigate(R.id.action_go_to_verification_code)
    }
}