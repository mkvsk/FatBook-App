package online.fatbook.fatbookapp.ui.fragment.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_favourites_recipes_page.*
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.FragmentFavouritesRecipesPageBinding
import online.fatbook.fatbookapp.ui.adapters.RecipeAdapter
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener

class FavouritesRecipesPageFragment : Fragment(), OnRecipeClickListener {

    private var _binding: FragmentFavouritesRecipesPageBinding? = null
    private val binding get() = _binding!!

    private var adapter: RecipeAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesRecipesPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onRecipeClick(position: Int) {
        Log.d("recipe click", position.toString())
    }

    override fun onBookmarksClick(recipe: RecipeSimpleObject?, bookmark: Boolean, position: Int) {
        Log.d("bookmark click", position.toString())
    }

    override fun onForkClicked(recipe: RecipeSimpleObject?, fork: Boolean, position: Int) {
        Log.d("fork click", position.toString())
    }

    override fun onPause() {
        super.onPause()
        Log.d("STATE", "PAUSE TAB 2")
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
        Log.d("STATE", "RESUME TAB 2")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}