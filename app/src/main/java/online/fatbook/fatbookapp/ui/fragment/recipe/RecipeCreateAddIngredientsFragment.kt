package online.fatbook.fatbookapp.ui.fragment.recipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateAddIngredientsBinding

class RecipeCreateAddIngredientsFragment : Fragment() {

    private var binding: FragmentRecipeCreateAddIngredientsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeCreateAddIngredientsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

}