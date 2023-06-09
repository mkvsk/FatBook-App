package online.fatbook.fatbookapp.ui.user.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.FragmentFavouritesRecipesPageBinding
import online.fatbook.fatbookapp.ui.feed.adapters.RecipeAdapter
import online.fatbook.fatbookapp.ui.base.OnRecipeClickListener
import online.fatbook.fatbookapp.ui.user.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class FavouritesRecipesPageFragment(val user: User) : Fragment(), OnRecipeClickListener {

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
        adapter = RecipeAdapter(requireContext())
        adapter!!.setClickListener(this)
        binding.rvFavouritesRecipesPage.adapter = adapter
        setData(user)
    }

    //TODO если рецептов нет - вывести какое-нибудь TextView "Вы еще не добавили ни один рецепт в избранное"
    fun setData(user: User) {
        adapter?.setData(user.recipesFavourites, userViewModel.user.value)
    }

    override fun onRecipeClick(id: Long) {
        Log.d("recipe click", id.toString())
    }

    override fun onBookmarksClick(recipe: RecipeSimpleObject?, bookmark: Boolean, position: Int) {
        Log.d("bookmark click", position.toString())
    }

    override fun onForkClicked(recipe: RecipeSimpleObject?, fork: Boolean, position: Int) {
        Log.d("fork click", position.toString())
    }

    override fun onForkClicked(
        recipe: RecipeSimpleObject?,
        fork: Boolean,
        position: Int,
        recipePreviewItemViewHolder: RecipeAdapter.RecipePreviewItemViewHolder
    ) {
    }

    override fun onUsernameClick(username: String) {
        Log.d("username click", username)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}