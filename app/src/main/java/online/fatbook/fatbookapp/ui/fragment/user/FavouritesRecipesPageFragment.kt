package online.fatbook.fatbookapp.ui.fragment.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject
import online.fatbook.fatbookapp.databinding.FragmentFavouritesRecipesPageBinding
import online.fatbook.fatbookapp.ui.adapters.RecipeAdapter
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class FavouritesRecipesPageFragment : Fragment(), OnRecipeClickListener {

    private var _binding: FragmentFavouritesRecipesPageBinding? = null
    private val binding get() = _binding!!

    private var adapter: RecipeAdapter? = null

    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }

    companion object {
        private const val TAG = "FavouritesRecipesPageFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesRecipesPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = RecipeAdapter()
        adapter!!.setClickListener(this)
        adapter!!.setContext(requireContext())
        binding.rvFavouritesRecipesPage.adapter = adapter
        setData()
    }

    //TODO если рецептов нет - вывести какое-нибудь TextView "Вы еще не добавили ни один рецепт в избранное"
    fun setData() {
        adapter!!.setData(userViewModel.user.value!!.recipesFavourites, userViewModel.user.value)
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