package online.fatbook.fatbookapp.ui.fragment.old

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import lombok.extern.java.Log
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.FragmentBookmarksOldBinding
import online.fatbook.fatbookapp.network.service.RetrofitFactory
import online.fatbook.fatbookapp.ui.adapters.RecipeAdapter
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Log
class BookmarksFragmentOLD : Fragment(), OnRecipeClickListener {
    private var binding: FragmentBookmarksOldBinding? = null
    private var userViewModel: UserViewModel? = null
    private var recipeViewModel: RecipeViewModel? = null
    private var recipeList: ArrayList<Recipe>? = null
    private var adapter: RecipeAdapter? = null
    private var user: User? = null
    private var userUpdated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarksOldBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.swipeRefreshBookmarks.setColorSchemeColors(resources.getColor(R.color.color_pink_a200))
        binding!!.swipeRefreshBookmarks.setOnRefreshListener {
            userUpdated = false
            loadRecipes()
        }
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        recipeViewModel = ViewModelProvider(requireActivity()).get(RecipeViewModel::class.java)
        user = userViewModel!!.user.value
        recipeList = ArrayList()
        userViewModel!!.user.observe(viewLifecycleOwner) { _user: User? ->
            user = _user
            if (!userUpdated) {
                loadRecipes()
            }
        }
//        setupAdapter()
    }

    private fun loadRecipes() {
        RetrofitFactory.apiService().getUserBookmarks(user!!.username)
            .enqueue(object : Callback<ArrayList<Recipe>?> {
                override fun onResponse(
                    call: Call<ArrayList<Recipe>?>,
                    response: Response<ArrayList<Recipe>?>
                ) {
                    binding!!.swipeRefreshBookmarks.isRefreshing = false
                    if (response.code() == 200) {
//                        log(Level.INFO, "bookmarks load SUCCESS")
                        if (response.body() != null) {
//                            BookmarksFragment.log.log(Level.INFO, response.body().toString())
                        }
                        recipeList = response.body()
                        adapter!!.setData(java.util.ArrayList(), user)
                        adapter!!.notifyDataSetChanged()
                    } else {
//                        BookmarksFragment.log.log(Level.INFO, "bookmarks load FAILED")
                    }
                }

                override fun onFailure(call: Call<ArrayList<Recipe>?>, t: Throwable) {
                    binding!!.swipeRefreshBookmarks.isRefreshing = false
//                    BookmarksFragment.log.log(Level.INFO, "bookmarks load FAILED")
                }
            })
    }

//    private fun setupAdapter() {
//        val recyclerView = binding!!.rvBookmarks
//        adapter = RecipeAdapter()
//        adapter!!.setData(recipeList, user)
//        recyclerView.adapter = adapter
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onRecipeClick(id: Long) {
//        val recipe = recipeList!![position]
//        recipeViewModel!!.selectedRecipe.value = recipe
//        recipeViewModel!!.selectedRecipePosition.value = position
//        NavHostFragment.findNavController(this)
//                .navigate(R.id.action_go_to_recipe_view_from_bookmarks_old)
    }

    override fun onBookmarksClick(recipe: RecipeSimpleObject?, bookmark: Boolean, position: Int) {
        recipeList!!.removeAt(position)
        adapter!!.notifyItemRemoved(position)
//        user!!.recipesFavourites!!.remove(recipe!!.identifier)
        saveUser()
    }

    private fun saveUser() {
//        RetrofitFactory.apiService().updateUser(user).enqueue(object : Callback<User?> {
//            override fun onResponse(call: Call<User?>, response: Response<User?>) {
//                if (response.code() == 200) {
////                    BookmarksFragment.log.log(Level.INFO, "user update SUCCESS")
//                    if (response.body() != null) {
////                        BookmarksFragment.log.log(Level.INFO, response.body().toString())
//                    }
//                    userViewModel!!.user.value = response.body()
//                    userUpdated = true
//                } else {
////                    BookmarksFragment.log.log(Level.INFO, "user update FAILED")
//                }
//            }
//
//            override fun onFailure(call: Call<User?>, t: Throwable) {
////                BookmarksFragment.log.log(Level.INFO, "user update FAILED")
//            }
//        })
    }

    override fun onForkClicked(recipe: RecipeSimpleObject?, fork: Boolean, position: Int) {
        recipeForked(recipe, fork, position)
    }

    override fun onForkClicked(
        recipe: RecipeSimpleObject?,
        fork: Boolean,
        position: Int,
        viewHolder: RecipeAdapter.ViewHolder
    ) {

    }

    override fun onUsernameClick(username: String) {
    }

    private fun recipeForked(recipe: RecipeSimpleObject?, fork: Boolean, position: Int) {
        RetrofitFactory.apiService().recipeForked(user!!.pid, recipe!!.pid, fork)
            .enqueue(object : Callback<Recipe?> {
                override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
                    if (response.code() == 200) {
//                        BookmarksFragment.log.log(Level.INFO, "fork SUCCESS")
                        recipeViewModel?.setSelectedRecipe(response.body())
                        loadUser()
                    } else {
//                        BookmarksFragment.log.log(Level.INFO, "fork FAILED")
                    }
                }

                override fun onFailure(call: Call<Recipe?>, t: Throwable) {
//                    BookmarksFragment.log.log(Level.INFO, "fork FAILED")
                }
            })
    }

    private fun loadUser() {
        RetrofitFactory.apiService().getUserByUsername(user!!.username)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.code() == 200) {
//                        BookmarksFragment.log.log(Level.INFO, "load user SUCCESS")
//                        BookmarksFragment.log.log(
//                            Level.INFO,
//                            "" + response.code() + " found user: " + response.body()
//                        )
                        response.body()?.let { userViewModel!!.setUser(it) }
                    } else {
//                        BookmarksFragment.log.log(Level.INFO, "load user ERROR")
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
//                    BookmarksFragment.log.log(Level.INFO, "load user ERROR")
                }
            })
    }
}