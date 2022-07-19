package online.fatbook.fatbookapp.ui.fragment.signup

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_verification_code.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentVerificationCodeBinding
import online.fatbook.fatbookapp.ui.viewmodel.SignupViewModel
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils
import java.lang.Exception

class VerificationCodeFragment : Fragment() {

    private var binding: FragmentVerificationCodeBinding? = null

    private val signUpViewModel by lazy { obtainViewModel(SignupViewModel::class.java) }

    var timer = 10

    var handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerificationCodeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startTimer()
        fragment_verification_code_resend_link.isEnabled = false

        fragment_verification_code_resend_link.setOnClickListener {
            fragment_verification_code_resend_link.isEnabled = false
            timer = 10
            startTimer()
        }

        fragment_verification_code_button_next.setOnClickListener {
            val value = signUpViewModel.VCCode.value
            if (StringUtils.equals(
                    fragment_verification_code_edittext_vc.text.toString(),
                    signUpViewModel.VCCode.value
                )
            ) {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_signup_password)
            } else {
                //TODO invalid code
            }
        }
    }

    private fun startTimer() {

        context?.let {
            handler.postDelayed({
                if (timer > -1) {
                    fragment_verification_code_resend_link.text = String.format(
                        resources.getString(R.string.resend_verification_code_timer), timer
                    )
                    timer -= 1
                    startTimer();
                } else {
                    fragment_verification_code_resend_link.text =
                        resources.getString(R.string.resend_verification_code)
                    fragment_verification_code_resend_link.isEnabled = true
                }
            }, 1000L)
        }

    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

}