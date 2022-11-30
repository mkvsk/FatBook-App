package online.fatbook.fatbookapp.ui.fragment.authentication.login

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
import androidx.navigation.fragment.findNavController
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.network.AuthenticationResponse
import online.fatbook.fatbookapp.databinding.FragmentLoginRecoverPassVerificationCodeBinding
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils

class LoginRecoverPasswordVCodeFragment : Fragment() {

    private var reconnectCount = 1
    private var isReconnectCancelled = false

    private var _binding: FragmentLoginRecoverPassVerificationCodeBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentLoginRecoverPassVerificationCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleBackPressed()
        addObservers()

        binding.fragmentLoginRecoverPassVcodeDialogText.text = String.format(
            getString(R.string.dialog_recover_pass_vcode),
            getEmailHidden()
        )


        //TODO убрать
        binding.fragmentLoginRecoverPassVcodeEdittextVc.setText(authViewModel.vCode.value)
        binding.fragmentLoginRecoverPassVcodeButtonNext.isEnabled = true

        binding.fragmentLoginRecoverPassVcodeResendLink.setOnClickListener {
            if (!authViewModel.isTimerRunning.value!!) {
                authViewModel.isTimerRunning.value = true
                authViewModel.startTimer(authViewModel.resendVCTimer.value!!)
                resendCode()
            }
        }

        binding.fragmentLoginRecoverPassVcodeEdittextVc.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.fragmentLoginRecoverPassVcodeEdittextVc.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext)
                binding.fragmentLoginRecoverPassVcodeButtonNext.isEnabled = s.toString().length == 6
                if (s.toString().length == 6) {
                    hideKeyboard()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.fragmentLoginRecoverPassVcodeButtonNext.setOnClickListener {
            if (StringUtils.equals(
                    binding.fragmentLoginRecoverPassVcodeEdittextVc.text.toString(),
                    authViewModel.vCode.value
                )
            ) {
                isReconnectCancelled = false
                confirmVCode(binding.fragmentLoginRecoverPassVcodeEdittextVc.text.toString())
            } else {
                hideKeyboard(binding.fragmentLoginRecoverPassVcodeEdittextVc)
                showErrorMessage(getString(R.string.dialog_wrong_verification_code_2_500), true)
            }
        }
    }

    private fun getEmailHidden(): String {
        return authViewModel.recoverEmail.value!!
    }

    private fun resendCode() {
        authViewModel.recoverPassword(
            authViewModel.recoverIdentifier.value!!,
            object : ResultCallback<AuthenticationResponse> {
                override fun onResult(value: AuthenticationResponse?) {
                    authViewModel.setVCode(value!!.vcode.toString())
                    binding.fragmentLoginRecoverPassVcodeEdittextVc.setText(StringUtils.EMPTY)
                    binding.fragmentLoginRecoverPassVcodeDialogText.setText(R.string.dialog_verification_code)
                    binding.fragmentLoginRecoverPassVcodeDialogText.setTextColor(
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
        binding.fragmentLoginRecoverPassVcodeDialogText.text = message
        binding.fragmentLoginRecoverPassVcodeDialogText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )

        if (dyeEditText) {
            binding.fragmentLoginRecoverPassVcodeEdittextVc.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_corner_edittext_error
                )
        }
    }

    private fun showDefaultMessage(message: String) {
        binding.fragmentLoginRecoverPassVcodeDialogText.text = message
        binding.fragmentLoginRecoverPassVcodeDialogText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )

        binding.fragmentLoginRecoverPassVcodeEdittextVc.background =
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.round_corner_edittext
            )
    }

    private fun confirmVCode(vCode: String) {
        Log.d("VCODE CONFIRM attempt", reconnectCount.toString())
        hideKeyboard(binding.fragmentLoginRecoverPassVcodeEdittextVc)
        binding.loader.progressOverlayAuth.visibility = View.VISIBLE
        authViewModel.confirmVCode(
            vCode,
            authViewModel.recoverEmail.value!!,
            object : ResultCallback<AuthenticationResponse> {
                override fun onResult(value: AuthenticationResponse?) {
                    binding.loader.progressOverlayAuth.visibility = View.GONE
                    when (value!!.code) {
                        0 -> {
                            if (!isReconnectCancelled) {
                                navigateToNewPassFragment()
                            }
                        }
                        1 -> {
                            showErrorMessage(
                                getString(R.string.dialog_wrong_verification_code_1),
                                true
                            )
                        }
                        2 -> {
                            showErrorMessage(
                                getString(R.string.dialog_wrong_verification_code_2_500),
                                true
                            )
                        }
                        3 -> {
                            showErrorMessage(
                                getString(R.string.dialog_wrong_verification_code_3),
                                true
                            )
                        }
                        else -> {
                            showErrorMessage(
                                getString(R.string.dialog_wrong_verification_code_2_500),
                                true
                            )
                        }
                    }
                }

                override fun onFailure(value: AuthenticationResponse?) {
                    if (!isReconnectCancelled) {
                        if (reconnectCount < 6) {
                            reconnectCount++
                            confirmVCode(vCode)
                        } else {
                            hideKeyboard(binding.fragmentLoginRecoverPassVcodeEdittextVc)
                            showErrorMessage(getString(R.string.dialog_register_error), false)
                            binding.loader.progressOverlayAuth.visibility = View.GONE
                        }
                    }
                }
            })
    }

    private fun navigateToNewPassFragment() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_go_to_new_pass_from_login_vcode)
    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.loader.progressOverlayAuth.visibility == View.VISIBLE) {
                        showDefaultMessage(getString(R.string.dialog_register_email_error))
                        binding.loader.progressOverlayAuth.visibility = View.GONE
                        isReconnectCancelled = true
                    } else {
                        popBackStack()
                    }
                }
            })
    }

    private fun popBackStack() {
        findNavController().popBackStack()
    }

    private fun addObservers() {
        authViewModel.currentCountdown.observe(viewLifecycleOwner) {
            if (it == 0L) {
                //enable button
                binding.fragmentLoginRecoverPassVcodeResendLink.isEnabled = true
                binding.fragmentLoginRecoverPassVcodeResendLink.text =
                    resources.getString(R.string.resend_verification_code)
                binding.fragmentLoginRecoverPassVcodeResendLink.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.vcEnabledColor_text,
                    )
                )
            } else {
                //disable button
                binding.fragmentLoginRecoverPassVcodeResendLink.isEnabled = false
                binding.fragmentLoginRecoverPassVcodeResendLink.text = String.format(
                    resources.getString(R.string.resend_verification_code_timer), it
                )
                binding.fragmentLoginRecoverPassVcodeResendLink.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.vcDisabledColor_text,
                    )
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}