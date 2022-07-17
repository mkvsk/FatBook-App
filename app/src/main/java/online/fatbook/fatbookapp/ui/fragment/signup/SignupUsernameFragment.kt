package online.fatbook.fatbookapp.ui.fragment.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentSignupUsernameBinding

class SignupUsernameFragment : Fragment() {
    private var binding: FragmentSignupUsernameBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupUsernameBinding.inflate(inflater, container, false)
        return binding!!.root
    }

}