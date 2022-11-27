package online.fatbook.fatbookapp.ui.fragment.notifications

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import online.fatbook.fatbookapp.databinding.FragmentNotificationsBinding
import online.fatbook.fatbookapp.ui.listeners.BaseFragmentActionsListener

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
}