package online.fatbook.fatbookapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.GestureDetector.OnDoubleTapListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_image_view.*
import kotlinx.android.synthetic.main.fragment_user_profile.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentImageViewBinding
import online.fatbook.fatbookapp.ui.viewmodel.ImageViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class ImageViewFragment : Fragment() {

    private var binding: FragmentImageViewBinding? = null
    private val imageViewModel by lazy { obtainViewModel(ImageViewModel::class.java) }

    //TODO ANIM fragment creation
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageViewBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = View.GONE
        toolbar_image_view.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        setupMenu()
        imageview_full_image.setOnDoubleTapListener(object : OnDoubleTapListener{
            override fun onSingleTapConfirmed(p0: MotionEvent?): Boolean {
                //TODO ANIM toolbar collapse
                if (toolbar_image_view.visibility == View.VISIBLE) {
                    toolbar_image_view.visibility = View.GONE
                } else {
                    toolbar_image_view.visibility = View.VISIBLE
                }
                return true
            }

            override fun onDoubleTap(p0: MotionEvent?): Boolean {
                return true
            }

            override fun onDoubleTapEvent(p0: MotionEvent?): Boolean {
                return true
            }
        })
        imageViewModel.image.value.let {
            Glide
                .with(requireContext())
                .load(it)
                .into(imageview_full_image)
        }
    }

    private fun setupMenu() {
        toolbar_image_view.inflateMenu(R.menu.image_view_menu)
        toolbar_image_view.setOnMenuItemClickListener(this::onOptionsItemSelected)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_image_view_report -> {
                Toast.makeText(requireContext(), "Reported", Toast.LENGTH_SHORT).show()
                //TODO report image
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        imageViewModel.image.value = null
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = View.VISIBLE
    }
}