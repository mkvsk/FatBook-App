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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.authentication.LoginResponse
import online.fatbook.fatbookapp.databinding.FragmentAccountCreatedBinding
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import online.fatbook.fatbookapp.ui.activity.SplashActivity
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.util.Constants.SP_TAG
import online.fatbook.fatbookapp.util.Constants.SP_TAG_PASSWORD
import online.fatbook.fatbookapp.util.Constants.SP_TAG_USERNAME
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
        fragment_register_account_created_dialog_text.text =
            String.format(getString(R.string.dialog_account_created), authViewModel.username.value)

        saveUserData()

        fragment_register_account_created_button_next.setOnClickListener {
            val request: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", authViewModel.username.value.toString())
                .addFormDataPart("password", authViewModel.password.value.toString())
                .build()
            authViewModel.login(request, object : ResultCallback<LoginResponse> {
                override fun onResult(value: LoginResponse?) {
                    if (value != null) {
                        saveData(value)
                        navigateToFeed()
                    } else {
//                        navigateToWelcome()
                        //TODO sign in try again
                    }
                }

                override fun onFailure(value: LoginResponse?) {

                }
            })
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
    }

    private fun navigateToWelcome() {
        val sharedPreferences =
            requireActivity().getSharedPreferences(SP_TAG, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(SP_TAG_USERNAME, StringUtils.EMPTY)
        editor.putString(SP_TAG_PASSWORD, StringUtils.EMPTY)
        editor.apply()
        startActivity(Intent(requireActivity(), SplashActivity::class.java))
        requireActivity().finish()
    }

    private fun saveData(value: LoginResponse) {
        authViewModel.jwtAccess.value = value.access_token
        authViewModel.jwtRefresh.value = value.refresh_token
        RetrofitFactory.updateJWT(value.access_token!!)
    }

    private fun navigateToFeed() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_go_to_feed_from_account_created)
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