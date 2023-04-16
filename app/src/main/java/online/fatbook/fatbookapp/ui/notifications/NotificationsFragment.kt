package online.fatbook.fatbookapp.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentNotificationsBinding
import online.fatbook.fatbookapp.ui.navigation.listeners.BaseFragmentActionsListener

class NotificationsFragment : Fragment(), BaseFragmentActionsListener {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("===t=======NotificationsFragment==========", "onViewCreated")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("===t=======NotificationsFragment==========", "onDestroy")
        _binding = null
    }

    override fun onBackPressedBase(): Boolean {
        return false
    }

    override fun scrollUpBase() {
        binding.nsvNotifications.scrollTo(0, 0)
        binding.appBarLayoutNotifications.setExpanded(true, false)
    }

    override fun openFragment() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility =
            View.VISIBLE
    }
}