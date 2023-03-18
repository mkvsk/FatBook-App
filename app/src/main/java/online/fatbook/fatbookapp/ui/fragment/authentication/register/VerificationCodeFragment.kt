package online.fatbook.fatbookapp.ui.fragment.authentication.register

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
import androidx.navigation.fragment.findNavController
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.network.AuthenticationResponse
import online.fatbook.fatbookapp.databinding.FragmentVerificationCodeBinding
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.ui.viewmodel.TimerViewModel
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils

class VerificationCodeFragment : Fragment() {

    private var _binding: FragmentVerificationCodeBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }
    private val timerViewModel by lazy { obtainViewModel(TimerViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerificationCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleBackPressed()
        initListeners()
        initObservers()
        authViewModel.setIsLoading(false)
        //TODO убрать
//        binding.fragmentVerificationCodeEdittextVc.setText(authViewModel.vCode.value)
//        binding.fragmentVerificationCodeButtonNext.isEnabled = true
    }

    private fun initObservers() {
        timerViewModel.currentCountdown.observe(viewLifecycleOwner) {
            if (it == 0L) {
                binding.fragmentVerificationCodeResendLink.isEnabled = true
                binding.fragmentVerificationCodeResendLink.text =
                    resources.getString(R.string.resend_verification_code)
                binding.fragmentVerificationCodeResendLink.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.vcEnabledColor_text,
                    )
                )
            } else {
                binding.fragmentVerificationCodeResendLink.isEnabled = false
                binding.fragmentVerificationCodeResendLink.text = String.format(
                    resources.getString(R.string.resend_verification_code_timer), it
                )
                binding.fragmentVerificationCodeResendLink.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.vcDisabledColor_text,
                    )
                )
            }
        }

        authViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.loader.progressOverlayAuth.visibility = View.VISIBLE
            } else {
                binding.loader.progressOverlayAuth.visibility = View.GONE
            }
        }
    }

    private fun initListeners() {
        binding.fragmentVerificationCodeResendLink.setOnClickListener {
            if (!timerViewModel.isTimerRunning.value!!) {
                timerViewModel.setIsTimerRunning(true)
                timerViewModel.startTimer(timerViewModel.resendVCTimer.value!!)
                resendCode()
            }
        }

        binding.fragmentVerificationCodeEdittextVc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.fragmentVerificationCodeEdittextVc.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext)
                binding.fragmentVerificationCodeButtonNext.isEnabled = s.toString().length == 6
                if (s.toString().length == 6) {
                    hideKeyboard()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.fragmentVerificationCodeButtonNext.setOnClickListener {
            if (StringUtils.equals(
                    binding.fragmentVerificationCodeEdittextVc.text.toString(),
                    authViewModel.vCode.value
                )
            ) {
                confirmVCode(binding.fragmentVerificationCodeEdittextVc.text.toString())
            } else {
                hideKeyboard(binding.fragmentVerificationCodeEdittextVc)
                showErrorMessage(getString(R.string.dialog_wrong_verification_code_2_500), true)
            }
        }
    }

    private fun resendCode() {
        hideKeyboard(binding.fragmentVerificationCodeEdittextVc)
        authViewModel.emailCheck(
            authViewModel.userEmail.value!!,
            object : ResultCallback<AuthenticationResponse> {
                override fun onResult(value: AuthenticationResponse?) {
                    authViewModel.setVCode(value?.vcode!!)
                    binding.fragmentVerificationCodeEdittextVc.setText(StringUtils.EMPTY)
                    binding.fragmentVerificationCodeDialogText.setText(R.string.dialog_verification_code)
                    binding.fragmentVerificationCodeDialogText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.main_text
                        )
                    )
                    Log.d("CODE ================= ", value.vcode.toString())
                    Toast.makeText(requireContext(), value.vcode.toString(), Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(value: AuthenticationResponse?) {
                }
            })
    }

    private fun showErrorMessage(message: String, dyeEditText: Boolean) {
        binding.fragmentVerificationCodeDialogText.text = message
        binding.fragmentVerificationCodeDialogText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )

        if (dyeEditText) {
            binding.fragmentVerificationCodeEdittextVc.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_corner_edittext_error
                )
        }
    }

    private fun showDefaultMessage(message: String) {
        binding.fragmentVerificationCodeDialogText.text = message
        binding.fragmentVerificationCodeDialogText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.main_text
            )
        )

        binding.fragmentVerificationCodeEdittextVc.background =
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.round_corner_edittext
            )
    }

    private fun confirmVCode(vCode: String) {
        Log.d("VCODE CONFIRM attempt", "")
        hideKeyboard(binding.fragmentVerificationCodeEdittextVc)
        authViewModel.setIsLoading(true)
        authViewModel.confirmVCode(
            vCode,
            authViewModel.userEmail.value!!,
            object : ResultCallback<AuthenticationResponse> {
                override fun onResult(value: AuthenticationResponse?) {
                    when (value?.code) {
                        0 -> {
                            navigateToRegisterPassword()
                        }
                        1 -> {
                            authViewModel.setIsLoading(false)
                            showErrorMessage(
                                getString(R.string.dialog_wrong_verification_code_1),
                                true
                            )
                        }
                        2 -> {
                            authViewModel.setIsLoading(false)
                            showErrorMessage(
                                getString(R.string.dialog_wrong_verification_code_2_500),
                                true
                            )
                        }
                        3 -> {
                            authViewModel.setIsLoading(false)
                            showErrorMessage(
                                getString(R.string.dialog_wrong_verification_code_3),
                                true
                            )
                        }
                        else -> {
                            authViewModel.setIsLoading(false)
                            showErrorMessage(
                                getString(R.string.dialog_wrong_verification_code_2_500),
                                true
                            )
                        }
                    }
                }

                override fun onFailure(value: AuthenticationResponse?) {
                    hideKeyboard(binding.fragmentVerificationCodeEdittextVc)
                    showErrorMessage(getString(R.string.dialog_register_error), false)
                    binding.loader.progressOverlayAuth.visibility = View.GONE
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
                    if (authViewModel.isLoading.value == true) {
                        showDefaultMessage(getString(R.string.dialog_register_email_default))
                        authViewModel.setIsLoading(false)
                    } else {
                        popBackStack()
                    }
                }
            })
    }

    private fun popBackStack() {
        findNavController().popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}