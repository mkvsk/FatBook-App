package online.fatbook.fatbookapp.ui.search.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.transition.AutoTransition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Scene
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.rv_feed_recipe_card_preview.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.*
import online.fatbook.fatbookapp.core.user.UserSimpleObject
import online.fatbook.fatbookapp.databinding.FragmentSearchBinding
import online.fatbook.fatbookapp.network.callback.ResultCallback
import online.fatbook.fatbookapp.network.request.RecipeSearchRequest
import online.fatbook.fatbookapp.network.request.UserSearchRequest
import online.fatbook.fatbookapp.ui.base.OnRecipeClickListener
import online.fatbook.fatbookapp.ui.feed.adapters.RecipeAdapter
import online.fatbook.fatbookapp.ui.navigation.listeners.BaseFragmentActionsListener
import online.fatbook.fatbookapp.ui.recipe.viewmodel.RecipeViewViewModel
import online.fatbook.fatbookapp.ui.search.adapters.SearchAdapter
import online.fatbook.fatbookapp.ui.search.listeners.OnSearchItemClickListener
import online.fatbook.fatbookapp.ui.search.viewmodel.SearchViewModel
import online.fatbook.fatbookapp.ui.staticdata.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.ui.user.adapters.FollowAdapter
import online.fatbook.fatbookapp.ui.user.listeners.OnUserFollowClickListener
import online.fatbook.fatbookapp.ui.user.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.Constants.TAG_SELECT_ALL_BUTTON
import online.fatbook.fatbookapp.util.RecipeUtils
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils


