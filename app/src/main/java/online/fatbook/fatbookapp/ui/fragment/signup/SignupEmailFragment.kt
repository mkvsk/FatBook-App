package online.fatbook.fatbookapp.ui.fragment.signup

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_signup_email.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentSignupEmailBinding
import online.fatbook.fatbookapp.ui.viewmodel.SignupViewModel
import online.fatbook.fatbookapp.util.obtainViewModel
import java.util.regex.Pattern

class SignupEmailFragment : Fragment() {

    private var binding: FragmentSignupEmailBinding? = null

    private val signupViewModel by lazy { obtainViewModel(SignupViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupEmailBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_signup_email_button_next.setOnClickListener {
            if (emailValidate()) {
                if (emailCheck()) {
                    signupViewModel.email.value = fragment_signup_email_edittext_email.text.toString()
                    NavHostFragment.findNavController(this).navigate(R.id.action_go_to_verification_code)
                    sendVerificationCode(signupViewModel.email.value!!)
                } else {
                    //TODO почта уже используется
                }
            } else {
                //TODO почта инвалиндая
            }
        }
    }

    //TODO api call
    private fun sendVerificationCode(value: String) {
    }

    //TODO api call
    private fun emailCheck(): Boolean {
        return true
    }

    private fun emailValidate(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(fragment_signup_email_edittext_email.text).matches()
    }
}