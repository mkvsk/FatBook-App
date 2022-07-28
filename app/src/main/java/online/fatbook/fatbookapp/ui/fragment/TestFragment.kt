package online.fatbook.fatbookapp.ui.fragment

import android.content.Context
import android.graphics.drawable.StateListDrawable
import android.location.GnssAntennaInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.transition.AutoTransition
import androidx.transition.Scene
import androidx.transition.TransitionManager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_test.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentTestBinding

class TestFragment : Fragment() {

    private var binding: FragmentTestBinding? = null

    private var expanded: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTestBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageview_ic_expand.setOnClickListener {
            TransitionManager.go(Scene(cardview_userprofile), AutoTransition())
            if (!expanded) {
                textview_bio_userprofile.maxLines = Integer.MAX_VALUE
                imageview_ic_expand.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_expand_less
                    )
                )
                expanded = true
            } else {
                textview_bio_userprofile.maxLines = 3
                imageview_ic_expand.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_expand_more
                    )
                )
                expanded = false
            }
        }


    }

}
