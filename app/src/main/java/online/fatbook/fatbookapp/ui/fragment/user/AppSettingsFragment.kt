package online.fatbook.fatbookapp.ui.fragment.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_app_settings.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import online.fatbook.fatbookapp.databinding.FragmentAppSettingsBinding
import online.fatbook.fatbookapp.ui.activity.MainActivity
import online.fatbook.fatbookapp.ui.activity.SplashActivity
import online.fatbook.fatbookapp.util.Constants.SP_TAG
import online.fatbook.fatbookapp.util.Constants.SP_TAG_DARK_MODE
import online.fatbook.fatbookapp.util.Constants.SP_TAG_DARK_MODE_CHANGED

class AppSettingsFragment : Fragment() {
    private var binding: FragmentAppSettingsBinding? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppSettingsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    //TODO ANIM dark mode switch
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        val sharedPreferences =
                requireActivity().getSharedPreferences(SP_TAG, Context.MODE_PRIVATE)
        switch_app_theme.isChecked = sharedPreferences.getBoolean(SP_TAG_DARK_MODE, false)
        val intent = Intent(requireContext(), SplashActivity::class.java)
//        requireActivity().overridePendingTransition(0, 0)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        val editor = sharedPreferences.edit()
        switch_app_theme.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean(SP_TAG_DARK_MODE_CHANGED, true)
            when {
                isChecked -> {
                    editor.putBoolean(SP_TAG_DARK_MODE, true)
                    editor.apply()
                    requireActivity().finish()
                    requireActivity().startActivity(intent)
//                    requireActivity().overridePendingTransition(0, 0)
                    Log.d("MAINACTIVITY app settings", "finish() called, true")
                }
                else -> {
                    editor.putBoolean(SP_TAG_DARK_MODE, false)
                    editor.apply()

                    requireActivity().finish()
                    requireActivity().startActivity(intent)
//                    requireActivity().overridePendingTransition(0, 0)
                    Log.d("MAINACTIVITY app settings", "finish() called, false")
                }
            }
        }
    }

    private fun setupMenu() {
        toolbar_app_settings.setNavigationOnClickListener {
            popBackStack()
        }
    }

    private fun popBackStack() {
        NavHostFragment.findNavController(this).popBackStack()
    }
}