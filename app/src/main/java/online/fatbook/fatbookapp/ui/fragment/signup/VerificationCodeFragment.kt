package online.fatbook.fatbookapp.ui.fragment.signup

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_verification_code.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentVerificationCodeBinding

class VerificationCodeFragment : Fragment() {
    private var binding: FragmentVerificationCodeBinding? = null
    var timer = 10
    var handler = Handler()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVerificationCodeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_verification_code_resend_link.setOnClickListener {
            fragment_verification_code_resend_link.isEnabled = false
            timer = 60
            startTimer()
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

}