package online.fatbook.fatbookapp.ui.fragment.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_all_recipes_page.*
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.FragmentAllRecipesPageBinding
import online.fatbook.fatbookapp.ui.adapters.RecipeAdapter
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener

class AllRecipesPageFragment : Fragment(), OnRecipeClickListener {

    private var binding: FragmentAllRecipesPageBinding? = null
    private var adapter: RecipeAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllRecipesPageBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list1 = listOf(
            Recipe(title = "Мой рецепт", forks = 255),
            Recipe(title = "Мой рецепт2", forks = 1477, author = "Neshik"),
            Recipe(
                title = "Text text text text text text text text",
                forks = 1234567,
                author = "Timofey"
            ),
//            Recipe(title = "Мой рецепт4"),
//            Recipe(title = "sobaka5"),
//            Recipe(title = "sobaka6"),
//            Recipe(title = "sobaka7"),
//            Recipe(title = "sobaka8", forks = 1339),
//            Recipe(title = "sobaka9"),
//            Recipe(title = "sobaka10")
        )

        adapter = RecipeAdapter()
        adapter!!.setData(list1, User())
        adapter!!.setClickListener(this)
        rv_all_recipes_page.adapter = adapter
    }

    override fun onRecipeClick(position: Int) {
        Log.d("recipe click", position.toString())
    }

    override fun onBookmarksClick(recipe: Recipe?, bookmark: Boolean, position: Int) {
        Log.d("bookmark click", position.toString())
    }

    override fun onForkClicked(recipe: Recipe?, fork: Boolean, position: Int) {
        Log.d("fork click", position.toString())
    }

    override fun onPause() {
        super.onPause()

        Log.d("STATE", "PAUSE TAB 1")

    }

    override fun onResume() {
        super.onResume()
        binding!!.root.requestLayout()
        Log.d("STATE", "RESUME TAB 1")

    }
}