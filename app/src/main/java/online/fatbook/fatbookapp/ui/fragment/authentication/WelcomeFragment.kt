package online.fatbook.fatbookapp.ui.fragment.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentWelcomeBinding
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() {
        binding.fragmentWelcomeButtonRegister.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_register_from_welcome)
        }
        binding.fragmentWelcomeButtonLogin.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_go_to_login_from_welcome)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}