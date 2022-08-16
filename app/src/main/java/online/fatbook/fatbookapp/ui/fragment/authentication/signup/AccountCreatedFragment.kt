package online.fatbook.fatbookapp.ui.fragment.authentication.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_account_created.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentAccountCreatedBinding
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class AccountCreatedFragment : Fragment() {

    private var binding: FragmentAccountCreatedBinding? = null

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountCreatedBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment_signup_account_created_dialog_text.text =
            String.format(getString(R.string.dialog_account_created), authViewModel.username.value)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    saveUserData()
                    requireActivity().finish()
                }
            })
    }

    /**
     * TODO save data to shared prefs
     * email, username, password
     * settings - dark theme: false
     */
    private fun saveUserData() {
    }
}