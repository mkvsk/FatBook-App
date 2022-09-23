package online.fatbook.fatbookapp.ui.fragment.authentication.signin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_sign_in.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.User
import online.fatbook.fatbookapp.databinding.FragmentSignInBinding
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel

class SignInFragment : Fragment() {

    private var binding: FragmentSignInBinding? = null

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_signin_button_signin.setOnClickListener {
            hideKeyboard(fragment_signin_edittext_password)
        }
        fragment_signin_edittext_username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length in 3..20) {
                    checkEditTextIsFilled(fragment_signin_edittext_username, true)
                } else {
                    checkEditTextIsFilled(fragment_signin_edittext_username, false)
                }
                enableButtonNext(
                    username = fragment_signin_edittext_username.text.toString(),
                    password = fragment_signin_edittext_password.text.toString()
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        fragment_signin_edittext_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length in 6..20) {
                    checkEditTextIsFilled(fragment_signin_edittext_password, true)
                } else {
                    checkEditTextIsFilled(fragment_signin_edittext_password, false)
                }
                enableButtonNext(
                    username = fragment_signin_edittext_username.text.toString(),
                    password = fragment_signin_edittext_password.text.toString()
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

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
        fragment_signin_button_signin.isEnabled =
            username.length in 3..20 && password.length in 6..20
    }

    private fun showErrorMessage(message: String) {
        fragment_signin_dialog_text.text = message
        fragment_signin_dialog_text.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )
    }

//    private fun signIn() {
////        showErrorMessage(getString(R.string.dialog_wrong_data_signin))
//        authViewModel.signIn(
//            fragment_signin_edittext_username.text.toString(),
//            fragment_signin_edittext_password.text.toString(), object : ResultCallback<User> {
//                override fun onResult(value: User?) {
//                    //TODO save user data to room
//                }
//            }
//        )
//    }

}