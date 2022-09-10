package online.fatbook.fatbookapp.ui.fragment.recipe.view.edit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_recipe_view.*
import online.fatbook.fatbookapp.core.Recipe
import online.fatbook.fatbookapp.core.RecipeIngredient
import online.fatbook.fatbookapp.databinding.FragmentRecipeViewBinding
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class RecipeViewFragment : Fragment() {

    private var binding: FragmentRecipeViewBinding? = null
    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeViewBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData(123123L)
        drawData()
    }

    private fun loadData(id: Long) {
        val recipe: Recipe = Recipe(
            pid = 1235L,
            name = "Kartoshechka",
            description = "lalalalallalal",
            author = "Neshik",
            ingredients = ArrayList(),
            image = "https://fatbook.b-cdn.net/root/upal.jpg",
            forks = 123,
            createDate = "10.09.2022", identifier = id
        )
        recipe.recipePortionFats = 1.9

        recipeViewModel.selectedRecipe.value = recipe

    }

    private fun drawData() {
        val recipe = recipeViewModel.selectedRecipe.value
        Glide
            .with(requireContext())
            .load(recipe!!.image)
            .into(imageview_main_photo_recipe_view)


    }
}