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
import androidx.navigation.fragment.findNavController
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.databinding.FragmentLoginRecoverPassBinding
import online.fatbook.fatbookapp.network.AuthenticationResponse
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.ui.viewmodel.TimerViewModel
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel

class LoginRecoverPassword : Fragment() {

    private var _binding: FragmentLoginRecoverPassBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }
    private val timerViewModel by lazy { obtainViewModel(TimerViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginRecoverPassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackPressed()
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        authViewModel.recoverIdentifier.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.fragmentLoginRecoverPassEdittextUsername.setText(authViewModel.recoverIdentifier.value)
                enableButtonNext(binding.fragmentLoginRecoverPassEdittextUsername.text.toString())
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
        binding.fragmentLoginRecoverPassEdittextUsername.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length in 3..20) {
                    checkEditTextIsFilled(binding.fragmentLoginRecoverPassEdittextUsername, true)
                } else {
                    checkEditTextIsFilled(binding.fragmentLoginRecoverPassEdittextUsername, false)
                }
                enableButtonNext(binding.fragmentLoginRecoverPassEdittextUsername.text.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.fragmentLoginRecoverPassButtonNext.setOnClickListener {
            if (authViewModel.recoverIdentifier.value!! != binding.fragmentLoginRecoverPassEdittextUsername.text.toString()) {
                timerViewModel.setIsTimerRunning(false)
                timerViewModel.setCurrentCountdown(0)
                timerViewModel.cancelTimer()
                recoverPassword(binding.fragmentLoginRecoverPassEdittextUsername.text.toString())
            } else {
                if (timerViewModel.isTimerRunning.value == false) {
                    recoverPassword(binding.fragmentLoginRecoverPassEdittextUsername.text.toString())
                } else {
                    navigateToVerificationCode()
                }
            }
        }
    }

    private fun recoverPassword(identifier: String) {
        Log.d("RECOVER PASSWORD attempt", "")
        authViewModel.setIsLoading(true)
        hideKeyboard(binding.fragmentLoginRecoverPassEdittextUsername)
        authViewModel.recoverPassword(identifier, object : ResultCallback<AuthenticationResponse> {
            override fun onResult(value: AuthenticationResponse?) {
                when (value!!.code) {
                    0 -> {
                        if (!timerViewModel.isTimerRunning.value!!) {
                            timerViewModel.setIsTimerRunning(true)
                            timerViewModel.startTimer(timerViewModel.resendVCTimer.value!!)
                        }
                        authViewModel.setRecoverIdentifier(identifier)
                        authViewModel.setRecoverEmail(value.email.toString())
                        authViewModel.setRecoverUsername(value.username.toString())
                        authViewModel.setVCode(value.vcode.toString())
                        Log.d("CODE ======================= ", value.vcode!!)
                        Toast.makeText(requireContext(), value.vcode, Toast.LENGTH_LONG)
                            .show()
                        navigateToVerificationCode()
                    }
                    6 -> {
                        authViewModel.setIsLoading(false)
                        showErrorMessage(getString(R.string.dialog_recover_pass_user_not_found))
                    }
                    else -> {
                        authViewModel.setIsLoading(false)
                        showErrorMessage(getString(R.string.dialog_register_error))
                    }
                }
            }

            override fun onFailure(value: AuthenticationResponse?) {
                showErrorMessage(getString(R.string.dialog_register_error))
                hideKeyboard(binding.fragmentLoginRecoverPassEdittextUsername)
                binding.loader.progressOverlayAuth.visibility = View.GONE
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
        binding.fragmentLoginRecoverPassButtonNext.isEnabled = username.length in 3..20
    }

    private fun showErrorMessage(message: String) {
        binding.fragmentLoginRecoverPassDialogText.text = message
        binding.fragmentLoginRecoverPassDialogText.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.dialogErrorMess_text
            )
        )
    }

    private fun showDefaultMessage(message: String) {
        binding.fragmentLoginRecoverPassDialogText.text = message
        binding.fragmentLoginRecoverPassDialogText.setTextColor(
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
                    if (authViewModel.isLoading.value == true) {
                        authViewModel.setIsLoading(false)
                        showDefaultMessage(getString(R.string.dialog_register_email_default))
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