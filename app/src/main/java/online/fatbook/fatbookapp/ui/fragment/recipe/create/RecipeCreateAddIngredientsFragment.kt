package online.fatbook.fatbookapp.ui.fragment.recipe.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_recipe_create_add_ingredients.*
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateAddIngredientsBinding
import online.fatbook.fatbookapp.ui.adapters.IngredientAdapter
import online.fatbook.fatbookapp.ui.listeners.OnIngredientItemClickListener
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class RecipeCreateAddIngredientsFragment : Fragment(), OnIngredientItemClickListener {

    private var binding: FragmentRecipeCreateAddIngredientsBinding? = null
    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }
    private var adapter: IngredientAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeCreateAddIngredientsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupIngredientsAdapter()
        loadIngredients()

        button_add_recipe_add_ingredients.setOnClickListener {
            val unit = null
            val quantity = 0.0

            val recipeIngredient = RecipeIngredient(
                pid = null,
                ingredient = recipeViewModel.newRecipeAddIngredient.value,
                unit = unit,
                quantity = quantity
            )
            recipeViewModel.newRecipe.value!!.ingredients!!.add(recipeIngredient)
            NavHostFragment.findNavController(this).popBackStack()
        }
    }

    private fun setupIngredientsAdapter() {
        val rv = rv_ingredients_recipe_add_ingredients
        adapter = IngredientAdapter()
        adapter!!.setClickListener(this)
        rv.adapter = adapter
    }

    private fun loadIngredients() {
        staticDataViewModel.getAllIngredients(object : ResultCallback<List<Ingredient>> {
            override fun onResult(value: List<Ingredient>?) {
                staticDataViewModel.ingredients.value = value
                adapter?.setData(value)
            }

            override fun onFailure(value: List<Ingredient>?) {
            }
        })
    }

    override fun onIngredientClick(previousItem: Int, selectedItem: Int, ingredient: Ingredient?) {
        adapter!!.selectedIngredient = staticDataViewModel.ingredients.value!![selectedItem]
        adapter!!.notifyItemChanged(previousItem)
        adapter!!.notifyItemChanged(selectedItem)
        recipeViewModel.newRecipeAddIngredient.value = ingredient

        textView_selected_ingredient_recipe_add_ingredients.text = ingredient!!.title
    }
}