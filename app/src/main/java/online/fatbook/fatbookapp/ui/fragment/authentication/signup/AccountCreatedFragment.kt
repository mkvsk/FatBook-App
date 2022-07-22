package online.fatbook.fatbookapp.ui.fragment.authentication.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_account_created.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentAccountCreatedBinding
import online.fatbook.fatbookapp.ui.viewmodel.SignupViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class AccountCreatedFragment : Fragment() {
    private var binding: FragmentAccountCreatedBinding? = null

    private val signupViewModel by lazy { obtainViewModel(SignupViewModel::class.java) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountCreatedBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment_signup_account_created_dialog_text.text = String.format(getString(R.string.dialog_account_created), signupViewModel.username.value)
    }
}