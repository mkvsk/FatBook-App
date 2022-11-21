package online.fatbook.fatbookapp.ui.fragment.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_search.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.include_progress_overlay.*
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingDifficulty
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.core.recipe.StaticDataBase
import online.fatbook.fatbookapp.databinding.FragmentSearchBinding
import online.fatbook.fatbookapp.ui.adapters.SearchAdapter
import online.fatbook.fatbookapp.ui.listeners.BaseFragmentActions
import online.fatbook.fatbookapp.ui.listeners.OnSearchItemClickListener
import online.fatbook.fatbookapp.ui.viewmodel.SearchViewModel
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.util.SearchUtils
import online.fatbook.fatbookapp.util.obtainViewModel

class SearchFragment : Fragment(), OnSearchItemClickListener, BaseFragmentActions {

    private var binding: FragmentSearchBinding? = null
    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }
    private val searchViewModel by lazy { obtainViewModel(SearchViewModel::class.java) }
    private var adapterCategories: SearchAdapter? = null
    private var adapterMethods: SearchAdapter? = null
    private var adapterDifficulty: SearchAdapter? = null

    private lateinit var bottomSheetSearchFilter: BottomSheetBehavior<*>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("===t=======SearchFragment==========", "onViewCreated")
        progress_overlay.visibility = View.VISIBLE
        toolbar_search.visibility = View.GONE

        if (searchViewModel.categories.value.isNullOrEmpty()) {
            searchViewModel.categories.value = ArrayList()
        }
        if (searchViewModel.methods.value.isNullOrEmpty()) {
            searchViewModel.methods.value = ArrayList()
        }
        if (searchViewModel.difficulties.value.isNullOrEmpty()) {
            searchViewModel.difficulties.value = ArrayList()
        }

        setupAdapters()

        loadCategories()
        loadMethods()
        loadDifficulty()

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

        seekbar_kcals_limit.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                textview_kcals_limit_setted_search.text = progress.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        handleBackPressed()
    }

    private fun checkStaticDataLoaded() {
        if (!staticDataViewModel.cookingMethods.value.isNullOrEmpty() && !staticDataViewModel.cookingCategories.value.isNullOrEmpty() && !staticDataViewModel.cookingDifficulties.value.isNullOrEmpty()) {
            toolbar_search.visibility = View.VISIBLE
            progress_overlay.visibility = View.GONE
        }
    }

    private fun setupAdapters() {
        adapterCategories = SearchAdapter()
        setupAdapter(rv_cooking_categories_search, adapterCategories!!)

        adapterMethods = SearchAdapter()
        setupAdapter(rv_cooking_methods_search, adapterMethods!!)

        adapterDifficulty = SearchAdapter()
        setupAdapter(rv_cooking_difficulty_search, adapterDifficulty!!)
    }

    private fun setupAdapter(rv: RecyclerView, adapter: SearchAdapter) {
        rv.layoutManager = getLayoutManager()
        adapter.setClickListener(this)
        rv.adapter = adapter
    }

    private fun getLayoutManager(): RecyclerView.LayoutManager {
        return FlexboxLayoutManager(context).apply {
            this.flexDirection = FlexDirection.ROW
            this.flexWrap = FlexWrap.WRAP
            this.justifyContent = JustifyContent.FLEX_START
        }
    }

    private fun loadCategories() {
        staticDataViewModel.getAllCookingCategories(object : ResultCallback<List<CookingCategory>> {
            override fun onResult(value: List<CookingCategory>?) {

                value as ArrayList
                value.add(0, SearchUtils.getSelectAll(CookingCategory::class.java))

                staticDataViewModel.cookingCategories.value = value
                adapterCategories?.setData(value)
                adapterCategories?.setSelected(getPreselectedCategories())
                checkStaticDataLoaded()
            }

            override fun onFailure(value: List<CookingCategory>?) {
                loadCategories()
            }
        })
    }

    private fun loadMethods() {
        staticDataViewModel.getAllCookingMethods(object : ResultCallback<List<CookingMethod>> {
            override fun onResult(value: List<CookingMethod>?) {

                value as ArrayList
                value.add(0, SearchUtils.getSelectAll(CookingMethod::class.java))

                staticDataViewModel.cookingMethods.value = value
                adapterMethods?.setData(value)
                adapterMethods?.setSelected(getPreselectedMethods())
                checkStaticDataLoaded()
            }

            override fun onFailure(value: List<CookingMethod>?) {
                loadMethods()
            }
        })
    }

    private fun loadDifficulty() {
        staticDataViewModel.getAllCookingDifficulties(object :
            ResultCallback<List<CookingDifficulty>> {
            override fun onResult(value: List<CookingDifficulty>?) {
                staticDataViewModel.cookingDifficulties.value = value
                adapterDifficulty?.setData(value)
                adapterDifficulty?.setSelected(getPreselectedDifficulties())
                checkStaticDataLoaded()
            }

            override fun onFailure(value: List<CookingDifficulty>?) {
                loadDifficulty()
            }
        })
    }

    //TODO shared prefs
    private fun getPreselectedCategories(): List<Int> {
        return ArrayList()
    }

    //TODO shared prefs
    private fun getPreselectedMethods(): List<Int> {
        return ArrayList()
    }

    //TODO shared prefs
    private fun getPreselectedDifficulties(): List<Int> {
        return ArrayList()
    }

    override fun onItemClick(item: StaticDataBase) {

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
            is CookingDifficulty -> {
                if (searchViewModel.difficulties.value!!.contains(item)) {
                    searchViewModel.difficulties.value!!.remove(item)
                } else {
                    searchViewModel.difficulties.value!!.add(item)
                }
            }
        }
    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
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

    override fun onDestroy() {
        super.onDestroy()
        Log.d("===t=======SearchFragment==========", "onDestroy")
    }

    override fun onBackPressedBase(): Boolean {
        return if (bottomSheetSearchFilter.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetSearchFilter.state = BottomSheetBehavior.STATE_COLLAPSED
            true
        } else {
            popBackStack()
            false
        }
    }

    override fun scrollUpBase() {
        search_layout.scrollTo(0, 0)
        appBarLayout_search.setExpanded(true, false)
    }
}