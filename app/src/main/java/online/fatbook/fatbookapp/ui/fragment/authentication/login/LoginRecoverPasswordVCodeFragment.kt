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
import online.fatbook.fatbookapp.ui.viewmodel.TimerViewModel
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils

class LoginRecoverPasswordVCodeFragment : Fragment() {

    private var _binding: FragmentLoginRecoverPassVerificationCodeBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }
    private val timerViewModel by lazy { obtainViewModel(TimerViewModel::class.java) }

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
        initListeners()
        initObservers()
        initViews()
    }

    private fun initViews() {
        binding.fragmentLoginRecoverPassVcodeDialogText.text = String.format(
            getString(R.string.dialog_recover_pass_vcode),
            getEmailHidden()
        )
    }

    private fun initListeners() {
        //TODO убрать
//        binding.fragmentLoginRecoverPassVcodeEdittextVc.setText(authViewModel.vCode.value)
//        binding.fragmentLoginRecoverPassVcodeButtonNext.isEnabled = true

        binding.fragmentLoginRecoverPassVcodeResendLink.setOnClickListener {
            if (!timerViewModel.isTimerRunning.value!!) {
                timerViewModel.setIsTimerRunning(true)
                timerViewModel.startTimer(timerViewModel.resendVCTimer.value!!)
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
                confirmVCode(binding.fragmentLoginRecoverPassVcodeEdittextVc.text.toString())
            } else {
                hideKeyboard(binding.fragmentLoginRecoverPassVcodeEdittextVc)
                showErrorMessage(getString(R.string.dialog_wrong_verification_code_2_500), true)
            }
        }
    }

    private fun initObservers() {
        authViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.loader.progressOverlayAuth.visibility = View.VISIBLE
            } else {
                binding.loader.progressOverlayAuth.visibility = View.GONE
            }
        }

        timerViewModel.currentCountdown.observe(viewLifecycleOwner) {
            if (it == 0L) {
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

        authViewModel.vCode.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.fragmentLoginRecoverPassVcodeEdittextVc.setText(StringUtils.EMPTY)
                binding.fragmentLoginRecoverPassVcodeDialogText.setText(R.string.dialog_verification_code)
                binding.fragmentLoginRecoverPassVcodeDialogText.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.main_text
                    )
                )
                Log.d("CODE ================= ", authViewModel.vCode.value.toString())
            }
        }

        authViewModel.resultCodeVCode.observe(viewLifecycleOwner) {
            when (it) {
                0 -> {
                    showDefaultMessage(getString(R.string.dialog_register_email_error))
                    navigateToNewPassFragment()
                }
                -1 -> {

                    showErrorMessage(authViewModel.error.value.toString(), false)
                }
                null -> {
                    showDefaultMessage(getString(R.string.dialog_register_email_error))
                }
                else -> {
                    showErrorMessage(authViewModel.error.value.toString(), true)
                }
            }
        }
    }

    private fun getEmailHidden(): String {
        return authViewModel.recoverEmail.value!!
    }

    private fun resendCode() {
        hideKeyboard(binding.fragmentLoginRecoverPassVcodeEdittextVc)
        authViewModel.setResultCodeRecoverPass(null)
        authViewModel.recoverPassword(authViewModel.recoverIdentifier.value!!)
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
        Log.d("VCODE CONFIRM attempt", "")
        hideKeyboard(binding.fragmentLoginRecoverPassVcodeEdittextVc)
        authViewModel.setIsLoading(true)
        authViewModel.confirmVCode(vCode)
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
                    if (authViewModel.isLoading.value == true) {
                        showDefaultMessage(getString(R.string.dialog_register_email_error))
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
