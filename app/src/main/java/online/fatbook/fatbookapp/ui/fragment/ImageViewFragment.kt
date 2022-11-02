package online.fatbook.fatbookapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_image_view.*
import online.fatbook.fatbookapp.databinding.FragmentImageViewBinding
import online.fatbook.fatbookapp.ui.viewmodel.ImageViewModel
import online.fatbook.fatbookapp.util.obtainViewModel


class ImageViewFragment : Fragment() {

    private var binding: FragmentImageViewBinding? = null
    private val imageViewModel by lazy { obtainViewModel(ImageViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageViewBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar_image_view.setNavigationOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }
        imageViewModel.image.value.let {
            Glide
                .with(requireContext())
                .load(it)
                .into(imageview_full_image)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        imageViewModel.image.value = null
    }
}