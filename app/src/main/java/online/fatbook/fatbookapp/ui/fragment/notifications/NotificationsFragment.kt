package online.fatbook.fatbookapp.ui.fragment.notifications

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_notifications.*
import online.fatbook.fatbookapp.databinding.FragmentNotificationsBinding
import online.fatbook.fatbookapp.ui.listeners.BaseFragmentActions

class NotificationsFragment : Fragment(), BaseFragmentActions {

    private var binding: FragmentNotificationsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("===t=======NotificationsFragment==========", "onViewCreated")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("===t=======NotificationsFragment==========", "onDestroy")
    }

    override fun onBackPressedBase(): Boolean {
        return false
    }

    override fun scrollUpBase() {
        nsv_notifications.scrollTo(0, 0)
        appBarLayout_notifications.setExpanded(true, false)
    }
}