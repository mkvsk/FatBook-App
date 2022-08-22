package online.fatbook.fatbookapp.ui.fragment.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import online.fatbook.fatbookapp.databinding.FragmentRecipeMethodsCategoriesItemsBinding

class RecipeMethodsCategoriesItemsFragment : Fragment() {
    private var binding: FragmentRecipeMethodsCategoriesItemsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeMethodsCategoriesItemsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

}