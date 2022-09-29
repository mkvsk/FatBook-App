package online.fatbook.fatbookapp.ui.fragment.util

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.fragment_app_settings.*
import kotlinx.android.synthetic.main.include_progress_overlay.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentAppSettingsBinding
import online.fatbook.fatbookapp.util.Constants
import org.apache.commons.lang3.StringUtils

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
        progress_overlay.visibility = View.GONE

        switch_app_theme.setOnClickListener {
            progress_overlay.visibility = View.VISIBLE

            val handler = Handler()
            handler.postDelayed({
                progress_overlay.visibility = View.GONE
            }, 1500)
        }

        switch_app_theme.setOnCheckedChangeListener { _, isChecked ->
            val sharedPreferences = requireActivity().getSharedPreferences(
                Constants.SP_TAG, Context.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()

            if (isChecked) {
                editor.putBoolean(Constants.SP_TAG_DARK_MODE, true)
                editor.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                editor.putBoolean(Constants.SP_TAG_DARK_MODE, false)
                editor.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}