class SearchFragment : Fragment(), BaseFragmentActionsListener, OnRecipeClickListener,
    OnUserFollowClickListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var adapterRecipe: RecipeAdapter? = null
    private var rvRecipe: RecyclerView? = null
    private var adapterUser: FollowAdapter? = null
    private var rvUser: RecyclerView? = null

    private val recipeViewViewModel by lazy { obtainViewModel(RecipeViewViewModel::class.java) }
    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }
    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }
    private val searchViewModel by lazy { obtainViewModel(SearchViewModel::class.java) }
    private var adapterCategories: SearchAdapter? = null
    private var adapterMethods: SearchAdapter? = null
    private var adapterDifficulty: SearchAdapter? = null

    private lateinit var bottomSheetSearchFilter: BottomSheetBehavior<*>

    private var searchView: SearchView? = null

    final val KEY_STATE_SEARCH = "rv_state"
    private var mBundleRecyclerViewState: Bundle? = null
    private var mListState: Parcelable? = null
    private var mRecyclerView: RecyclerView? = null

    companion object {
        private const val TAG = "SearchFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
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
        searchView = binding.searchView

        binding.loader.progressOverlay.visibility = View.VISIBLE
        searchViewModel.setRecipeSearchRequest(RecipeSearchRequest())
        searchViewModel.setUserSearchRequest(UserSearchRequest())
        setupMenu()
        setupAdapters()
        initViews()
        initObservers()
        initListeners()
    }

    private fun initViews() {
        bottomSheetSearchFilter = BottomSheetBehavior.from(binding.bottomSheetSearch.sheetSearch)
        bottomSheetSearchFilter.state = BottomSheetBehavior.STATE_COLLAPSED
        setSearchMode(
            true,
            searchItem = binding.toolbar.menu.findItem(R.id.menu_search_recipe),
            unselectItem = binding.toolbar.menu.findItem(R.id.menu_search_user)
        )

        if (!searchViewModel.searchUsers.value.isNullOrEmpty()
            || !searchViewModel.searchRecipes.value.isNullOrEmpty()
        ) {
            drawData()
        }
    }

    private fun setupMenu() {
        binding.toolbar.inflateMenu(R.menu.search_menu)
        binding.toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search_recipe -> {
                searchViewModel.setIsSearchRecipe(true)
                setSearchMode(
                    true,
                    searchItem = binding.toolbar.menu.findItem(R.id.menu_search_recipe),
                    unselectItem = binding.toolbar.menu.findItem(R.id.menu_search_user)
                )
                true
            }
            R.id.menu_search_user -> {
                searchViewModel.setIsSearchRecipe(false)
                setSearchMode(
                    false,
                    searchItem = binding.toolbar.menu.findItem(R.id.menu_search_user),
                    unselectItem = binding.toolbar.menu.findItem(R.id.menu_search_recipe)
                )
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun setSearchMode(
        searchRecipe: Boolean,
        searchItem: MenuItem?,
        unselectItem: MenuItem?
    ) {
        androidx.transition.TransitionManager.go(
            Scene(binding.toolbar),
            androidx.transition.AutoTransition()
        )
        binding.searchView.setQuery(StringUtils.EMPTY, false)
        searchItem?.icon!!.setTint(ContextCompat.getColor(context!!, R.color.pink_a200))
        unselectItem?.icon!!.setTint(ContextCompat.getColor(context!!, R.color.main_text))
        if (searchRecipe) {
            binding.searchView.queryHint = "search recipe"
        } else {
            binding.searchView.queryHint = "search user"
        }
    }

    private fun findRecipe(txt: String) {
        bottomSheetSearchFilter.state = BottomSheetBehavior.STATE_COLLAPSED
        searchViewModel.setIsLoading(true)
        searchViewModel.recipeSearchRequest.value!!.searchString = txt
        searchViewModel.searchRecipe(object : ResultCallback<List<RecipeSimpleObject>> {
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

    private fun findUser(txt: String) {
        searchViewModel.userSearchRequest.value!!.searchString = txt
        searchViewModel.searchUser(object : ResultCallback<List<UserSimpleObject>> {
            override fun onResult(value: List<UserSimpleObject>?) {
                searchViewModel.setSearchUsers(value!!)
                drawData()
            }

            override fun onFailure(value: List<UserSimpleObject>?) {
                searchViewModel.setIsLoading(false)
            }
        })
    }

    private fun initListeners() {
        loadCategories()
        loadMethods()
        loadDifficulty()

        bottomSheetSearchFilter.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    TransitionManager.go(
                        android.transition.Scene(binding.clBottomSheet),
                        AutoTransition()
                    )
                    binding.bottomSheetSearch.scrollViewBottomSheetSearch.scrollTo(0, 0)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        binding.bottomSheetSearch.seekbarKcalsLimit.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                binding.bottomSheetSearch.textviewKcalsLimitSettedSearch.text = progress.toString()
                searchViewModel.recipeSearchRequest.value!!.kcal = progress
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        binding.bottomSheetSearch.buttonApplySearch.setOnClickListener {
            searchViewModel.setIsSearchRecipe(true)
            findRecipe(binding.searchView.query.toString())
        }

        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(queryText: String?): Boolean {
                Log.d(TAG, "onQueryTextSubmit: TEST")
                if (binding.searchView.query.length > 2) {
                    hideKeyboard()
                    binding.nsvSearch.smoothScrollTo(0, 0)
                    if (searchViewModel.isSearchRecipe.value == true) {
                        findRecipe(queryText.toString())
                    } else {
                        findUser(queryText.toString())
                    }
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                Log.d(TAG, "onQueryTextChange: EVENT TEXT HAS BEEN CHANGED")
                return true
            }
        })
    }

    private fun drawData() {
        Log.d(TAG, "drawData: ${searchViewModel.searchRecipes.value}")
        Log.d(TAG, "drawDataUsers: ${searchViewModel.searchUsers.value}")

        TransitionManager.go(
            android.transition.Scene(binding.containerSearch),
            android.transition.Fade()
        )
        if (searchViewModel.isSearchRecipe.value == true) {
            binding.searchRvFindUser.root.visibility = View.GONE
            binding.searchRvFindRecipe.root.visibility = View.VISIBLE
            adapterRecipe!!.setData(searchViewModel.searchRecipes.value)
        } else {
            binding.searchRvFindRecipe.root.visibility = View.GONE
            binding.searchRvFindUser.root.visibility = View.VISIBLE
            adapterUser!!.setData(searchViewModel.searchUsers.value)
        }
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
        bottomSheetAdapters()
        recipeAdapter()
        userAdapter()
    }

    private fun userAdapter() {
        rvUser = binding.searchRvFindUser.rvUserSearch
        mRecyclerView = rvUser
        adapterUser = FollowAdapter()
        adapterUser!!.setRvFollowClickListener(this)
        adapterUser!!.setContext(requireContext())
        rvUser!!.adapter = adapterUser
        rvUser!!.adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun recipeAdapter() {
        rvRecipe = binding.searchRvFindRecipe.rvRecipeSearch
        mRecyclerView = rvRecipe
        adapterRecipe = RecipeAdapter()
        adapterRecipe!!.setClickListener(this)
        adapterRecipe!!.setContext(requireContext())
        rvRecipe!!.adapter = adapterRecipe
        rvRecipe!!.adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun bottomSheetAdapters() {
        binding.bottomSheetSearch.rvCookingCategoriesSearch.layoutManager = getLayoutManager()
        adapterCategories = SearchAdapter()
        adapterCategories!!.setClickListener(object : OnSearchItemClickListener {
            override fun onItemClick(item: StaticDataObject) {
                item as CookingCategory
                if (searchViewModel.recipeSearchRequest.value!!.categories.contains(item)) {
                    searchViewModel.recipeSearchRequest.value!!.categories.remove(item)
                } else {
                    searchViewModel.recipeSearchRequest.value!!.categories.add(item)
                }
            }

            override fun onSelectAllClick() {
                if (adapterCategories!!.selectedItems!!.isEmpty()) {
                    searchViewModel.recipeSearchRequest.value!!.categories.clear()
                } else {
                    searchViewModel.recipeSearchRequest.value!!.categories.clear()
                    searchViewModel.recipeSearchRequest.value!!.categories.addAll(
                        searchViewModel.categories.value!!
                    )
                    searchViewModel.recipeSearchRequest.value!!.categories.removeAt(0)
                }
            }
        })
        binding.bottomSheetSearch.rvCookingCategoriesSearch.adapter = adapterCategories

        binding.bottomSheetSearch.rvCookingMethodsSearch.layoutManager = getLayoutManager()
        adapterMethods = SearchAdapter()
        adapterMethods!!.setClickListener(object : OnSearchItemClickListener {
            override fun onItemClick(item: StaticDataObject) {
                item as CookingMethod
                if (searchViewModel.recipeSearchRequest.value!!.methods.contains(item)) {
                    searchViewModel.recipeSearchRequest.value!!.methods.remove(item)
                } else {
                    searchViewModel.recipeSearchRequest.value!!.methods.add(item)
                }
            }

            override fun onSelectAllClick() {
                if (adapterMethods!!.selectedItems!!.isEmpty()) {
                    searchViewModel.recipeSearchRequest.value!!.methods.clear()
                } else {
                    searchViewModel.recipeSearchRequest.value!!.methods.clear()
                    searchViewModel.recipeSearchRequest.value!!.methods.addAll(searchViewModel.methods.value!!)
                    searchViewModel.recipeSearchRequest.value!!.methods.removeAt(0)
                }
            }
        })
        binding.bottomSheetSearch.rvCookingMethodsSearch.adapter = adapterMethods

        binding.bottomSheetSearch.rvCookingDifficultySearch.layoutManager = getLayoutManager()
        adapterDifficulty = SearchAdapter()
        adapterDifficulty!!.setClickListener(object : OnSearchItemClickListener {
            override fun onItemClick(item: StaticDataObject) {
                item as CookingDifficulty
                if (searchViewModel.recipeSearchRequest.value!!.difficulties.contains(item)) {
                    searchViewModel.recipeSearchRequest.value!!.difficulties.remove(item)
                } else {
                    searchViewModel.recipeSearchRequest.value!!.difficulties.add(item)
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
        staticDataViewModel.getAllCookingCategories(object :
            ResultCallback<List<CookingCategory>> {
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
        Log.d(TAG, "onDestroy: SEARCH DEAD")
        super.onDestroy()
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

    //    rv recipe listeners
    override fun onRecipeClick(id: Long) {
        recipeViewViewModel.setSelectedRecipeId(id)
        NavHostFragment.findNavController(this).navigate(R.id.action_go_to_recipe_view_from_search)
    }

    override fun onBookmarksClick(
        recipe: RecipeSimpleObject?,
        bookmark: Boolean,
        position: Int
    ) {
//        TODO
    }

    override fun onForkClicked(recipe: RecipeSimpleObject?, fork: Boolean, position: Int) {
//        TODO
    }

    override fun onForkClicked(
        recipe: RecipeSimpleObject?,
        fork: Boolean,
        position: Int,
        viewHolder: RecipeAdapter.ViewHolder
    ) {
        recipeViewViewModel.recipeFork(recipe!!.pid!!, fork, object : ResultCallback<Int> {
            override fun onResult(value: Int?) {
                if (fork) {
                    userViewModel.user.value!!.recipesForked!!.add(recipe)
                } else {
                    userViewModel.user.value!!.recipesForked!!.removeIf { recipe.pid == it.pid }
                }

//                val tv: TextView = viewHolder.itemView.findViewById(R.id.textView_rv_card_recipe_forks_avg)
//                tv.text = value.toString()
//
//                val vc: ImageView = viewHolder.itemView.findViewById(R.id.view_click_fork)
//                vc.tag = RecipeUtils.TAG_CLICK_FALSE

                viewHolder.itemView.textView_rv_card_recipe_forks_avg.text = value.toString()
                viewHolder.itemView.view_click_fork.tag = RecipeUtils.TAG_CLICK_FALSE
                adapterRecipe!!.toggleForks(viewHolder.itemView.imageView_rv_card_recipe_fork, fork)
            }

            override fun onFailure(value: Int?) {
                Toast.makeText(
                    requireContext(), getString(R.string.error_common_network), Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onUsernameClick(username: String) {
        userViewModel.setSelectedUsername(username)
        findNavController().navigate(R.id.action_go_to_user_profile_from_search)
    }

    //    rv follow listeners
    override fun onSimpleUserClick(username: String) {
        userViewModel.setSelectedUsername(username)
        findNavController().navigate(R.id.action_go_to_user_profile_from_search)
    }

    override fun onUserSendMessageClick() {
//        TODO
    }

    override fun onUserFollowClick() {
//        TODO
    }

    override fun onResume() {
        Log.d("==========SearchFragment==========", "onResume")

        super.onResume()
        if (mBundleRecyclerViewState != null) {
            Looper.myLooper()?.let {
                Handler(it).post {
                    mListState = mBundleRecyclerViewState?.getBundle(KEY_STATE_SEARCH)
                    mRecyclerView!!.layoutManager?.onRestoreInstanceState(mListState)
                }
            }
        }
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        outState.run {
//            putBundle(KEY_STATE_SEARCH, mBundleRecyclerViewState)
//        }
//        super.onSaveInstanceState(outState)
//    }
//
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        mRecyclerView = savedInstanceState?.getParcelable(KEY_STATE_SEARCH)
//    }

    override fun onPause() {
        Log.d("==========SearchFragment==========", "onPause")

        super.onPause()
        mBundleRecyclerViewState = Bundle()
        mListState = mRecyclerView!!.layoutManager?.onSaveInstanceState()
        mBundleRecyclerViewState?.putParcelable(KEY_STATE_SEARCH, mListState)
    }
}