package online.fatbook.fatbookapp.ui.fragment.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_app_settings.*
import online.fatbook.fatbookapp.databinding.FragmentAppSettingsBinding
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences =
                requireActivity().getSharedPreferences(SP_TAG, Context.MODE_PRIVATE)
        switch_app_theme.isChecked = sharedPreferences.getBoolean(SP_TAG_DARK_MODE, false)
        val intent = Intent(requireContext(), SplashActivity::class.java)
        val editor = sharedPreferences.edit()
        switch_app_theme.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean(SP_TAG_DARK_MODE_CHANGED, true)
            when {
                isChecked -> {
                    editor.putBoolean(SP_TAG_DARK_MODE, true)
                    editor.apply()
                    requireActivity().startActivity(intent)
                    requireActivity().finish()
                }
                else -> {
                    editor.putBoolean(SP_TAG_DARK_MODE, false)
                    editor.apply()
                    requireActivity().startActivity(intent)
                    requireActivity().finish()
                }
            }
        }
    }
}