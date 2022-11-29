package online.fatbook.fatbookapp.ui.fragment.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.FragmentAllRecipesPageBinding
import online.fatbook.fatbookapp.ui.adapters.RecipeAdapter
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class UserRecipesPageFragment(val user: User) : Fragment(), OnRecipeClickListener {

    private var _binding: FragmentAllRecipesPageBinding? = null
    private val binding get() = _binding!!

    private var adapter: RecipeAdapter? = null

    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }

    companion object {
        private const val TAG = "UserRecipesPageFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllRecipesPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = RecipeAdapter()
        adapter!!.setClickListener(this)
        adapter!!.setContext(requireContext())
        binding.rvUserRecipesPage.adapter = adapter
        setData()
    }

    //TODO если рецептов нет - вывести какое-нибудь TextView "Вы еще не создали ни одного рецепта"
    fun setData() {
        adapter?.setData(user.recipes, userViewModel.user.value)
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