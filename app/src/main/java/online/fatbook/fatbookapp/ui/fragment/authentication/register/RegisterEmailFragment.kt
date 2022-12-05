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
import androidx.navigation.fragment.findNavController
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.network.AuthenticationResponse
import online.fatbook.fatbookapp.databinding.FragmentRegisterEmailBinding
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.ui.viewmodel.TimerViewModel
import online.fatbook.fatbookapp.util.Constants.SYMBOL_AT
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel

class RegisterEmailFragment : Fragment() {

    private var _binding: FragmentRegisterEmailBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }
    private val timerViewModel by lazy { obtainViewModel(TimerViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleBackPressed()
        authViewModel.setResultCode(null)
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.fragmentRegisterEmailButtonNext.setOnClickListener {
            if (emailValidate(binding.fragmentRegisterEmailEdittextEmail.text.toString())) {
                if (authViewModel.userEmail.value!! != binding.fragmentRegisterEmailEdittextEmail.text.toString()) {
                    timerViewModel.setIsTimerRunning(false)
                    timerViewModel.setCurrentCountdown(0)
                    timerViewModel.cancelTimer()
                    emailCheck(binding.fragmentRegisterEmailEdittextEmail.text.toString())
                } else {
                    if (timerViewModel.isTimerRunning.value == false) {
                        emailCheck(binding.fragmentRegisterEmailEdittextEmail.text.toString())
                    } else {
                        navigateToVerificationCode()
                    }
                }
            } else {
                hideKeyboard(binding.fragmentRegisterEmailEdittextEmail)
                showErrorMessage(getString(R.string.dialog_wrong_data_register_email), true)
            }
        }

        binding.fragmentRegisterEmailEdittextEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.fragmentRegisterEmailEdittextEmail.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext)
                if (s != null) {
                    binding.fragmentRegisterEmailButtonNext.isEnabled = s.contains(SYMBOL_AT)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun initObservers() {
        authViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.loader.progressOverlayAuth.visibility = View.VISIBLE
            } else {
                binding.loader.progressOverlayAuth.visibility = View.GONE
            }
        }

        authViewModel.resultCode.observe(viewLifecycleOwner) {
            when (it) {
                0 -> {
                    if (timerViewModel.isTimerRunning.value!!) {
                        timerViewModel.setIsTimerRunning(true)
                        timerViewModel.startTimer(timerViewModel.resendVCTimer.value!!)
                    }
                    navigateToVerificationCode()
                }
                -1 -> {
                    showErrorMessage(authViewModel.error.value.toString(), false)
                    hideKeyboard(binding.fragmentRegisterEmailEdittextEmail)
                }
                null -> {
                    showDefaultMessage(getString(R.string.dialog_register_email_error))
                }
                else -> {
                    showErrorMessage(authViewModel.error.value.toString(), true)
                    hideKeyboard(binding.fragmentRegisterEmailEdittextEmail)
                }
            }
        }

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

    private fun showErrorMessage(message: String, dyeEditText: Boolean) {
        binding.fragmentRegisterEmailDialogText.text = message;
        binding.fragmentRegisterEmailDialogText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )

        if (dyeEditText) {
            binding.fragmentRegisterEmailEdittextEmail.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext_error)
        }
    }

    private fun showDefaultMessage(message: String) {
        binding.fragmentRegisterEmailDialogText.text = message;
        binding.fragmentRegisterEmailDialogText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.main_text
            )
        )
        binding.fragmentRegisterEmailEdittextEmail.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext)
    }

    private fun emailValidate(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    private fun emailCheck(email: String) {
        Log.d("EMAIL CHECK attempt", "")
        authViewModel.setIsLoading(true)
        hideKeyboard(binding.fragmentRegisterEmailEdittextEmail)
        authViewModel.emailCheck(email)

//        authViewModel.emailCheck(email, object : ResultCallback<AuthenticationResponse> {
//            override fun onResult(value: AuthenticationResponse?) {
//                binding.loader.progressOverlayAuth.visibility = View.GONE
//                when (value!!.code) {
//                    0 -> {
//                        if (!isReconnectCancelled) {
//                            authViewModel.setUsername(value.email!!)
//                            if (!timerViewModel.isTimerRunning.value!!) {
//                                timerViewModel.setIsTimerRunning(true)
//                                timerViewModel.startTimer(timerViewModel.resendVCTimer.value!!)
//                            }
//                            authViewModel.setVCode(value.vcode!!)
//                            Log.d("CODE ======================= ", value.vcode!!)
//                            Toast.makeText(requireContext(), value.vcode, Toast.LENGTH_LONG)
//                                .show()
//                            navigateToVerificationCode()
//                        }
//                    }
//                    4 -> {
//                        showErrorMessage(
//                            getString(R.string.dialog_email_used_register_email),
//                            true
//                        )
//                    }
//                    else -> {
//                        showErrorMessage(getString(R.string.dialog_register_error), true)
//                    }
//                }
//            }
//
//            override fun onFailure(value: AuthenticationResponse?) {
//                if (!isReconnectCancelled) {
//                    if (reconnectCount < 6) {
//                        reconnectCount++
//                        emailCheck(email)
//                    } else {
//                        showErrorMessage(getString(R.string.dialog_register_error), false)
//                        hideKeyboard(binding.fragmentRegisterEmailEdittextEmail)
//                        binding.loader.progressOverlayAuth.visibility = View.GONE
//                    }
//                }
//            }
//        })
    }

    private fun navigateToVerificationCode() {
        NavHostFragment.findNavController(this).navigate(R.id.action_go_to_verification_code)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}