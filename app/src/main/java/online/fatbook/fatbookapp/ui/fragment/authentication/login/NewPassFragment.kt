package online.fatbook.fatbookapp.ui.fragment.authentication.login

import android.content.Context
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
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_new_pass.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.authentication.AuthenticationResponse
import online.fatbook.fatbookapp.databinding.FragmentNewPassBinding
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.Constants
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import java.util.regex.Pattern


class NewPassFragment : Fragment() {

    private var reconnectCount = 1
    private var isReconnectCancelled = false

    private var binding: FragmentNewPassBinding? = null
    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPassBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleBackPressed()

        fragment_new_pass_button_next.setOnClickListener {
            hideKeyboard(fragment_new_pass_edittext_repeat_new_password)
            if (fragment_new_pass_edittext_new_password.text.toString().contentEquals(
                    fragment_new_pass_edittext_repeat_new_password.text.toString()
                )
            ) {
                if (passwordValidate()) {
                    changePassword(fragment_new_pass_edittext_new_password.text.toString())
                } else {
                    showErrorMessage(getString(R.string.dialog_new_pass))
                }
            } else {
                showErrorMessage(getString(R.string.dialog_new_pass_passwords_do_not_match))
            }
        }

        fragment_new_pass_edittext_new_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length in 6..20) {
                    checkEditTextIsFilled(fragment_new_pass_edittext_new_password, true)
                } else {
                    checkEditTextIsFilled(fragment_new_pass_edittext_new_password, false)
                }
                enableButtonNext(
                    fragment_new_pass_edittext_new_password.text.toString(),
                    fragment_new_pass_edittext_repeat_new_password.text.toString()
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        fragment_new_pass_edittext_repeat_new_password.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length in 6..20) {
                    checkEditTextIsFilled(
                        fragment_new_pass_edittext_repeat_new_password,
                        true
                    )
                } else {
                    checkEditTextIsFilled(
                        fragment_new_pass_edittext_repeat_new_password,
                        false
                    )
                }
                enableButtonNext(
                    fragment_new_pass_edittext_new_password.text.toString(),
                    fragment_new_pass_edittext_repeat_new_password.text.toString()
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun changePassword(password: String) {
        progressbar_new_pass.visibility = View.VISIBLE
//        authViewModel.username.value = authViewModel.recoverUsername.value
//        authViewModel.password.value = value

//        println("username : ${authViewModel.username.value}")
//        println("password : ${authViewModel.password.value}")

        authViewModel.changePassword(
            authViewModel.recoverUsername.value.toString(),
            password,
            object : ResultCallback<AuthenticationResponse> {
                override fun onResult(value: AuthenticationResponse?) {
                    progressbar_new_pass.visibility = View.GONE
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
                            hideKeyboard(fragment_login_edittext_password)
                            showErrorMessage(getString(R.string.dialog_connection_error))
                            progressbar_login.visibility = View.GONE
                        }
                    }
                }
            })
    }

    private fun navigateToFeed() {
        NavHostFragment.findNavController(this).navigate(R.id.action_go_to_feed_from_new_pass)
    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (progressbar_new_pass.visibility == View.VISIBLE) {
                        progressbar_new_pass.visibility = View.GONE
                        showDefaultMessage(getString(R.string.dialog_new_pass))
                        isReconnectCancelled = true
                    } else {
                        popBackStack()
                    }
                }
            })
    }

    private fun popBackStack() {
        NavHostFragment.findNavController(this).popBackStack()
    }

    private fun saveUserAndProceed(username: String?, password: String) {
        authViewModel.username.value = username
        authViewModel.password.value = password
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
        fragment_new_pass_kuzya_dialog.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.cat_dialog_invalid_data
            )
        )
        fragment_new_pass_dialog_text.text = message
        fragment_new_pass_dialog_text.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )
    }

    private fun showDefaultMessage(message: String) {
        fragment_new_pass_kuzya_dialog.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.cat_dialog_invalid_data
            )
        )
        fragment_new_pass_dialog_text.text = message
        fragment_new_pass_dialog_text.setTextColor(
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
        fragment_new_pass_button_next.isEnabled =
            password.length in 6..20 && repeatPassword.length in 6..20
    }

    private fun passwordValidate(): Boolean {
        return Pattern.compile(Constants.PASSWORD_REGEX)
            .matcher(fragment_new_pass_edittext_new_password.text.toString()).matches()
    }
}