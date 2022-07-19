package online.fatbook.fatbookapp.ui.fragment.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_signup_password.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentSignupPasswordBinding
import online.fatbook.fatbookapp.ui.viewmodel.SignupViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class SignupPasswordFragment : Fragment() {
    private var binding: FragmentSignupPasswordBinding? = null

    private val signupViewModel by lazy { obtainViewModel(SignupViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupPasswordBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment_signup_password_button_next.setOnClickListener {
            if (passwordCheck(
                    fragment_signup_password_edittext_password.text.toString()
                )
            ) {
                signupViewModel.password.value =
                    fragment_signup_password_edittext_password.text.toString()
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_go_to_signup_username)
            } else {
                //TODO INVALID
            }

        }
    }

    private fun passwordCheck(password: String): Boolean {
        return true
    }
}