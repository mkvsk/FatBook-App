package online.fatbook.fatbookapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_search.*
import kotlinx.android.synthetic.main.include_progress_overlay.*
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.core.recipe.StaticDataObject
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

    private lateinit var bottomSheetSearchFilter: BottomSheetBehavior<*>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (searchViewModel.categories.value.isNullOrEmpty()) {
            searchViewModel.categories.value = ArrayList()
        }
        if (searchViewModel.methods.value.isNullOrEmpty()) {
            searchViewModel.methods.value = ArrayList()
        }
        if (searchViewModel.difficulty.value.isNullOrEmpty()) {
            searchViewModel.difficulty.value = ArrayList()
        }

        loadCategories()
        loadMethods()
        loadDifficulty()

        setupCategoriesAdapter()
        setupMethodsAdapter()
        setupDifficultyAdapter()

        bottomSheetSearchFilter = BottomSheetBehavior.from(sheet_search)
        bottomSheetSearchFilter.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetSearchFilter.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    scroll_view_bottom_sheet_search.scrollTo(0, 0)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        seekbar_kcals_limit.setOnSeekBarChangeListener(this)
        handleBackPressed()
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
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.justifyContent = JustifyContent.FLEX_START
        rv.layoutManager = layoutManager
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
                //TODO replace value + value to value
                adapterCategories?.setData(value + value)

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
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.justifyContent = JustifyContent.FLEX_START
        rv.layoutManager = layoutManager
        adapterCategories = SearchAdapter()
        adapterCategories!!.setClickListener(this)
        rv.adapter = adapterCategories
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        textview_kcals_limit_setted_search.text = progress.toString()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    override fun onItemClick(item: StaticDataObject) {}

    override fun onItemClickChoose(item: StaticDataObject) {

        when (item) {
            is CookingCategory -> {
                if (searchViewModel.categories.value!!.contains(item)) {
                    searchViewModel.categories.value!!.remove(item)
                } else {
                    searchViewModel.categories.value!!.add(item)
                }
            }
            is CookingMethod -> {
                if (searchViewModel.methods.value!!.contains(item)) {
                    searchViewModel.methods.value!!.remove(item)
                } else {
                    searchViewModel.methods.value!!.add(item)
                }
            }
//            is CookingDifficulty -> {
//
//            }
        }
    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (bottomSheetSearchFilter.state == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetSearchFilter.state = BottomSheetBehavior.STATE_COLLAPSED
                    } else {
                        popBackStack()
                    }
                }
            })
    }

    private fun popBackStack() {
        NavHostFragment.findNavController(this).popBackStack()
    }
}