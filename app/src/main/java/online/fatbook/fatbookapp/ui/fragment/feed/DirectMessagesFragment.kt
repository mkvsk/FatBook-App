package online.fatbook.fatbookapp.ui.fragment.feed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentDirectMessagesBinding


class DirectMessagesFragment : Fragment() {

    private var binding: FragmentDirectMessagesBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDirectMessagesBinding.inflate(inflater, container, false)
        return binding!!.root
    }

}