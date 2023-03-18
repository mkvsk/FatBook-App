package online.fatbook.fatbookapp.ui.user.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import online.fatbook.fatbookapp.databinding.FragmentAppSettingsBinding
import online.fatbook.fatbookapp.SplashActivity
import online.fatbook.fatbookapp.util.Constants.SP_TAG
import online.fatbook.fatbookapp.util.Constants.SP_TAG_DARK_MODE
import online.fatbook.fatbookapp.util.Constants.SP_TAG_DARK_MODE_CHANGED

class AppSettingsFragment : Fragment() {
    private var _binding: FragmentAppSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    //TODO ANIM dark mode switch
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        val sharedPreferences =
            requireActivity().getSharedPreferences(SP_TAG, Context.MODE_PRIVATE)
        binding.switchAppTheme.isChecked = sharedPreferences.getBoolean(SP_TAG_DARK_MODE, false)
        val intent = Intent(requireContext(), SplashActivity::class.java)
        val editor = sharedPreferences.edit()
        binding.switchAppTheme.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean(SP_TAG_DARK_MODE_CHANGED, true)
            when {
                isChecked -> {
                    editor.putBoolean(SP_TAG_DARK_MODE, true)
                    editor.apply()
                    requireActivity().finish()
                    requireActivity().startActivity(intent)
                    Log.d("MAINACTIVITY app settings", "finish() called, true")
                }
                else -> {
                    editor.putBoolean(SP_TAG_DARK_MODE, false)
                    editor.apply()

                    requireActivity().finish()
                    requireActivity().startActivity(intent)
                    Log.d("MAINACTIVITY app settings", "finish() called, false")
                }
            }
        }
    }

    private fun setupMenu() {
        binding.toolbar.setNavigationOnClickListener {
            popBackStack()
        }
    }

    private fun popBackStack() {
        findNavController().popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}