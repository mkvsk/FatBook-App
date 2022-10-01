package online.fatbook.fatbookapp.ui.fragment.recipe.create

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_recipe_methods_categories_items.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.core.recipe.StaticDataObject
import online.fatbook.fatbookapp.databinding.FragmentRecipeMethodsCategoriesItemsBinding
import online.fatbook.fatbookapp.ui.adapters.StaticDataAdapter
import online.fatbook.fatbookapp.ui.listeners.OnStaticDataClickListener
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class RecipeMethodsCategoriesItemsFragment : Fragment(), OnStaticDataClickListener {
    private var binding: FragmentRecipeMethodsCategoriesItemsBinding? = null
    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }
    private var adapter: StaticDataAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeMethodsCategoriesItemsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupItemsAdapter()
        if (staticDataViewModel.loadCookingMethod.value!!) {
            loadCookingMethods()
        } else {
            // show menu
            // on item click listener:
            //
            loadCookingCategories()
        }
    }

    private fun setupItemsAdapter() {
        val rv = rv_recipe_methods_categories_items
        adapter = StaticDataAdapter()
        adapter!!.setClickListener(this)
        rv.adapter = adapter
    }

    private fun loadCookingCategories() {
        staticDataViewModel.getAllCookingCategories(object : ResultCallback<List<CookingCategory>> {
            override fun onResult(value: List<CookingCategory>?) {
                staticDataViewModel.cookingCategories.value = value
                adapter?.setData(value)
            }

            override fun onFailure(value: List<CookingCategory>?) {
            }
        })
    }

    private fun loadCookingMethods() {
        staticDataViewModel.getAllCookingMethods(object : ResultCallback<List<CookingMethod>> {
            override fun onResult(value: List<CookingMethod>?) {
                staticDataViewModel.cookingMethods.value = value
                adapter?.setData(value)
            }

            override fun onFailure(value: List<CookingMethod>?) {
            }
        })
    }

    override fun onItemClick(item: StaticDataObject) {
        Log.i("SELECTED METHOD", "${item.title}")
        recipeViewModel.newRecipeCookingMethod.value = item as CookingMethod
        recipeViewModel.newRecipe.value!!.cookingMethod = recipeViewModel.newRecipeCookingMethod.value
        NavHostFragment.findNavController(this).popBackStack()
    }

    override fun onItemClickChoose(item: StaticDataObject) {
        if (recipeViewModel.newRecipeCookingCategories.value!!.contains(item as CookingCategory)) {
            recipeViewModel.newRecipeCookingCategories.value!!.remove(item)
        } else {
            recipeViewModel.newRecipeCookingCategories.value!!.add(item)
        }

        Log.i("============================================================", "")
        Log.i("SELECTED CATEGORIES", "${recipeViewModel.newRecipe.value!!.cookingCategories}")
        Log.i("============================================================", "")
    }
}