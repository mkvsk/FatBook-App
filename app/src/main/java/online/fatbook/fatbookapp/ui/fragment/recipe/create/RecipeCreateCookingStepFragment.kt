package online.fatbook.fatbookapp.ui.fragment.recipe.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateCookingStepBinding

class RecipeCreateCookingStepFragment : Fragment() {

    private var binding: FragmentRecipeCreateCookingStepBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeCreateCookingStepBinding.inflate(inflater, container, false)
        return binding!!.root
    }

}