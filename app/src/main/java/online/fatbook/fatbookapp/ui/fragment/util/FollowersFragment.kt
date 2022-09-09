package online.fatbook.fatbookapp.ui.fragment.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import online.fatbook.fatbookapp.databinding.FragmentFollowersBinding

class FollowersFragment : Fragment() {

    private var binding: FragmentFollowersBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowersBinding.inflate(inflater, container, false)
        return binding!!.root
    }

}