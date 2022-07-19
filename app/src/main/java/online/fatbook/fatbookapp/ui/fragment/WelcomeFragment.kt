package online.fatbook.fatbookapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_welcome.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private var binding: FragmentWelcomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_welcome_button_signup.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_go_to_signup_email)
        }
    }

}