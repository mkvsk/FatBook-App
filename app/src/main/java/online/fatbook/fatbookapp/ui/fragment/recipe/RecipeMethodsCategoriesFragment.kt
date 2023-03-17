package online.fatbook.fatbookapp.ui.fragment.recipe

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.core.recipe.StaticDataObject
import online.fatbook.fatbookapp.databinding.FragmentRecipeMethodsCategoriesBinding
import online.fatbook.fatbookapp.ui.adapters.StaticDataAdapter
import online.fatbook.fatbookapp.ui.listeners.OnStaticDataClickListener
import online.fatbook.fatbookapp.ui.viewmodel.RecipeEditViewModel
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class RecipeMethodsCategoriesFragment : Fragment(), OnStaticDataClickListener {

    private var _binding: FragmentRecipeMethodsCategoriesBinding? = null
    private val binding get() = _binding!!

    private val recipeEditViewModel by lazy { obtainViewModel(RecipeEditViewModel::class.java) }
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

        if (recipeEditViewModel.recipeCookingCategories.value == null) {
            recipeEditViewModel.setRecipeCookingCategories(ArrayList())
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
                recipeEditViewModel.recipe.value!!.cookingCategories =
                    recipeEditViewModel.recipeCookingCategories.value as ArrayList<CookingCategory>
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
                    staticDataViewModel.setCookingCategories(value)
                    adapter?.setData(value)
                    val list: ArrayList<Int> = ArrayList()
                    if (!recipeEditViewModel.recipe.value!!.cookingCategories.isNullOrEmpty()) {
                        for (i in recipeEditViewModel.recipe.value!!.cookingCategories!!) {
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
                    staticDataViewModel.setCookingMethods(value)
                    adapter?.setData(value)
                    val list: ArrayList<Int> = ArrayList()
                    if (recipeEditViewModel.recipe.value!!.cookingMethod != null) {
                        list.add(value.indexOf(recipeEditViewModel.recipe.value!!.cookingMethod!!))
                    }
                    adapter?.setSelected(list)
                }
            }

            override fun onFailure(value: List<CookingMethod>?) {
            }
        })
    }

    override fun onItemClick(item: StaticDataObject) {
        recipeEditViewModel.setRecipeCookingMethod(item as CookingMethod)
        recipeEditViewModel.recipe.value!!.cookingMethod =
            recipeEditViewModel.recipeCookingMethod.value
        Log.i("============================================================", "")
        Log.i("SELECTED METHOD", "${recipeEditViewModel.recipe.value!!.cookingMethod}")
        Log.i("============================================================", "")
        findNavController().popBackStack()
    }

    override fun onItemClickChoose(item: StaticDataObject) {
        if (recipeEditViewModel.recipeCookingCategories.value!!.contains(item as CookingCategory)) {
            recipeEditViewModel.recipeCookingCategories.value!!.remove(item)
        } else {
            recipeEditViewModel.recipeCookingCategories.value!!.add(item)
        }
        recipeEditViewModel.recipe.value!!.cookingCategories =
            recipeEditViewModel.recipeCookingCategories.value

        Log.i("============================================================", "")
        Log.i("SELECTED CATEGORIES", "${recipeEditViewModel.recipe.value!!.cookingCategories}")
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