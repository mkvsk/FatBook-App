package online.fatbook.fatbookapp.ui.fragment.authentication.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_verification_code.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentVerificationCodeBinding
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils

class VerificationCodeFragment : Fragment() {

    private var binding: FragmentVerificationCodeBinding? = null

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerificationCodeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addObservers()

        fragment_verification_code_resend_link.setOnClickListener {
            if (!authViewModel.isTimerRunning.value!!) {
                authViewModel.isTimerRunning.value = true
                authViewModel.startTimer(authViewModel.resendVCTimer.value!!)
            }
        }

        fragment_verification_code_edittext_vc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fragment_verification_code_edittext_vc.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.round_corner_edittext)
                fragment_verification_code_button_next.isEnabled = s.toString().length == 6
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        fragment_verification_code_button_next.setOnClickListener {
            if (StringUtils.equals(
                    fragment_verification_code_edittext_vc.text.toString(),
                    authViewModel.vCode.value
                )
            ) {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_signup_password)
            } else {
                hideKeyboard(fragment_verification_code_edittext_vc)
                fragment_verification_code_dialog_text.text =
                    getString(R.string.dialog_wrong_verification_code)
                fragment_verification_code_dialog_text.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.dialogErrorMess_text
                    )
                )
                fragment_verification_code_edittext_vc.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.round_corner_edittext_error
                    )
            }
        }
    }

    private fun addObservers() {
        authViewModel.currentCountdown.observe(viewLifecycleOwner) {
            if (it == 0L) {
                //enable button
                fragment_verification_code_resend_link.isEnabled = true
                fragment_verification_code_resend_link.text =
                    resources.getString(R.string.resend_verification_code)
            } else {
                //disable button
                fragment_verification_code_resend_link.isEnabled = false
                fragment_verification_code_resend_link.text = String.format(
                    resources.getString(R.string.resend_verification_code_timer), it
                )

            }
        }
    }

}