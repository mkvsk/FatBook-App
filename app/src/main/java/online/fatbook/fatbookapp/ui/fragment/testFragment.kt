package online.fatbook.fatbookapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.fragment_test.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentTestBinding

class testFragment : Fragment() {

    private var binding: FragmentTestBinding? = null

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
            if(expandableLayout.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(cardview_userprofile, AutoTransition())
                expandableLayout.visibility = View.VISIBLE
                imageview_ic_expand.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_expand_less))
            } else {
                TransitionManager.beginDelayedTransition(cardview_userprofile, AutoTransition())
                expandableLayout.visibility = View.GONE
                imageview_ic_expand.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_expand_more))
            }
        }

    }

}

