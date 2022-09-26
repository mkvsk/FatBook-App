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
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_register_username.*
import kotlinx.android.synthetic.main.fragment_verification_code.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.authentication.AuthenticationResponse
import online.fatbook.fatbookapp.databinding.FragmentVerificationCodeBinding
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.ProgressBarUtil.hideProgressBar
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils

class VerificationCodeFragment : Fragment() {

    private var reconnectCount = 1

    private var binding: FragmentVerificationCodeBinding? = null

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerificationCodeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleBackPressed()
        addObservers()

        //TODO убрать
        fragment_verification_code_edittext_vc.setText(authViewModel.vCode.value)
        fragment_verification_code_button_next.isEnabled = true

        fragment_verification_code_resend_link.setOnClickListener {
            if (!authViewModel.isTimerRunning.value!!) {
                authViewModel.isTimerRunning.value = true
                authViewModel.startTimer(authViewModel.resendVCTimer.value!!)
                resendCode()
            }
        }

        fragment_verification_code_edittext_vc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fragment_verification_code_edittext_vc.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext)
                fragment_verification_code_button_next.isEnabled = s.toString().length == 6
                if (s.toString().length == 6) {
                    hideKeyboard()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        fragment_verification_code_button_next.setOnClickListener {
            if (StringUtils.equals(
                    fragment_verification_code_edittext_vc.text.toString(),
                    authViewModel.vCode.value
                )
            ) {
                confirmVCode(fragment_verification_code_edittext_vc.text.toString())
            } else {
                hideKeyboard(fragment_verification_code_edittext_vc)
                showErrorMessage(getString(R.string.dialog_wrong_verification_code_2_500), true)
            }
        }
    }

    private fun resendCode() {
        authViewModel.emailCheck(
            authViewModel.userEmail.value!!,
            object : ResultCallback<AuthenticationResponse> {
                override fun onResult(value: AuthenticationResponse?) {
                    authViewModel.vCode.value = value!!.vcode
                    fragment_verification_code_edittext_vc.setText(StringUtils.EMPTY)
                    fragment_verification_code_dialog_text.setText(R.string.dialog_verification_code)
                    fragment_verification_code_dialog_text.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.main_text
                        )
                    )
                    Log.d("CODE ================= ", value.vcode.toString())
                }

                override fun onFailure(value: AuthenticationResponse?) {
                }
            })
    }

    private fun showErrorMessage(message: String, dyeEditText: Boolean) {
        fragment_verification_code_dialog_text.text = message
        fragment_verification_code_dialog_text.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )

        if (dyeEditText) {
            fragment_verification_code_edittext_vc.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_corner_edittext_error
                )
        }
    }

    private fun showDefaultMessage(message: String) {
        fragment_verification_code_dialog_text.text = message
        fragment_verification_code_dialog_text.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )

            fragment_verification_code_edittext_vc.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_corner_edittext
                )
    }

    private fun confirmVCode(vCode: String) {
        Log.d("VCODE CONFIRM attempt", reconnectCount.toString())
        hideKeyboard(fragment_verification_code_edittext_vc)
        progressbar_register_vc.visibility = View.VISIBLE
        authViewModel.confirmVCode(
            vCode,
            authViewModel.userEmail.value!!,
            object : ResultCallback<AuthenticationResponse> {
                override fun onResult(value: AuthenticationResponse?) {
                    progressbar_register_vc.visibility = View.GONE
                    when (value?.code) {
                        0 -> {
                            navigateToRegisterPassword()
                        }
                        1 -> {
                            showErrorMessage(getString(R.string.dialog_wrong_verification_code_1), true)
                        }
                        2 -> {
                            showErrorMessage(getString(R.string.dialog_wrong_verification_code_2_500), true)
                        }
                        3 -> {
                            showErrorMessage(getString(R.string.dialog_wrong_verification_code_3), true)
                        }
                        else -> {
                            showErrorMessage(getString(R.string.dialog_wrong_verification_code_2_500), true)
                        }
                    }
                }

                override fun onFailure(value: AuthenticationResponse?) {
                    if (reconnectCount < 6) {
                        reconnectCount++
                        confirmVCode(vCode)
                    } else {
                        hideKeyboard(fragment_register_username_edittext_username)
                        showErrorMessage(getString(R.string.dialog_register_error), false)
                        progressbar_register_vc.visibility = View.GONE
                    }
                }
            })
    }

    private fun navigateToRegisterPassword() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_go_to_register_password)
    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (progressbar_register_vc.visibility == View.VISIBLE) {
                        progressbar_register_vc.visibility = View.GONE
                        showDefaultMessage(getString(R.string.dialog_register_email_error))
                    } else {
                        popBackStack()
                    }
                }
            })
    }

    private fun popBackStack() {
        NavHostFragment.findNavController(this).popBackStack()
    }

    private fun addObservers() {
        authViewModel.currentCountdown.observe(viewLifecycleOwner) {
            if (it == 0L) {
                //enable button
                fragment_verification_code_resend_link.isEnabled = true
                fragment_verification_code_resend_link.text =
                    resources.getString(R.string.resend_verification_code)
                fragment_verification_code_resend_link.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.vcEnabledColor_text,
                    )
                )
            } else {
                //disable button
                fragment_verification_code_resend_link.isEnabled = false
                fragment_verification_code_resend_link.text = String.format(
                    resources.getString(R.string.resend_verification_code_timer), it
                )
                fragment_verification_code_resend_link.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.vcDisabledColor_text,
                    )
                )
            }
        }
    }

}