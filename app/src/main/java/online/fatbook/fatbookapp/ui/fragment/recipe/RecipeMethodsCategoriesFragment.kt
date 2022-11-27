package online.fatbook.fatbookapp.ui.fragment.recipe

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.core.recipe.StaticDataObject
import online.fatbook.fatbookapp.databinding.FragmentRecipeMethodsCategoriesBinding
import online.fatbook.fatbookapp.ui.adapters.StaticDataAdapter
import online.fatbook.fatbookapp.ui.listeners.OnStaticDataClickListener
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class RecipeMethodsCategoriesFragment : Fragment(), OnStaticDataClickListener {

    private var _binding: FragmentRecipeMethodsCategoriesBinding? = null
    private val binding get() = _binding!!

    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }
    private var adapter: StaticDataAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeMethodsCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (recipeViewModel.newRecipeCookingCategories.value == null) {
            recipeViewModel.newRecipeCookingCategories.value = ArrayList()
        }
        setupItemsAdapter()
        if (staticDataViewModel.loadCookingMethod.value!!) {
            binding.toolbarRecipeMethodsCategoriesItems.setTitle(R.string.toolbar_title_cooking_method)
            loadCookingMethods()
            setupMenu(false)
        } else {
            binding.toolbarRecipeMethodsCategoriesItems.setTitle(R.string.toolbar_title_cooking_categories)
            loadCookingCategories()
            setupMenu(true)
        }
    }

    private fun setupMenu(category: Boolean) {
        if (category) {
            binding.toolbarRecipeMethodsCategoriesItems.inflateMenu(R.menu.add_categories_menu)
            binding.toolbarRecipeMethodsCategoriesItems.setOnMenuItemClickListener(this::onOptionsItemSelected)
        }
        binding.toolbarRecipeMethodsCategoriesItems.setNavigationOnClickListener {
            popBackStack()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add_categories -> {
                recipeViewModel.newRecipe.value!!.cookingCategories =
                    recipeViewModel.newRecipeCookingCategories.value as ArrayList<CookingCategory>
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupItemsAdapter() {
        val rv = binding.rvRecipeMethodsCategoriesItems
        adapter = StaticDataAdapter()
        adapter!!.setClickListener(this)
        rv.adapter = adapter
    }

    private fun loadCookingCategories() {
        staticDataViewModel.getAllCookingCategories(object : ResultCallback<List<CookingCategory>> {
            override fun onResult(value: List<CookingCategory>?) {
                value?.let {
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
            }

            override fun onFailure(value: List<CookingCategory>?) {
            }
        })
    }

    private fun loadCookingMethods() {
        staticDataViewModel.getAllCookingMethods(object : ResultCallback<List<CookingMethod>> {
            override fun onResult(value: List<CookingMethod>?) {
                value?.let {
                    staticDataViewModel.cookingMethods.value = value
                    adapter?.setData(value)
                    val list: ArrayList<Int> = ArrayList()
                    if (recipeViewModel.newRecipe.value!!.cookingMethod != null) {
                        list.add(value.indexOf(recipeViewModel.newRecipe.value!!.cookingMethod!!))
                    }
                    adapter?.setSelected(list)
                }
            }

            override fun onFailure(value: List<CookingMethod>?) {
            }
        })
    }

    override fun onItemClick(item: StaticDataObject) {
        recipeViewModel.newRecipeCookingMethod.value = item as CookingMethod
        recipeViewModel.newRecipe.value!!.cookingMethod =
            recipeViewModel.newRecipeCookingMethod.value
        Log.i("============================================================", "")
        Log.i("SELECTED METHOD", "${recipeViewModel.newRecipe.value!!.cookingMethod}")
        Log.i("============================================================", "")
        findNavController().popBackStack()
    }

    override fun onItemClickChoose(item: StaticDataObject) {
        if (recipeViewModel.newRecipeCookingCategories.value!!.contains(item as CookingCategory)) {
            recipeViewModel.newRecipeCookingCategories.value!!.remove(item)
        } else {
            recipeViewModel.newRecipeCookingCategories.value!!.add(item)
        }
        recipeViewModel.newRecipe.value!!.cookingCategories =
            recipeViewModel.newRecipeCookingCategories.value

        Log.i("============================================================", "")
        Log.i("SELECTED CATEGORIES", "${recipeViewModel.newRecipe.value!!.cookingCategories}")
        Log.i("============================================================", "")
    }

    private fun popBackStack() {
        findNavController().popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}