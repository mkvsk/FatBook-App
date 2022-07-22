package online.fatbook.fatbookapp.ui.fragment.authentication.signin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import online.fatbook.fatbookapp.databinding.FragmentSigninNewPassBinding

class SigninNewPassFragment : Fragment() {
    private var binding: FragmentSigninNewPassBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSigninNewPassBinding.inflate(inflater, container, false)
        return binding!!.root
    }

}