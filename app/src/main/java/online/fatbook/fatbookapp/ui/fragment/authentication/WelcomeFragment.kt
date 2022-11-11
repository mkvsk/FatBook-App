package online.fatbook.fatbookapp.ui.fragment.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_welcome.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentWelcomeBinding
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class WelcomeFragment : Fragment() {

    private var binding: FragmentWelcomeBinding? = null

    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_welcome_button_register.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_go_to_register_from_welcome)
        }
        fragment_welcome_button_login.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_go_to_login_from_welcome)
        }

    }

}