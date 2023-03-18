package online.fatbook.fatbookapp.ui.user.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentBadgesBinding

class BadgesFragment : Fragment() {

    private var _binding: FragmentBadgesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBadgesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
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