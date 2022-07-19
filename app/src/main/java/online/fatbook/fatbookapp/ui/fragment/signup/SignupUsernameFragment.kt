package online.fatbook.fatbookapp.ui.fragment.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_signup_username.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentSignupUsernameBinding
import online.fatbook.fatbookapp.ui.viewmodel.SignupViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class SignupUsernameFragment : Fragment() {
    private var binding: FragmentSignupUsernameBinding? = null

    private val signupViewModel by lazy { obtainViewModel(SignupViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupUsernameBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_signup_username_button_next.setOnClickListener {
            if (usernameValidate(fragment_signup_username_edittext_username.text.toString())) {
                if (usernameCheck(fragment_signup_username_edittext_username.text.toString())) {
                    signupViewModel.username.value = fragment_signup_username_edittext_username.text.toString()
                    createNewUser()
                }

            }
        }
    }

    //TODO api call create new user
    private fun createNewUser() {
        println(signupViewModel.email.value)
        println(signupViewModel.password.value)
        println(signupViewModel.username.value)

        NavHostFragment.findNavController(this)
            .navigate(R.id.action_go_to_account_created)


    }

    //TODO api call
    private fun usernameCheck(username: String): Boolean {
        return true
    }

    private fun usernameValidate(username: String): Boolean {
        return true

    }
}