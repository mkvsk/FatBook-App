package online.fatbook.fatbookapp.ui.fragment.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.*
import online.fatbook.fatbookapp.databinding.FragmentSearchBinding
import online.fatbook.fatbookapp.network.request.SearchRequest
import online.fatbook.fatbookapp.ui.adapters.SearchAdapter
import online.fatbook.fatbookapp.ui.listeners.BaseFragmentActionsListener
import online.fatbook.fatbookapp.ui.listeners.OnSearchItemClickListener
import online.fatbook.fatbookapp.ui.viewmodel.SearchViewModel
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.util.Constants.TAG_SELECT_ALL_BUTTON
import online.fatbook.fatbookapp.util.obtainViewModel

class SearchFragment : Fragment(), BaseFragmentActionsListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("==========SearchFragment==========", "onViewCreated")
        binding.loader.progressOverlay.visibility = View.VISIBLE
        searchViewModel.setSearchRequest(SearchRequest())
        setupAdapters()
        initObservers()
        setupListeners()
        loadCategories()
        loadMethods()
        loadDifficulty()

        bottomSheetSearchFilter = BottomSheetBehavior.from(binding.bottomSheetSearch.sheetSearch)
        bottomSheetSearchFilter.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetSearchFilter.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.bottomSheetSearch.scrollViewBottomSheetSearch.scrollTo(0, 0)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

    }

    private fun setupListeners() {
        binding.bottomSheetSearch.seekbarKcalsLimit.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                binding.bottomSheetSearch.textviewKcalsLimitSettedSearch.text = progress.toString()
                searchViewModel.searchRequest.value!!.kcal = progress
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        binding.bottomSheetSearch.buttonApplySearch.setOnClickListener {
            bottomSheetSearchFilter.state = BottomSheetBehavior.STATE_COLLAPSED
            searchViewModel.setIsLoading(true)
            searchViewModel.search(object : ResultCallback<List<RecipeSimpleObject>> {
                override fun onResult(value: List<RecipeSimpleObject>?) {
                    value?.let {
                        searchViewModel.setSearchRecipes(value)
                        drawData()
                    }
                }

                override fun onFailure(value: List<RecipeSimpleObject>?) {
                    searchViewModel.setIsLoading(false)
                }
            })
        }
    }

    private fun drawData() {
        Log.d(TAG, "drawData: ${searchViewModel.searchRecipes.value}")
        searchViewModel.setIsLoading(false)
    }

    private fun initObservers() {
        searchViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.loader.progressOverlay.visibility = View.VISIBLE
            } else {
                binding.loader.progressOverlay.visibility = View.GONE
            }
        }
    }

    private fun checkStaticDataLoaded() {
        if (!searchViewModel.methods.value.isNullOrEmpty() && !searchViewModel.categories.value.isNullOrEmpty() && !searchViewModel.difficulties.value.isNullOrEmpty()) {
            binding.toolbar.visibility = View.VISIBLE
            binding.loader.progressOverlay.visibility = View.GONE
        }
    }

    private fun setupAdapters() {
        binding.bottomSheetSearch.rvCookingCategoriesSearch.layoutManager = getLayoutManager()
        adapterCategories = SearchAdapter()
        adapterCategories!!.setClickListener(object : OnSearchItemClickListener {
            override fun onItemClick(item: StaticDataObject) {
                item as CookingCategory
                if (searchViewModel.searchRequest.value!!.categories.contains(item)) {
                    searchViewModel.searchRequest.value!!.categories.remove(item)
                } else {
                    searchViewModel.searchRequest.value!!.categories.add(item)
                }
            }

            override fun onSelectAllClick() {
                if (adapterCategories!!.selectedItems!!.isEmpty()) {
                    searchViewModel.searchRequest.value!!.categories.clear()
                } else {
                    searchViewModel.searchRequest.value!!.categories.clear()
                    searchViewModel.searchRequest.value!!.categories.addAll(searchViewModel.categories.value!!)
                    searchViewModel.searchRequest.value!!.categories.removeAt(0)
                }
            }
        })
        binding.bottomSheetSearch.rvCookingCategoriesSearch.adapter = adapterCategories

        binding.bottomSheetSearch.rvCookingMethodsSearch.layoutManager = getLayoutManager()
        adapterMethods = SearchAdapter()
        adapterMethods!!.setClickListener(object : OnSearchItemClickListener {
            override fun onItemClick(item: StaticDataObject) {
                item as CookingMethod
                if (searchViewModel.searchRequest.value!!.methods.contains(item)) {
                    searchViewModel.searchRequest.value!!.methods.remove(item)
                } else {
                    searchViewModel.searchRequest.value!!.methods.add(item)
                }
            }

            override fun onSelectAllClick() {
                if (adapterMethods!!.selectedItems!!.isEmpty()) {
                    searchViewModel.searchRequest.value!!.methods.clear()
                } else {
                    searchViewModel.searchRequest.value!!.methods.clear()
                    searchViewModel.searchRequest.value!!.methods.addAll(searchViewModel.methods.value!!)
                    searchViewModel.searchRequest.value!!.methods.removeAt(0)
                }
            }
        })
        binding.bottomSheetSearch.rvCookingMethodsSearch.adapter = adapterMethods

        binding.bottomSheetSearch.rvCookingDifficultySearch.layoutManager = getLayoutManager()
        adapterDifficulty = SearchAdapter()
        adapterDifficulty!!.setClickListener(object : OnSearchItemClickListener {
            override fun onItemClick(item: StaticDataObject) {
                item as CookingDifficulty
                if (searchViewModel.searchRequest.value!!.difficulties.contains(item)) {
                    searchViewModel.searchRequest.value!!.difficulties.remove(item)
                } else {
                    searchViewModel.searchRequest.value!!.difficulties.add(item)
                }
            }

            override fun onSelectAllClick() {
            }
        })
        binding.bottomSheetSearch.rvCookingDifficultySearch.adapter = adapterDifficulty
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
                searchViewModel.setCategories(ArrayList(value!!))
                staticDataViewModel.setCookingCategories(ArrayList(value))
                adapterCategories?.setData(value)
                adapterCategories?.setSelectAll(
                    StaticDataObject(
                        getString(R.string.title_search_btn_select_all), TAG_SELECT_ALL_BUTTON
                    )
                )
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
                searchViewModel.setMethods(ArrayList(value!!))
                staticDataViewModel.setCookingMethods(ArrayList(value))
                adapterMethods?.setData(value)
                adapterMethods?.setSelectAll(
                    StaticDataObject(
                        getString(R.string.title_search_btn_select_all), TAG_SELECT_ALL_BUTTON
                    )
                )
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
                searchViewModel.setDifficulties(ArrayList(value!!))
                staticDataViewModel.setCookingDifficulties(ArrayList(value))
                adapterDifficulty?.setData(value)
                checkStaticDataLoaded()
            }

            override fun onFailure(value: List<CookingDifficulty>?) {
                loadDifficulty()
            }
        })
    }

    private fun popBackStack() {
        findNavController().popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("==========SearchFragment==========", "onDestroy")
        _binding = null
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
        binding.searchLayout.scrollTo(0, 0)
        binding.appBarLayout.setExpanded(true, false)
    }
}