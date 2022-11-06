package online.fatbook.fatbookapp.ui.fragment

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.*
import android.view.GestureDetector.OnDoubleTapListener
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.transition.AutoTransition
import androidx.transition.Fade
import androidx.transition.Scene
import androidx.transition.TransitionManager
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_image_view.*
import kotlinx.android.synthetic.main.fragment_user_profile.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentImageViewBinding
import online.fatbook.fatbookapp.ui.viewmodel.ImageViewModel
import online.fatbook.fatbookapp.util.obtainViewModel
import online.fatbook.fatbookapp.util.touchview.OnTouchImageViewListener

class ImageViewFragment : Fragment() {

    private var binding: FragmentImageViewBinding? = null
    private val imageViewModel by lazy { obtainViewModel(ImageViewModel::class.java) }
    private var toolbarIsVisible: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }

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

        imageview_full_image.setOnDoubleTapListener(object : OnDoubleTapListener {
            override fun onSingleTapConfirmed(p0: MotionEvent?): Boolean {
                if (toolbarIsVisible) {
                    appBarLayout_image_view.animate().alpha(0.0f).duration = 100
                    toolbarIsVisible = false
                } else {
                    appBarLayout_image_view.animate().alpha(1.0f).duration = 100
                    toolbarIsVisible = true
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

        imageview_full_image.setOnTouchImageViewListener(object : OnTouchImageViewListener {
            override fun onMove() {
            }

            override fun onMove(v: View, event: MotionEvent) {
                Log.d("TAG", "onMove: $v, $event")
            }
        })

//        imageview_full_image.setOnLongClickListener {
//            // Create a new ClipData.
//            // This is done in two steps to provide clarity. The convenience method
//            // ClipData.newPlainText() can create a plain text ClipData in one step.
//
//            // Create a new ClipData.Item from the ImageView object's tag.
//            val item = ClipData.Item(it.tag as? CharSequence)
//
//            // Create a new ClipData using the tag as a label, the plain text MIME type, and
//            // the already-created item. This creates a new ClipDescription object within the
//            // ClipData and sets its MIME type to "text/plain".
//            val dragData = ClipData(
//                it.tag as? CharSequence,
//                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
//                item)
//
//            // Instantiate the drag shadow builder.
//            val myShadow = MyDragShadowBuilder(it)
//
//            // Start the drag.
//            it.startDragAndDrop(dragData,  // The data to be dragged
//                myShadow,  // The drag shadow builder
//                null,      // No need to use local data
//                0          // Flags (not currently used, set to 0)
//            )
//
//            // Indicate that the long-click was handled.
//            true
//        }

//        imageview_full_image.setOnDragListener { v, e ->
//            when (e.action) {
//                DragEvent.ACTION_DRAG_STARTED -> {
//                    // Determines if this View can accept the dragged data.
//                    if (e.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
//                        // As an example of what your application might do, applies a blue color tint
//                        // to the View to indicate that it can accept data.
//                        (v as? TouchImageView)?.setColorFilter(Color.BLUE)
//
//                        // Invalidate the view to force a redraw in the new tint.
//                        v.invalidate()
//
//                        // Returns true to indicate that the View can accept the dragged data.
//                        true
//                    } else {
//                        // Returns false to indicate that, during the current drag and drop operation,
//                        // this View will not receive events again until ACTION_DRAG_ENDED is sent.
//                        false
//                    }
//                }
//                DragEvent.ACTION_DRAG_ENTERED -> {
//                    // Applies a green tint to the View.
//                    (v as? TouchImageView)?.setColorFilter(Color.GREEN)
//
//                    // Invalidates the view to force a redraw in the new tint.
//                    v.invalidate()
//
//                    // Returns true; the value is ignored.
//                    true
//                }
//
//                DragEvent.ACTION_DRAG_LOCATION ->
//                    // Ignore the event.
//                    true
//                DragEvent.ACTION_DRAG_EXITED -> {
//                    // Resets the color tint to blue.
//                    (v as? TouchImageView)?.setColorFilter(Color.BLUE)
//
//                    // Invalidates the view to force a redraw in the new tint.
//                    v.invalidate()
//
//                    // Returns true; the value is ignored.
//                    true
//                }
//                DragEvent.ACTION_DROP -> {
//                    // Gets the item containing the dragged data.
//                    val item: ClipData.Item = e.clipData.getItemAt(0)
//
//                    // Gets the text data from the item.
//                    val dragData = item.text
//
//                    // Displays a message containing the dragged data.
//                    Toast.makeText(requireContext(), "Dragged data is $dragData", Toast.LENGTH_LONG).show()
//
//                    // Turns off any color tints.
//                    (v as? ImageView)?.clearColorFilter()
//
//                    // Invalidates the view to force a redraw.
//                    v.invalidate()
//
//                    // Returns true. DragEvent.getResult() will return true.
//                    true
//                }
//
//                DragEvent.ACTION_DRAG_ENDED -> {
//                    // Turns off any color tinting.
//                    (v as? ImageView)?.clearColorFilter()
//
//                    // Invalidates the view to force a redraw.
//                    v.invalidate()
//
//                    // Does a getResult(), and displays what happened.
//                    when(e.result) {
//                        true ->
//                            Toast.makeText(requireContext(), "The drop was handled.", Toast.LENGTH_LONG)
//                        else ->
//                            Toast.makeText(requireContext(), "The drop didn't work.", Toast.LENGTH_LONG)
//                    }.show()
//
//                    // Returns true; the value is ignored.
//                    true
//                }
//                else -> {
//                    // An unknown action type was received.
//                    Log.e("DragDrop Example", "Unknown action type received by View.OnDragListener.")
//                    false
//                }
//            }
//        }
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