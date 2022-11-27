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
import online.fatbook.fatbookapp.util.Constants.SYMBOL_AT
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel

class RegisterEmailFragment : Fragment() {

    private var reconnectCount = 1
    private var isReconnectCancelled = false

    private var _binding: FragmentRegisterEmailBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragmentRegisterEmailButtonNext.setOnClickListener {
            if (emailValidate(binding.fragmentRegisterEmailEdittextEmail.text.toString())) {
                if (authViewModel.userEmail.value!! != binding.fragmentRegisterEmailEdittextEmail.text.toString()) {
                    authViewModel.isTimerRunning.value = false
                    authViewModel.currentCountdown.value = 0
                    authViewModel.cancelTimer()
                    isReconnectCancelled = false
                    emailCheck(binding.fragmentRegisterEmailEdittextEmail.text.toString())
                } else {
                    if (authViewModel.isTimerRunning.value == false) {
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

        handleBackPressed()
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
        Log.d("EMAIL CHECK attempt", reconnectCount.toString())
        binding.loader.progressOverlayAuth.visibility = View.VISIBLE
        hideKeyboard(binding.fragmentRegisterEmailEdittextEmail)
        authViewModel.emailCheck(email, object : ResultCallback<AuthenticationResponse> {
            override fun onResult(value: AuthenticationResponse?) {
                binding.loader.progressOverlayAuth.visibility = View.GONE
                when (value!!.code) {
                    0 -> {
                        if (!isReconnectCancelled) {
                            authViewModel.userEmail.value = value.email
                            if (!authViewModel.isTimerRunning.value!!) {
                                authViewModel.isTimerRunning.value = true
                                authViewModel.startTimer(authViewModel.resendVCTimer.value!!)
                            }
                            authViewModel.vCode.value = value.vcode
                            Log.d("CODE ======================= ", value.vcode!!)
                            Toast.makeText(requireContext(), value.vcode, Toast.LENGTH_LONG)
                                .show()
                            navigateToVerificationCode()
                        }
                    }
                    4 -> {
                        showErrorMessage(
                            getString(R.string.dialog_email_used_register_email),
                            true
                        )
                    }
                    else -> {
                        showErrorMessage(getString(R.string.dialog_register_error), true)
                    }
                }
            }

            override fun onFailure(value: AuthenticationResponse?) {
                if (!isReconnectCancelled) {
                    if (reconnectCount < 6) {
                        reconnectCount++
                        emailCheck(email)
                    } else {
                        showErrorMessage(getString(R.string.dialog_register_error), false)
                        hideKeyboard(binding.fragmentRegisterEmailEdittextEmail)
                        binding.loader.progressOverlayAuth.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun navigateToVerificationCode() {
        NavHostFragment.findNavController(this).navigate(R.id.action_go_to_verification_code)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
