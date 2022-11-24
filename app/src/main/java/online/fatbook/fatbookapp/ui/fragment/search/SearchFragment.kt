package online.fatbook.fatbookapp.ui.fragment.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
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
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingCategory
import online.fatbook.fatbookapp.core.recipe.CookingDifficulty
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.core.recipe.StaticDataObject
import online.fatbook.fatbookapp.databinding.FragmentSearchBinding
import online.fatbook.fatbookapp.ui.adapters.SearchAdapter
import online.fatbook.fatbookapp.ui.listeners.BaseFragmentActions
import online.fatbook.fatbookapp.ui.listeners.OnSearchItemClickListener
import online.fatbook.fatbookapp.ui.viewmodel.SearchViewModel
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.util.Constants.TAG_SELECT_ALL_BUTTON
import online.fatbook.fatbookapp.util.obtainViewModel

class SearchFragment : Fragment(), BaseFragmentActions {

    private var binding: FragmentSearchBinding? = null
    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }
    private val searchViewModel by lazy { obtainViewModel(SearchViewModel::class.java) }
    private var adapterCategories: SearchAdapter? = null
    private var adapterMethods: SearchAdapter? = null
    private var adapterDifficulty: SearchAdapter? = null

    private lateinit var bottomSheetSearchFilter: BottomSheetBehavior<*>

    companion object {
        private const val TAG = "SearchFragment"
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("==========SearchFragment==========", "onViewCreated")
        progress_overlay.visibility = View.VISIBLE
        toolbar_search.visibility = View.GONE

        if (searchViewModel.selectedCategories.value.isNullOrEmpty()) {
            searchViewModel.selectedCategories.value = ArrayList()
        }
        if (searchViewModel.selectedMethods.value.isNullOrEmpty()) {
            searchViewModel.selectedMethods.value = ArrayList()
        }
        if (searchViewModel.selectedDifficulties.value.isNullOrEmpty()) {
            searchViewModel.selectedDifficulties.value = ArrayList()
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

        //TODO save to shared prefs ??????????????
        button_apply_search.setOnClickListener {
            Log.d(TAG, "methods: ${searchViewModel.selectedMethods.value!!.size}; ${searchViewModel.selectedMethods.value}")
            Log.d(TAG, "categories: ${searchViewModel.selectedCategories.value!!.size}; ${searchViewModel.selectedCategories.value}")
            Log.d(TAG, "difficulties: ${searchViewModel.selectedDifficulties.value!!.size}; ${searchViewModel.selectedDifficulties.value}")
            Log.d(TAG, "kcal: ${seekbar_kcals_limit.progress}")
            bottomSheetSearchFilter.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun checkStaticDataLoaded() {
        if (!searchViewModel.methods.value.isNullOrEmpty() && !searchViewModel.categories.value.isNullOrEmpty() && !searchViewModel.difficulties.value.isNullOrEmpty()) {
            toolbar_search.visibility = View.VISIBLE
            progress_overlay.visibility = View.GONE
        }
    }

    private fun setupAdapters() {
        rv_cooking_categories_search.layoutManager = getLayoutManager()
        adapterCategories = SearchAdapter()
        adapterCategories!!.setClickListener(object : OnSearchItemClickListener {
            override fun onItemClick(item: StaticDataObject) {
                item as CookingCategory
                if (searchViewModel.selectedCategories.value!!.contains(item)) {
                    searchViewModel.selectedCategories.value!!.remove(item)
                } else {
                    searchViewModel.selectedCategories.value!!.add(item)
                }
            }

            override fun onSelectAllClick() {
                if (adapterCategories!!.selectedItems!!.isEmpty()) {
                    searchViewModel.selectedCategories.value!!.clear()
                } else {
                    searchViewModel.selectedCategories.value!!.clear()
                    searchViewModel.selectedCategories.value!!.addAll(searchViewModel.categories.value!!)
                    searchViewModel.selectedCategories.value!!.removeAt(0)
                }
            }
        })
        rv_cooking_categories_search.adapter = adapterCategories

        rv_cooking_methods_search.layoutManager = getLayoutManager()
        adapterMethods = SearchAdapter()
        adapterMethods!!.setClickListener(object : OnSearchItemClickListener {
            override fun onItemClick(item: StaticDataObject) {
                item as CookingMethod
                if (searchViewModel.selectedMethods.value!!.contains(item)) {
                    searchViewModel.selectedMethods.value!!.remove(item)
                } else {
                    searchViewModel.selectedMethods.value!!.add(item)
                }
            }

            override fun onSelectAllClick() {
                if (adapterMethods!!.selectedItems!!.isEmpty()) {
                    searchViewModel.selectedMethods.value!!.clear()
                } else {
                    searchViewModel.selectedMethods.value!!.clear()
                    searchViewModel.selectedMethods.value!!.addAll(searchViewModel.methods.value!!)
                    searchViewModel.selectedMethods.value!!.removeAt(0)
                }
            }
        })
        rv_cooking_methods_search.adapter = adapterMethods

        rv_cooking_difficulty_search.layoutManager = getLayoutManager()
        adapterDifficulty = SearchAdapter()
        adapterDifficulty!!.setClickListener(object : OnSearchItemClickListener {
            override fun onItemClick(item: StaticDataObject) {
                item as CookingDifficulty
                if (searchViewModel.selectedDifficulties.value!!.contains(item)) {
                    searchViewModel.selectedDifficulties.value!!.remove(item)
                } else {
                    searchViewModel.selectedDifficulties.value!!.add(item)
                }
            }

            override fun onSelectAllClick() {
            }
        })
        rv_cooking_difficulty_search.adapter = adapterDifficulty
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
                searchViewModel.categories.value = value as ArrayList<CookingCategory>
                adapterCategories?.setData(value)
                adapterCategories?.setSelectAll(StaticDataObject(getString(R.string.title_search_btn_select_all), TAG_SELECT_ALL_BUTTON))
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
                searchViewModel.methods.value = value as ArrayList<CookingMethod>
                adapterMethods?.setData(value)
                adapterMethods?.setSelectAll(StaticDataObject(getString(R.string.title_search_btn_select_all), TAG_SELECT_ALL_BUTTON))
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
                searchViewModel.difficulties.value = value as ArrayList<CookingDifficulty>
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
    private fun getPreselectedCategories(): ArrayList<Int> {
        return ArrayList()
    }

    //TODO shared prefs
    private fun setPreselectedCategories(value: Int) {
        getPreselectedCategories().add(value)
    }

    //TODO shared prefs
    private fun getPreselectedMethods(): ArrayList<Int> {
        return ArrayList()
    }

    //TODO shared prefs
    private fun setPreselectedMethods(value: Int) {
        getPreselectedMethods().add(value)
    }

    //TODO shared prefs
    private fun getPreselectedDifficulties(): ArrayList<Int> {
        return ArrayList()
    }

    //TODO shared prefs
    private fun setPreselectedDifficulties(value: Int) {
        getPreselectedDifficulties().add(value)
    }

    private fun popBackStack() {
        NavHostFragment.findNavController(this).popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("==========SearchFragment==========", "onDestroy")
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