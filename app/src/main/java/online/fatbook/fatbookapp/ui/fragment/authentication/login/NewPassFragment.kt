package online.fatbook.fatbookapp.ui.fragment.authentication.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import online.fatbook.fatbookapp.databinding.FragmentNewPassBinding
import online.fatbook.fatbookapp.ui.activity.MainActivity
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.Constants
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import java.util.regex.Pattern


class NewPassFragment : Fragment() {

    private var reconnectCount = 1
    private var isReconnectCancelled = false

    private var _binding: FragmentNewPassBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleBackPressed()

        binding.fragmentNewPassButtonNext.setOnClickListener {
            hideKeyboard(binding.fragmentNewPassEdittextRepeatNewPassword)
            if (binding.fragmentNewPassEdittextNewPassword.text.toString().contentEquals(
                    binding.fragmentNewPassEdittextRepeatNewPassword.text.toString()
                )
            ) {
                if (passwordValidate()) {
                    changePassword(binding.fragmentNewPassEdittextNewPassword.text.toString())
                } else {
                    showErrorMessage(getString(R.string.dialog_new_pass))
                }
            } else {
                showErrorMessage(getString(R.string.dialog_new_pass_passwords_do_not_match))
            }
        }

        binding.fragmentNewPassEdittextNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length in 6..20) {
                    checkEditTextIsFilled(binding.fragmentNewPassEdittextNewPassword, true)
                } else {
                    checkEditTextIsFilled(binding.fragmentNewPassEdittextNewPassword, false)
                }
                enableButtonNext(
                    binding.fragmentNewPassEdittextNewPassword.text.toString(),
                    binding.fragmentNewPassEdittextRepeatNewPassword.text.toString()
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.fragmentNewPassEdittextRepeatNewPassword.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length in 6..20) {
                    checkEditTextIsFilled(
                        binding.fragmentNewPassEdittextRepeatNewPassword,
                        true
                    )
                } else {
                    checkEditTextIsFilled(
                        binding.fragmentNewPassEdittextRepeatNewPassword,
                        false
                    )
                }
                enableButtonNext(
                    binding.fragmentNewPassEdittextNewPassword.text.toString(),
                    binding.fragmentNewPassEdittextRepeatNewPassword.text.toString()
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun changePassword(password: String) {
        binding.loader.progressOverlayAuth.visibility = View.VISIBLE
//        authViewModel.username.value = authViewModel.recoverUsername.value
//        authViewModel.password.value = value

//        println("username : ${authViewModel.username.value}")
//        println("password : ${authViewModel.password.value}")

        authViewModel.changePassword(
            authViewModel.recoverUsername.value!!,
            password,
            object : ResultCallback<AuthenticationResponse> {
                override fun onResult(value: AuthenticationResponse?) {
                    when (value!!.code) {
                        0 -> {
                            if (!isReconnectCancelled) {
                                saveUserAndProceed(value.username, password)
                            }
                        }
                        6 -> {
                            showErrorMessage(getString(R.string.dialog_recover_pass_user_not_found))
                        }
                        else -> showErrorMessage(getString(R.string.dialog_connection_error))
                    }
                }

                override fun onFailure(value: AuthenticationResponse?) {
                    if (!isReconnectCancelled) {
                        if (reconnectCount < 6) {
                            reconnectCount++
                            changePassword(password)
                        } else {
//                            hideKeyboard(fragment_login_edittext_password)
                            showErrorMessage(getString(R.string.dialog_connection_error))
                            binding.loader.progressOverlayAuth.visibility = View.GONE
                        }
                    }
                }
            })
    }

    private fun navigateToFeed() {
        requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.loader.progressOverlayAuth.visibility == View.VISIBLE) {
                        binding.loader.progressOverlayAuth.visibility = View.GONE
                        showDefaultMessage(getString(R.string.dialog_new_pass))
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

    private fun saveUserAndProceed(username: String?, password: String) {
        authViewModel.setUsername(username!!)
        authViewModel.setPassword(password)
        val sharedPreferences = requireActivity().getSharedPreferences(
            Constants.SP_TAG,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(Constants.SP_TAG_USERNAME, authViewModel.username.value)
        editor.putString(Constants.SP_TAG_PASSWORD, authViewModel.password.value)
        editor.apply()
        navigateToFeed()
    }

    private fun showErrorMessage(message: String) {
        binding.fragmentNewPassKuzyaDialog.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.cat_dialog_invalid_data
            )
        )
        binding.fragmentNewPassDialogText.text = message
        binding.fragmentNewPassDialogText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )
    }

    private fun showDefaultMessage(message: String) {
        binding.fragmentNewPassKuzyaDialog.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.cat_dialog_invalid_data
            )
        )
        binding.fragmentNewPassDialogText.text = message
        binding.fragmentNewPassDialogText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.main_text
            )
        )
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

    private fun enableButtonNext(password: String, repeatPassword: String) {
        binding.fragmentNewPassButtonNext.isEnabled =
            password.length in 6..20 && repeatPassword.length in 6..20
    }

    private fun passwordValidate(): Boolean {
        return Pattern.compile(Constants.PASSWORD_REGEX)
            .matcher(binding.fragmentNewPassEdittextNewPassword.text.toString()).matches()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}