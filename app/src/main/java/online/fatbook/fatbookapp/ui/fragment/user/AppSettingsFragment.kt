package online.fatbook.fatbookapp.ui.fragment.user

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.fragment_app_settings.*
import online.fatbook.fatbookapp.databinding.FragmentAppSettingsBinding
import online.fatbook.fatbookapp.util.Constants.SP_TAG
import online.fatbook.fatbookapp.util.Constants.SP_TAG_DARK_MODE

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
        switch_app_theme.isChecked = sharedPreferences.getBoolean(SP_TAG_DARK_MODE, true)

        switch_app_theme.setOnCheckedChangeListener { _, isChecked ->

            val editor = sharedPreferences.edit()
            when {
                isChecked -> {
                    editor.putBoolean(SP_TAG_DARK_MODE, true)
                    editor.apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                else -> {
                    editor.putBoolean(SP_TAG_DARK_MODE, false)
                    editor.apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }
}