package online.fatbook.fatbookapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentViewImageBinding


class ViewImageFragment : Fragment() {

    private var binding: FragmentViewImageBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewImageBinding.inflate(inflater, container, false)
        return binding!!.root
    }

}