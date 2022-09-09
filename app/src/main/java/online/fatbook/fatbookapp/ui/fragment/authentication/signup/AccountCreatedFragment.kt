package online.fatbook.fatbookapp.ui.fragment.authentication.signup

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_account_created.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentAccountCreatedBinding
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.Constants.SP_TAG
import online.fatbook.fatbookapp.util.Constants.SP_TAG_USERNAME
import online.fatbook.fatbookapp.util.Constants.SP_TAG_PASSWORD
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils

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

        saveUserData()

        fragment_signup_account_created_button_next.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_go_to_edit_profile_from_account_created)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
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
        val sharedPreferences =
            requireActivity().getSharedPreferences(SP_TAG, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(SP_TAG_USERNAME, authViewModel.username.value)
        editor.putString(SP_TAG_PASSWORD, authViewModel.password.value)
        editor.apply()
    }
}