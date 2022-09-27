package online.fatbook.fatbookapp.ui.fragment.authentication.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_register_password.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentRegisterPasswordBinding
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.ProgressBarUtil.hideProgressBar
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel

class RegisterPasswordFragment : Fragment() {
    private var binding: FragmentRegisterPasswordBinding? = null

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterPasswordBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideProgressBar()
        fragment_register_password_button_next.setOnClickListener {
            if (passwordValidate()) {
                authViewModel.password.value =
                    fragment_register_password_edittext_password.text.toString()
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_register_username)
            } else {
                hideKeyboard(fragment_register_password_edittext_password)
                showErrorMessage()
            }
        }

        fragment_register_password_edittext_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fragment_register_password_edittext_password.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext)
                fragment_register_password_button_next.isEnabled = s.toString().length in 6..16
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun showErrorMessage() {
        fragment_register_password_button_next.isEnabled = false
        fragment_register_password_dialog_text.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )
        fragment_register_password_edittext_password.text = null
        fragment_register_password_edittext_password.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext_error)
    }

    //TODO remove
    private fun passwordValidate(): Boolean {
        return true
//        return Pattern.compile(PASSWORD_REGEX).matcher(fragment_register_password_edittext_password.text.toString()).matches()
    }

}