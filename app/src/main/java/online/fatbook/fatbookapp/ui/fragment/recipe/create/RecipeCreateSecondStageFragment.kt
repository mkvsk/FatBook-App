package online.fatbook.fatbookapp.ui.fragment.recipe.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_recipe_create_second_stage.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateSecondStageBinding
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class RecipeCreateSecondStageFragment : Fragment() {

    private var binding: FragmentRecipeCreateSecondStageBinding? = null

    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeCreateSecondStageBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_add_ingredient_recipe_create_2_stage.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_add_ingredient_from_second_stage)
        }
    }
}