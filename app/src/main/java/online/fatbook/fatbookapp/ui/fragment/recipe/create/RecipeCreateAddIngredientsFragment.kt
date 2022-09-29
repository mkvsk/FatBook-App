package online.fatbook.fatbookapp.ui.fragment.recipe.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_recipe_create_add_ingredients.*
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredients
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateAddIngredientsBinding
import online.fatbook.fatbookapp.ui.adapters.IngredientsAdapter

class RecipeCreateAddIngredientsFragment : Fragment() {

    private var binding: FragmentRecipeCreateAddIngredientsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeCreateAddIngredientsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = IngredientsAdapter()
        val ingredientList = ArrayList<Ingredients>()
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))
        ingredientList.add(Ingredients(0L ,"Kartoshka"))

        adapter.setData(ingredientList)
        rv_ingredients_recipe_add_ingredients.adapter = adapter
    }
}