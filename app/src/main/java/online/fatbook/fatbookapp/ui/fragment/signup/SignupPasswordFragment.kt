package online.fatbook.fatbookapp.ui.fragment.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentSignupPasswordBinding

class SignupPasswordFragment : Fragment() {
    private var binding: FragmentSignupPasswordBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupPasswordBinding.inflate(inflater, container, false)
        return binding!!.root
    }

}