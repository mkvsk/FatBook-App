package online.fatbook.fatbookapp.ui.fragment.recipe.create

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_feed.*
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

        if (recipeViewModel.newRecipeCookingCategories.value == null) {
            recipeViewModel.newRecipeCookingCategories.value = ArrayList()
        }

        setupItemsAdapter()
        if (staticDataViewModel.loadCookingMethod.value!!) {
            toolbar_recipe_methods_categories_items.setTitle(R.string.toolbar_title_cooking_method)
            loadCookingMethods()
        } else {
            setupMenu()
            toolbar_recipe_methods_categories_items.setTitle(R.string.toolbar_title_cooking_categories)
            loadCookingCategories()
        }
    }

    private fun setupMenu() {
        val activity = (activity as AppCompatActivity?)!!
        activity.setSupportActionBar(toolbar_recipe_methods_categories_items)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_categories_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add_categories -> {
                recipeViewModel.newRecipe.value!!.cookingCategories =
                    recipeViewModel.newRecipeCookingCategories.value as ArrayList<CookingCategory>
                NavHostFragment.findNavController(this).popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupItemsAdapter() {
        val rv = rv_recipe_methods_categories_items
        adapter = StaticDataAdapter()
        adapter!!.setClickListener(this)
        rv.adapter = adapter
    }

    //TODO sort by title
    private fun loadCookingCategories() {
        staticDataViewModel.getAllCookingCategories(object : ResultCallback<List<CookingCategory>> {
            override fun onResult(value: List<CookingCategory>?) {
                Log.d("VALUE OLD", "$value")

                value as ArrayList
                val filter = value.single { it.title == "Other" || it.title == "Другое" }
                value.remove(filter)
                value.add(0, filter)

                Log.d("VALUE NEW", "$value")

                staticDataViewModel.cookingCategories.value = value

                adapter?.setData(value)

                val list: ArrayList<Int> = ArrayList()
                if (!recipeViewModel.newRecipe.value!!.cookingCategories.isNullOrEmpty()) {
                    for (i in recipeViewModel.newRecipe.value!!.cookingCategories!!) {
                        if (value.contains(i)) {
                            list.add(value.indexOf(i))
                        }
                    }
                }
                adapter?.setSelected(list)
            }

            override fun onFailure(value: List<CookingCategory>?) {
            }
        })
    }

    //TODO sort by title
    private fun loadCookingMethods() {
        staticDataViewModel.getAllCookingMethods(object : ResultCallback<List<CookingMethod>> {
            override fun onResult(value: List<CookingMethod>?) {
                Log.d("VALUE OLD", "$value")

                value as ArrayList
                val filter = value.single { it.title == "Other" || it.title == "Другое" }
                value.remove(filter)
                value.add(0, filter)

                Log.d("VALUE NEW", "$value")

                staticDataViewModel.cookingMethods.value = value
                adapter?.setData(value)

                val list: ArrayList<Int> = ArrayList()
                if (recipeViewModel.newRecipe.value!!.cookingMethod != null) {
                    list.add(value.indexOf(recipeViewModel.newRecipe.value!!.cookingMethod!!))
                }
                adapter?.setSelected(list)
            }

            override fun onFailure(value: List<CookingMethod>?) {
            }
        })
    }

    override fun onItemClick(item: StaticDataObject) {
        Log.i("SELECTED METHOD", "${item.title}")
        recipeViewModel.newRecipeCookingMethod.value = item as CookingMethod
        recipeViewModel.newRecipe.value!!.cookingMethod =
            recipeViewModel.newRecipeCookingMethod.value
        NavHostFragment.findNavController(this).popBackStack()
    }

    override fun onItemClickChoose(item: StaticDataObject) {
        if (recipeViewModel.newRecipeCookingCategories.value!!.contains(item as CookingCategory)) {
            recipeViewModel.newRecipeCookingCategories.value!!.remove(item)
        } else {
            recipeViewModel.newRecipeCookingCategories.value!!.add(item)
        }

        Log.i("============================================================", "")
        Log.i("SELECTED CATEGORIES", "${recipeViewModel.newRecipeCookingCategories.value!!}")
        Log.i("============================================================", "")
    }
}