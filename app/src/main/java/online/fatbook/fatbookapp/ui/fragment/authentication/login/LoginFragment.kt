package online.fatbook.fatbookapp.ui.fragment.authentication.login

import android.content.Context
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
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.include_progress_overlay.*
import kotlinx.android.synthetic.main.include_progress_overlay_auth.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.authentication.LoginResponse
import online.fatbook.fatbookapp.databinding.FragmentLoginBinding
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.Constants
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel

class LoginFragment : Fragment() {

    private var reconnectCount = 1
    private var isReconnectCancelled = false

    private var binding: FragmentLoginBinding? = null
    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleBackPressed()

        fragment_login_button_login.setOnClickListener {
            hideKeyboard(fragment_login_edittext_password)
            login(
                fragment_login_edittext_username.text.toString(),
                fragment_login_edittext_password.text.toString()
            )
        }

        fragment_login_forgot_password_link.setOnClickListener {
            authViewModel.recoverIdentifier.value = fragment_login_edittext_username.text.toString()
            NavHostFragment.findNavController(this).navigate(R.id.action_go_to_login_recover_pass_from_login)
        }

        fragment_login_edittext_username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length in 3..20) {
                    checkEditTextIsFilled(fragment_login_edittext_username, true)
                } else {
                    if (s.toString().isEmpty()) {
                        checkEditTextIsFilled(fragment_login_edittext_username, true)
                    } else {
                        checkEditTextIsFilled(fragment_login_edittext_username, false)
                    }
                }
                enableButtonNext(
                    username = fragment_login_edittext_username.text.toString(),
                    password = fragment_login_edittext_password.text.toString()
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        fragment_login_edittext_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length in 6..20) {
                    checkEditTextIsFilled(fragment_login_edittext_password, true)
                } else {
                    if (s.toString().isEmpty()) {
                        checkEditTextIsFilled(fragment_login_edittext_password, true)
                    } else {
                        checkEditTextIsFilled(fragment_login_edittext_password, false)
                    }
                }
                enableButtonNext(
                    username = fragment_login_edittext_username.text.toString(),
                    password = fragment_login_edittext_password.text.toString()
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun login(username: String, password: String) {
        Log.d("LOGIN attempt", reconnectCount.toString())
        progress_overlay_auth.visibility = View.VISIBLE
        val request: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("username", username)
            .addFormDataPart("password", password).build()
        authViewModel.login(request, object : ResultCallback<LoginResponse> {
            override fun onResult(value: LoginResponse?) {
                if (!isReconnectCancelled) {
                    if (value != null) {
                        authViewModel.username.value = username
                        authViewModel.password.value = password
                        authViewModel.jwtAccess.value = value.access_token
                        authViewModel.jwtRefresh.value = value.refresh_token
                        authViewModel.isUserAuthenticated.value = true
                        RetrofitFactory.updateJWT(value.access_token!!)
                        saveUserDataToSharedPrefs()
                        navigateToFeed()
                    } else {
                        showErrorMessage("Sequence not found")
                        progress_overlay_auth.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(value: LoginResponse?) {
                if (!isReconnectCancelled) {
                    if (reconnectCount < 6) {
                        reconnectCount++
                        login(username, password)
                    } else {
                        hideKeyboard(fragment_login_edittext_password)
                        showErrorMessage(getString(R.string.dialog_register_error))
                        progress_overlay_auth.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun navigateToFeed() {
        NavHostFragment.findNavController(this).navigate(R.id.action_go_to_feed_from_login)
    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (progress_overlay_auth.visibility == View.VISIBLE) {
                        progress_overlay_auth.visibility = View.GONE
                        showDefaultMessage(getString(R.string.dialog_register_email_error))
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

    private fun saveUserDataToSharedPrefs() {
        val sharedPreferences = requireActivity().getSharedPreferences(
            Constants.SP_TAG,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(Constants.SP_TAG_USERNAME, authViewModel.username.value)
        editor.putString(Constants.SP_TAG_PASSWORD, authViewModel.password.value)
        editor.apply()
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

    private fun enableButtonNext(username: String, password: String) {
        fragment_login_button_login.isEnabled =
            username.length in 3..20 && password.length in 6..20
    }

    private fun showErrorMessage(message: String) {
        fragment_login_dialog_text.text = message
        fragment_login_dialog_text.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )
    }

    private fun showDefaultMessage(message: String) {
        fragment_login_dialog_text.text = message
        fragment_login_dialog_text.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.main_text
            )
        )
    }
}