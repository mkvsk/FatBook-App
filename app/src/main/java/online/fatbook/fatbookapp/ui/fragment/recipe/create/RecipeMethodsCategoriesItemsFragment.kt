package online.fatbook.fatbookapp.ui.fragment.recipe.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.databinding.FragmentRecipeMethodsCategoriesItemsBinding
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class RecipeMethodsCategoriesItemsFragment : Fragment() {
    private var binding: FragmentRecipeMethodsCategoriesItemsBinding? = null

    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeMethodsCategoriesItemsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (staticDataViewModel.loadCookingMethod.value!!) {
            loadCookingMethods()
        } else {
            loadCookingCategories()
        }
    }

    private fun loadCookingCategories() {
        staticDataViewModel.getAllCookingCategories(object : ResultCallback<List<CookingCategory>> {
            override fun onResult(value: List<CookingCategory>?) {
                staticDataViewModel.cookingCategories.value = value
            }

            override fun onFailure(value: List<CookingCategory>?) {
            }
        })
    }

    private fun loadCookingMethods() {
        staticDataViewModel.getAllCookingMethods(object : ResultCallback<List<CookingMethod>> {
            override fun onResult(value: List<CookingMethod>?) {
                staticDataViewModel.cookingMethods.value = value
            }

            override fun onFailure(value: List<CookingMethod>?) {
            }
        })
    }

}