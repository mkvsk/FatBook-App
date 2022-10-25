package online.fatbook.fatbookapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_search.*
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.*
import online.fatbook.fatbookapp.databinding.FragmentSearchBinding
import online.fatbook.fatbookapp.ui.adapters.SearchAdapter
import online.fatbook.fatbookapp.ui.listeners.OnSearchItemClickListener
import online.fatbook.fatbookapp.ui.viewmodel.SearchViewModel
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class SearchFragment : Fragment(), SeekBar.OnSeekBarChangeListener, OnSearchItemClickListener {

    private var binding: FragmentSearchBinding? = null
    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }
    private val searchViewModel by lazy { obtainViewModel(SearchViewModel::class.java) }
    private var adapterCategories: SearchAdapter? = null
    private var adapterMethods: SearchAdapter? = null
    private var adapterDifficulty: SearchAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadCategories()
        setupCategoriesAdapter()
        loadMethods()
        setupMethodsAdapter()

        loadDifficulty()
        setupDifficultyAdapter()

        BottomSheetBehavior.from(sheet_search).apply {
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        val seekBar: SeekBar = seekbar_kcals_limit
        seekBar.setOnSeekBarChangeListener(this)
    }

    private fun loadMethods() {
        staticDataViewModel.getAllCookingMethods(object : ResultCallback<List<CookingMethod>> {
            override fun onResult(value: List<CookingMethod>?) {

                value as ArrayList
                val filter = value.single { it.title == "Other" || it.title == "Другое" }
                value.remove(filter)
                value.add(0, filter)

                staticDataViewModel.cookingMethods.value = value
                adapterMethods?.setData(value)

                val list: ArrayList<Int> = ArrayList()
                if (!searchViewModel.methods.value.isNullOrEmpty()) {
                    for (i in searchViewModel.methods.value!!) {
                        if (value.contains(i)) {
                            list.add(value.indexOf(i))
                        }
                    }
                }
                adapterMethods?.setSelected(list)
            }

            override fun onFailure(value: List<CookingMethod>?) {
            }
        })
    }

    private fun setupDifficultyAdapter() {
        val rv = rv_difficulty_search
        adapterDifficulty = SearchAdapter()
        adapterDifficulty!!.setClickListener(this)
        rv.adapter = adapterDifficulty
    }

    private fun loadDifficulty() {
        //TODO
    }

    private fun setupMethodsAdapter() {
        val rv = rv_cooking_methods_search
        adapterMethods = SearchAdapter()
        adapterMethods!!.setClickListener(this)
        rv.adapter = adapterMethods
    }

    private fun loadCategories() {
        staticDataViewModel.getAllCookingCategories(object : ResultCallback<List<CookingCategory>> {
            override fun onResult(value: List<CookingCategory>?) {
                value as ArrayList
                val filter = value.single { it.title == "Other" || it.title == "Другое" }
                value.remove(filter)
                value.add(0, filter)

                staticDataViewModel.cookingCategories.value = value

                adapterCategories?.setData(value)

                val list: ArrayList<Int> = ArrayList()
                if (!searchViewModel.categories.value.isNullOrEmpty()) {
                    for (i in searchViewModel.categories.value!!) {
                        if (value.contains(i)) {
                            list.add(value.indexOf(i))
                        }
                    }
                }
                adapterCategories?.setSelected(list)
            }

            override fun onFailure(value: List<CookingCategory>?) {
            }
        })
    }

    private fun setupCategoriesAdapter() {
        val rv = rv_cooking_categories_search
        adapterCategories = SearchAdapter()
        adapterCategories!!.setClickListener(this)
        rv.adapter = adapterCategories
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        textview_kcals_limit_setted_search.text = progress.toString()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    override fun onItemClick(item: StaticDataObject) {
    }

    override fun onItemClickChoose(item: StaticDataObject) {
//        if (recipeViewModel.newRecipeCookingCategories.value!!.contains(item as CookingCategory)) {
//            recipeViewModel.newRecipeCookingCategories.value!!.remove(item)
//        } else {
//            recipeViewModel.newRecipeCookingCategories.value!!.add(item)
//        }

        if (item is CookingCategory) {
//            searchViewModel.categories.value!!.contains(item as CookingCategory)
        }
        if (item is CookingMethod) {

        }
        if (item is CookingDifficulty) {

        }
    }
}