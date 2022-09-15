package online.fatbook.fatbookapp.ui.fragment.recipe.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.fragment_recipe_create_first_stage.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateFirstStageBinding
import online.fatbook.fatbookapp.util.TimePickerFragment

class RecipeCreateFirstStageFragment : Fragment() {
    private var binding: FragmentRecipeCreateFirstStageBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeCreateFirstStageBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button_delete_photo_recipe_create_1_stage.setOnClickListener {
            TimePickerFragment().show(parentFragmentManager, "time picker")
        }

    }

    private fun setTime() {

    }

}