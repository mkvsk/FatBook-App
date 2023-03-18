package online.fatbook.fatbookapp.ui.authentication.register

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import online.fatbook.fatbookapp.MainActivity
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentAccountCreatedBinding
import online.fatbook.fatbookapp.ui.authentication.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.Constants.SP_TAG
import online.fatbook.fatbookapp.util.Constants.SP_TAG_PASSWORD
import online.fatbook.fatbookapp.util.Constants.SP_TAG_USERNAME
import online.fatbook.fatbookapp.util.obtainViewModel

class AccountCreatedFragment : Fragment() {

    private var _binding: FragmentAccountCreatedBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountCreatedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initListeners()
        saveUserDataToSharedPrefs()
    }

    private fun initViews() {
        binding.fragmentRegisterAccountCreatedDialogText.text =
            String.format(
                getString(R.string.dialog_account_created),
                authViewModel.username.value
            )
    }

    private fun initListeners() {
        binding.fragmentRegisterAccountCreatedButtonNext.setOnClickListener {
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}