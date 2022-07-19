package online.fatbook.fatbookapp.ui.fragment.signin

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_sign_in.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.R.*
import online.fatbook.fatbookapp.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {
    private var binding: FragmentSignInBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding!!.root
    }


}