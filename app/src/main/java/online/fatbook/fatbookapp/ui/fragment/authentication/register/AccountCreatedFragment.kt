package online.fatbook.fatbookapp.ui.fragment.authentication.register

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_account_created.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentAccountCreatedBinding
import online.fatbook.fatbookapp.ui.activity.MainActivity
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.Constants.SP_TAG
import online.fatbook.fatbookapp.util.Constants.SP_TAG_PASSWORD
import online.fatbook.fatbookapp.util.Constants.SP_TAG_USERNAME
import online.fatbook.fatbookapp.util.obtainViewModel

class AccountCreatedFragment : Fragment() {

    private var binding: FragmentAccountCreatedBinding? = null
    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountCreatedBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment_register_account_created_dialog_text.text =
            String.format(getString(R.string.dialog_account_created), authViewModel.username.value)

        saveUserDataToSharedPrefs()

        fragment_register_account_created_button_next.setOnClickListener {
            navigateToFeed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
    }

    private fun navigateToFeed() {
        requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun saveUserDataToSharedPrefs() {
        val sharedPreferences = requireActivity().getSharedPreferences(SP_TAG, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(SP_TAG_USERNAME, authViewModel.username.value)
        editor.putString(SP_TAG_PASSWORD, authViewModel.password.value)
        editor.apply()
    }
}