package online.fatbook.fatbookapp.ui.fragment.authentication.login

import android.content.Context
import android.content.Intent
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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentLoginBinding
import online.fatbook.fatbookapp.ui.activity.MainActivity
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.Constants
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel

class LoginFragment : Fragment() {

    private var reconnectCount = 1
    private var isReconnectCancelled = false

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackPressed()
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        authViewModel.isUserAuthenticated.observe(viewLifecycleOwner) {
            if (it) {
                saveUserDataToSharedPrefs()
                navigateToFeed()
            }
        }

        authViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.loader.progressOverlayAuth.visibility = View.VISIBLE
            } else {
                binding.loader.progressOverlayAuth.visibility = View.GONE
            }
        }

        authViewModel.resultCodeAuth.observe(viewLifecycleOwner) {
            when (it) {
                1 -> {
                    showDefaultMessage(getString(R.string.dialog_register_email_error))
                }
                -1 -> {
                    hideKeyboard(binding.fragmentLoginEdittextUsername)
                    hideKeyboard(binding.fragmentLoginEdittextPassword)
                    showErrorMessage(authViewModel.error.value.toString())
                }
            }
        }
    }

    private fun initListeners() {
        binding.fragmentLoginButtonLogin.setOnClickListener {
            hideKeyboard(binding.fragmentLoginEdittextPassword)
            login(
                binding.fragmentLoginEdittextUsername.text.toString(),
                binding.fragmentLoginEdittextPassword.text.toString()
            )
        }

        //TODO fix
        binding.fragmentLoginForgotPasswordLink.setOnClickListener {
            var recoverIdentifier: String = authViewModel.recoverIdentifier.value!!
            recoverIdentifier =
                binding.fragmentLoginEdittextUsername.text.toString()
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_login_recover_pass_from_login)
        }

        binding.fragmentLoginEdittextUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length in 3..20) {
                    checkEditTextIsFilled(binding.fragmentLoginEdittextUsername, true)
                } else {
                    if (s.toString().isEmpty()) {
                        checkEditTextIsFilled(binding.fragmentLoginEdittextUsername, true)
                    } else {
                        checkEditTextIsFilled(binding.fragmentLoginEdittextUsername, false)
                    }
                }
                enableButtonNext(
                    username = binding.fragmentLoginEdittextUsername.text.toString(),
                    password = binding.fragmentLoginEdittextPassword.text.toString()
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.fragmentLoginEdittextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length in 6..20) {
                    checkEditTextIsFilled(binding.fragmentLoginEdittextPassword, true)
                } else {
                    if (s.toString().isEmpty()) {
                        checkEditTextIsFilled(binding.fragmentLoginEdittextPassword, true)
                    } else {
                        checkEditTextIsFilled(binding.fragmentLoginEdittextPassword, false)
                    }
                }
                enableButtonNext(
                    username = binding.fragmentLoginEdittextUsername.text.toString(),
                    password = binding.fragmentLoginEdittextPassword.text.toString()
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun login(username: String, password: String) {
        Log.d("LOGIN attempt", "for user $username/$password #$reconnectCount")
        val request: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("username", username)
            .addFormDataPart("password", password).build()
        authViewModel.login(request, password)
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
                    if (authViewModel.isLoading.value == true) {
                        authViewModel.setIsLoading(false)
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
        binding.fragmentLoginButtonLogin.isEnabled =
            username.length in 3..20 && password.length in 6..20
    }

    private fun showErrorMessage(message: String) {
        binding.fragmentLoginDialogText.text = message
        binding.fragmentLoginDialogText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )
    }

    private fun showDefaultMessage(message: String) {
        binding.fragmentLoginDialogText.text = message
        binding.fragmentLoginDialogText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.main_text
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        authViewModel.setResultCodeAuth(null)
        authViewModel.setError("")
    }
}