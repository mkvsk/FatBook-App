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
import androidx.navigation.fragment.findNavController
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

    private var _binding: FragmentRegisterUsernameBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterUsernameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentRegisterUsernameButtonNext.setOnClickListener {
            if (usernameValidate()) {
                authViewModel.setUsername(binding.fragmentRegisterUsernameEdittextUsername.text.toString())
                isReconnectCancelled = false
                createNewUser()
            } else {
                hideKeyboard(binding.fragmentRegisterUsernameEdittextUsername)
                showErrorMessage(getString(R.string.dialog_register_username_invalid), true)
            }
        }

        binding.fragmentRegisterUsernameEdittextUsername.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.fragmentRegisterUsernameEdittextUsername.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext)
                binding.fragmentRegisterUsernameButtonNext.isEnabled = s.toString().length in 3..20
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    binding.fragmentRegisterUsernameDialogText.text =
                        getString(R.string.dialog_register_username)
                    binding.fragmentRegisterUsernameDialogText.setTextColor(
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
        binding.loader.progressOverlayAuth.visibility = View.VISIBLE
        hideKeyboard(binding.fragmentRegisterUsernameEdittextUsername)
        authViewModel.register(
            AuthenticationRequest(
                authViewModel.username.value,
                authViewModel.password.value,
                authViewModel.userEmail.value
            ), object : ResultCallback<AuthenticationResponse> {
                override fun onResult(value: AuthenticationResponse?) {
                    binding.loader.progressOverlayAuth.visibility = View.GONE
                    value?.let {
                        when (it.code) {
                            0 -> {
                                if (!isReconnectCancelled) {
                                    navigateToAccountCreated()
                                }
                            }
                            4 -> {
                                hideKeyboard(binding.fragmentRegisterUsernameEdittextUsername)
                                showErrorMessage(
                                    getString(R.string.dialog_register_email_error), true
                                )
                            }
                            5 -> {
                                hideKeyboard(binding.fragmentRegisterUsernameEdittextUsername)
                                showErrorMessage(
                                    getString(R.string.dialog_register_username_unavailable), true
                                )
                            }
                            else -> {
                                hideKeyboard(binding.fragmentRegisterUsernameEdittextUsername)
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
                            hideKeyboard(binding.fragmentRegisterUsernameEdittextUsername)
                            showErrorMessage(getString(R.string.dialog_register_error), false)
                            binding.loader.progressOverlayAuth.visibility = View.GONE
                        }
                    }
                }
            })
    }

    private fun showErrorMessage(message: String, dyeEditText: Boolean) {
        binding.fragmentRegisterUsernameButtonNext.isEnabled = false
        binding.fragmentRegisterUsernameDialogText.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.dialogErrorMess_text
            )
        )
        binding.fragmentRegisterUsernameDialogText.text = message
        if (dyeEditText) {
            binding.fragmentRegisterUsernameEdittextUsername.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext_error)
        }
    }

    private fun showDefaultMessage(message: String) {
        binding.fragmentRegisterUsernameButtonNext.isEnabled = false
        binding.fragmentRegisterUsernameDialogText.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.dialogErrorMess_text
            )
        )
        binding.fragmentRegisterUsernameDialogText.text = message;
        binding.fragmentRegisterUsernameEdittextUsername.background =
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
                    if (binding.loader.progressOverlayAuth.visibility == View.VISIBLE) {
                        binding.loader.progressOverlayAuth.visibility = View.GONE
                        showDefaultMessage(getString(R.string.dialog_register_email_error))
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

    private fun usernameValidate(): Boolean {
        return Pattern.compile(USERNAME_REGEX)
            .matcher(binding.fragmentRegisterUsernameEdittextUsername.text).matches()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}