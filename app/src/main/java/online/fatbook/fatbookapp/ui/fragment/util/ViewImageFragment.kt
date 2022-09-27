package online.fatbook.fatbookapp.ui.fragment.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.fragment_view_image.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentViewImageBinding
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.obtainViewModel


class ViewImageFragment : Fragment() {

    private var binding: FragmentViewImageBinding? = null
   // private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewImageBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showData()
    }

    private fun showData() {

        Glide
            .with(requireContext())
            .load("https://fatbook.b-cdn.net/root/upal.jpg")
            .into(imageview_full_image)
    }

}