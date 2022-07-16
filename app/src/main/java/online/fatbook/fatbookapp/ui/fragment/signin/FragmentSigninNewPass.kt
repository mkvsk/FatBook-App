package online.fatbook.fatbookapp.ui.fragment.signin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentSigninNewPassBinding

class FragmentSigninNewPass : Fragment() {
    private var binding: FragmentSigninNewPassBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSigninNewPassBinding.inflate(inflater, container, false)
        return binding!!.root
    }

}