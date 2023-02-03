package online.fatbook.fatbookapp.ui.fragment.recipe

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.size
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingStep
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.FragmentRecipeViewBinding
import online.fatbook.fatbookapp.network.service.RetrofitFactory
import online.fatbook.fatbookapp.ui.adapters.FullRecipeIngredientAdapter
import online.fatbook.fatbookapp.ui.adapters.ViewRecipeCookingStepAdapter
import online.fatbook.fatbookapp.ui.fragment.feed.FeedFragment
import online.fatbook.fatbookapp.ui.viewmodel.AuthenticationViewModel
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.Constants.MAX_PORTIONS
import online.fatbook.fatbookapp.util.Constants.MIN_PORTIONS
import online.fatbook.fatbookapp.util.FormatUtils
import online.fatbook.fatbookapp.util.FormatUtils.roundOffDecimal
import online.fatbook.fatbookapp.util.RecipeUtils
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeViewFragment : Fragment() {

    private var _binding: FragmentRecipeViewBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by lazy { obtainViewModel(AuthenticationViewModel::class.java) }
    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }

    private var recipeForked = false
    private var recipeInFav = false

    private var portionQty: Int = 0

    companion object {
        private const val TAG = "RecipeViewFragment"
    }

    private var recipe: Recipe = Recipe()
    private var ingredientAdapter: FullRecipeIngredientAdapter? = null
    private var stepAdapter: ViewRecipeCookingStepAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeViewModel.setIsLoading(true)
        handleBackPressed()
        setupMenu()

        loadData(recipeViewModel.selectedRecipeId.value!!)
        checkAuthor()

        initViews()
        initListeners()
        initObservers()


        binding.textviewAuthorUsernameRecipeView.setOnClickListener {
            //val v = textview_author_username_recipe_view.text.toString()
//            userViewModel.selectedUsername.value = "hewix"
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_userprofile_from_recipe_view)
        }

        binding.imageViewForkViewRecipe.setOnClickListener {
            if (recipeForked) {
                recipeForked = false
            } else {
                recipeForked = true
            }
            toggleForks(recipeForked)
        }

        binding.imageViewRecipeViewFavourites.setOnClickListener {
            if (recipeInFav) {
                recipeInFav = false
            } else {
                recipeInFav = true
            }
            toggleFavourites(recipeInFav)
        }

        binding.edittextInputComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!str.toString().isNullOrEmpty()) {
                    binding.buttonSendComment.visibility = View.VISIBLE
                } else {
                    binding.buttonSendComment.visibility = View.GONE
                }
            }

            override fun afterTextChanged(str: Editable?) {
            }

        })

        binding.buttonSendComment.setOnClickListener {
            binding.buttonSendComment.visibility = View.GONE
            hideKeyboard(binding.edittextInputComment)
        }

        binding.imageViewIcCommentsViewRecipe.setOnClickListener {
            binding.nsvRecipeView.post {
                binding.nsvRecipeView.scrollTo(
                    0,
                    binding.cardviewRecipeViewFullInfo.bottom
                )
            }
        }

        binding.buttonRemovePortionRecipeView.setOnClickListener {
            if (portionQty > MIN_PORTIONS) {
                binding.buttonAddPortionRecipeView.isClickable = true
                portionQty = portionQty.dec()
                calculateIngredients(portionQty)

                if (portionQty == MIN_PORTIONS) {
                    binding.buttonRemovePortionRecipeView.isClickable = false
                }
                binding.textviewPortionsQtyRecipeView.text = portionQty.toString()
            }

        }

        binding.buttonAddPortionRecipeView.setOnClickListener {
            if (portionQty < MAX_PORTIONS) {
                binding.buttonRemovePortionRecipeView.isClickable = true
                portionQty = portionQty.inc()
                calculateIngredients(portionQty)

                if (portionQty == MAX_PORTIONS) {
                    binding.buttonAddPortionRecipeView.isClickable = false
                }
                binding.textviewPortionsQtyRecipeView.text = portionQty.toString()
            }
        }

    }

    private fun setupMenu() {
        binding.toolbarRecipeView.setOnMenuItemClickListener(this::onOptionsItemSelected)
        binding.toolbarRecipeView.setNavigationOnClickListener {
            popBackStack()
        }
        binding.toolbarRecipeView.inflateMenu(R.menu.recipe_view_menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_recipe_view_edit -> {
//                go to recipe create to edit
                true
            }
            R.id.menu_recipe_view_delete -> {
//                dialogMsg()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initViews() {
        binding.textViewCommentsAvgViewRecipe.text = binding.rvCommentsRecipeView.size.toString()
    }

    private fun initObservers() {
        recipeViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.loader.progressOverlay.visibility = View.VISIBLE
            } else {
                binding.loader.progressOverlay.visibility = View.GONE
            }
        }
    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (recipeViewModel.isLoading.value == true) {
                        recipeViewModel.setIsLoading(false)
                    } else {
                        popBackStack()
                    }
                }
            })
    }

    private fun popBackStack() {
        findNavController().popBackStack()
    }

    private fun calculateIngredients(portionQty: Int) {
        val listRecipeIngredient = ArrayList<RecipeIngredient>()
        recipe.ingredients?.forEach {
            listRecipeIngredient.add(
                it.copy(
                    quantity = it.quantity?.div(recipe.portions!!)!!.times(this.portionQty)
                )
            )
        }
        ingredientAdapter?.setData(listRecipeIngredient)
    }

    private fun setupIngredientAdapter(ingredients: ArrayList<RecipeIngredient>?) {
        val rv = binding.rvIngredientsRecipeView
        ingredientAdapter = FullRecipeIngredientAdapter()
        ingredientAdapter!!.setData(ingredients)
        rv.adapter = ingredientAdapter
    }

    private fun setupStepAdapter(cookingSteps: ArrayList<CookingStep>?) {
        val rv = binding.rvCookingStepsRecipeView
        stepAdapter = ViewRecipeCookingStepAdapter(requireContext())
        stepAdapter!!.setData(cookingSteps)
        rv.adapter = stepAdapter
    }

    private fun checkAuthor() {
        //setup menu for author/viewer
//                TODO Fix icons, check author
//        if (recipe.user!!.username!! == userViewModel.user.value?.username) {
//            binding.toolbarRecipeView.inflateMenu(R.menu.recipe_view_menu)
//        }
    }

    private fun toggleFavourites(inFavourite: Boolean) {
        if (inFavourite) {
            binding.imageViewRecipeViewFavourites.setImageResource(R.drawable.ic_add_to_fav)
            binding.imageViewRecipeViewFavourites.tag = RecipeUtils.TAG_FAVOURITES_CHECKED
        } else {
            binding.imageViewRecipeViewFavourites.setImageResource(R.drawable.ic_not_fav)
            binding.imageViewRecipeViewFavourites.tag = RecipeUtils.TAG_FAVOURITES_UNCHECKED
        }

        recipeSaveToFav(recipe, inFavourite)
    }

    private fun recipeSaveToFav(recipe: Recipe, inFavourite: Boolean) {
        Toast.makeText(requireContext(), "bookmarked", Toast.LENGTH_SHORT).show()
        RetrofitFactory.apiService()
            .recipeBookmarked(userViewModel.user.value?.pid, recipe.pid, inFavourite)
            .enqueue(object : Callback<Recipe?> {
                override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
                    Log.d(RecipeViewFragment.TAG, "onResponse: bookmark SUCCESS")
                    recipeViewModel.setSelectedRecipe(response.body())
                    loadUser()
                }

                override fun onFailure(call: Call<Recipe?>, t: Throwable) {
                    Log.d(RecipeViewFragment.TAG, "onResponse: bookmark FAILED")
                }
            })
    }

    private fun loadUser() {
        userViewModel.getUserByUsername(authViewModel.username.value!!,
            object : ResultCallback<User> {
                override fun onResult(value: User?) {
                    userViewModel.setUser(value!!)
                }

                override fun onFailure(value: User?) {
                }
            })
    }

    private fun toggleForks(forked: Boolean) {
        if (forked) {
            binding.imageViewForkViewRecipe.setImageResource(R.drawable.ic_fork_checked)
            binding.imageViewForkViewRecipe.tag = RecipeUtils.TAG_FORK_CHECKED
        } else {
            binding.imageViewForkViewRecipe.setImageResource(R.drawable.ic_fork_unchecked)
            binding.imageViewForkViewRecipe.tag = RecipeUtils.TAG_FORK_UNCHECKED
        }
    }

    private fun loadData(id: Long) {
        recipeViewModel.getRecipeById(id, object : ResultCallback<Recipe> {
            override fun onResult(value: Recipe?) {
                recipe = value!!
                drawData()
            }

            override fun onFailure(value: Recipe?) {
                Toast.makeText(requireContext(), "recipe load failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun drawData() {
        binding.textviewAuthorUsernameRecipeView.text = recipe.user?.username
        binding.textviewDateRecipe.text = FormatUtils.getCreateDate(recipe.createDate.toString())
        binding.textViewRecipeViewRecipeTitle.text = recipe.title
        binding.textviewDifficultyRecipeView.text = recipe.difficulty!!.title.toString()
        binding.textviewCookingTimeRecipeView.text = recipe.cookingTime.toString()
        binding.textviewMethodRecipeView.text = recipe.cookingMethod!!.title.toString()
        binding.textviewCategoriesRecipeView.text =
            recipe.cookingCategories!!.joinToString { it.title.toString() }
        if (recipe.isAllIngredientUnitsValid) {
            binding.cardviewNutritionFactsRecipeView.visibility = View.VISIBLE
            binding.textviewPortionKcalsQtyRecipeView.text =
                roundOffDecimal(recipe.kcalPerPortion!!).toString()
            binding.tvQtyProteins.text = roundOffDecimal(recipe.proteinsPerPortion!!).toString()
            binding.tvQtyFats.text = roundOffDecimal(recipe.fatsPerPortion!!).toString()
            binding.tvQtyCarbs.text = roundOffDecimal(recipe.carbsPerPortion!!).toString()
        } else {
            binding.cardviewNutritionFactsRecipeView.visibility = View.GONE
        }
        binding.textviewPortionsQtyRecipeView.text = recipe.portions.toString()

        Glide
            .with(requireContext())
            .load(recipe.image)
            .placeholder(requireContext().getDrawable(R.drawable.default_recipe_image_rv_feed))
            .into(binding.imageViewRecipePhoto)

        Glide
            .with(requireContext())
            .load(recipe.user?.profileImage)
            .placeholder(requireContext().getDrawable(R.drawable.ic_default_userphoto))
            .into(binding.imageviewAuthorPhotoRecipeView)

        binding.textViewForksAvgViewRecipe.text = convertNumeric(recipe.forks!!)
        binding.textViewCommentsAvgViewRecipe.text = convertNumeric(recipe.comments?.size ?: 0)

        portionQty = binding.textviewPortionsQtyRecipeView.text.toString().toInt()
        if (portionQty == MIN_PORTIONS) {
            binding.buttonRemovePortionRecipeView.isClickable = false
        }
        if (portionQty == MAX_PORTIONS) {
            binding.buttonAddPortionRecipeView.isClickable = false
        }

        setupIngredientAdapter(recipe.ingredients)
        setupStepAdapter(recipe.steps)

        userViewModel.user.value?.recipesFavourites?.forEach {
            recipeInFav = (it.identifier?.equals(recipe.identifier) == true)
            toggleFavourites(recipeInFav)
        }

        userViewModel.user.value?.recipesForked?.forEach {
            recipeForked = (it.identifier?.equals(recipe.identifier) == true)
            toggleForks(recipeForked)
        }

        recipeViewModel.setIsLoading(false)
    }

    private fun initListeners() {

    }

    private fun convertNumeric(value: Int): String {
        return "%,d".format(value)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

