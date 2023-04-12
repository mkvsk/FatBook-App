package online.fatbook.fatbookapp.ui.user.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.rv_feed_recipe_card_preview.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.FragmentAllRecipesPageBinding
import online.fatbook.fatbookapp.network.callback.ResultCallback
import online.fatbook.fatbookapp.ui.feed.adapters.RecipeAdapter
import online.fatbook.fatbookapp.ui.base.OnRecipeClickListener
import online.fatbook.fatbookapp.ui.recipe.viewmodel.RecipeViewViewModel
import online.fatbook.fatbookapp.ui.user.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class UserRecipesPageFragment(val user: User) : Fragment(), OnRecipeClickListener {

    private var _binding: FragmentAllRecipesPageBinding? = null
    private val binding get() = _binding!!

    private var adapter: RecipeAdapter? = null

    private val recipeViewViewModel by lazy { obtainViewModel(RecipeViewViewModel::class.java) }
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
        setupAdapter()
    }

    private fun setupAdapter() {
        adapter = RecipeAdapter()
        adapter!!.setClickListener(this)
        adapter!!.setContext(requireContext())
        binding.rvUserRecipesPage.adapter = adapter
        setData(user)
    }

    //TODO если рецептов нет - вывести какое-нибудь TextView "Вы еще не создали ни одного рецепта"
    fun setData(user: User) {
        adapter?.setData(user.recipes, userViewModel.user.value)
    }

    override fun onRecipeClick(id: Long) {
        Log.d("recipe click", id.toString())
        recipeViewViewModel.setSelectedRecipeId(id)
        NavHostFragment.findNavController(parentFragment!!.requireParentFragment())
            .navigate(R.id.action_go_to_recipe_view_from_user_profile)
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
        viewHolder: RecipeAdapter.ViewHolder
    ) {
        recipeViewViewModel.recipeFork(recipe!!.pid!!, fork, object : ResultCallback<Int> {
            override fun onResult(value: Int?) {
                if (fork) {
                    userViewModel.user.value!!.recipesForked!!.add(recipe)
                } else {
                    userViewModel.user.value!!.recipesForked!!.removeIf { recipe.pid == it.pid }
                }
                viewHolder.itemView.textView_rv_card_recipe_forks_avg.text = value.toString()
//                viewHolder.itemView.view_click_fork.tag = RecipeUtils.TAG_CLICK_FALSE
            }

            override fun onFailure(value: Int?) {
                Toast.makeText(
                    requireContext(), getString(R.string.error_common_network), Toast.LENGTH_SHORT
                ).show()
            }
        })
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