package online.fatbook.fatbookapp.ui.fragment.recipe.view.edit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_recipe_view.*
import kotlinx.android.synthetic.main.fragment_recipe_view_old.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.Recipe
import online.fatbook.fatbookapp.databinding.FragmentRecipeViewBinding
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.RecipeUtils
import online.fatbook.fatbookapp.util.obtainViewModel

class RecipeViewFragment : Fragment() {

    private var binding: FragmentRecipeViewBinding? = null
    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }

    private var recipeForked = false
    private var recipeInFav = false

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

        textview_author_username_recipe_view.setOnClickListener {
            //val v = textview_author_username_recipe_view.text.toString()
            userViewModel.selectedUsername.value = "hewix"
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_userprofile_from_recipe_view)
        }

        imageView_fork_view_recipe.setOnClickListener {
            toggleForks(recipeForked)
        }

        imageView_recipe_view_favourites.setOnClickListener {
            toggleFavourites(recipeInFav)
        }

        edittext_input_comment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!str.toString().isNullOrEmpty()) {
                    button_send_comment.visibility = View.VISIBLE
                } else {
                    button_send_comment.visibility = View.GONE
                }
            }

            override fun afterTextChanged(str: Editable?) {
            }

        })
    }

    private fun toggleFavourites(inFavourite: Boolean) {
        if (inFavourite) {
            Toast.makeText(context,"Removed from favourites",Toast.LENGTH_SHORT).show()
            recipeInFav = false
        } else {
            Toast.makeText(context,"Added to favourites",Toast.LENGTH_SHORT).show()
            recipeInFav = true
        }
    }

    private fun toggleForks(forked: Boolean) {
        if (forked) {
            Toast.makeText(context,"Recipe not forked :(",Toast.LENGTH_SHORT).show()
            recipeForked = false
        } else {
            Toast.makeText(context,"Recipe forked!",Toast.LENGTH_SHORT).show()
            recipeForked = true
        }
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