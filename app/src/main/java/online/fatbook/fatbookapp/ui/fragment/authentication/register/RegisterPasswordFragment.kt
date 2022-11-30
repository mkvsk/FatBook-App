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
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentRegisterPasswordBinding
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.ProgressBarUtil.hideProgressBar
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel

class RegisterPasswordFragment : Fragment() {
    private var _binding: FragmentRegisterPasswordBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideProgressBar()
        binding.fragmentRegisterPasswordButtonNext.setOnClickListener {
            if (passwordValidate()) {
                authViewModel.setPassword(binding.fragmentRegisterPasswordEdittextPassword.text.toString())
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_register_username)
            } else {
                hideKeyboard(binding.fragmentRegisterPasswordEdittextPassword)
                showErrorMessage()
            }
        }

        binding.fragmentRegisterPasswordEdittextPassword.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.fragmentRegisterPasswordEdittextPassword.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext)
                binding.fragmentRegisterPasswordButtonNext.isEnabled = s.toString().length in 6..16
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun showErrorMessage() {
        binding.fragmentRegisterPasswordButtonNext.isEnabled = false
        binding.fragmentRegisterPasswordDialogText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dialogErrorMess_text
            )
        )
        binding.fragmentRegisterPasswordEdittextPassword.text = null
        binding.fragmentRegisterPasswordEdittextPassword.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext_error)
    }

    //TODO remove
    private fun passwordValidate(): Boolean {
        return true
//        return Pattern.compile(PASSWORD_REGEX).matcher(binding.fragmentRegisterPasswordEdittextPassword.text.toString()).matches()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}