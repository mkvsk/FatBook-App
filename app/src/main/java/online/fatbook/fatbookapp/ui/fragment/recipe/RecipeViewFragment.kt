package online.fatbook.fatbookapp.ui.fragment.recipe

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.view.size
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.recipe.CookingStep
import online.fatbook.fatbookapp.core.recipe.Recipe
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.databinding.FragmentRecipeViewBinding
import online.fatbook.fatbookapp.ui.adapters.FullRecipeIngredientAdapter
import online.fatbook.fatbookapp.ui.adapters.ViewRecipeCookingStepAdapter
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.Constants.MAX_PORTIONS
import online.fatbook.fatbookapp.util.Constants.MIN_PORTIONS
import online.fatbook.fatbookapp.util.FormatUtils
import online.fatbook.fatbookapp.util.FormatUtils.roundOffDecimal
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel

class RecipeViewFragment : Fragment() {

    private var _binding: FragmentRecipeViewBinding? = null
    private val binding get() = _binding!!

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

        binding.loader.progressOverlay.visibility = View.VISIBLE
        checkAuthor()
        loadData(recipeViewModel.selectedRecipeId.value!!)

        binding.textviewAuthorUsernameRecipeView.setOnClickListener {
            //val v = textview_author_username_recipe_view.text.toString()
//            userViewModel.selectedUsername.value = "hewix"
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_userprofile_from_recipe_view)
        }

        binding.imageViewForkViewRecipe.setOnClickListener {
            toggleForks(recipeForked)
        }

        binding.imageViewRecipeViewFavourites.setOnClickListener {
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

        binding.textViewCommentsAvgViewRecipe.text = binding.rvCommentsRecipeView.size.toString()

        initListeners()
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
    }

    private fun toggleFavourites(inFavourite: Boolean) {
        recipeInFav = if (inFavourite) {
            Glide
                .with(requireContext())
                .load(requireContext().getDrawable(R.drawable.ic_not_fav))
                .into(binding.imageViewRecipeViewFavourites)
            Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show()
            false
        } else {
            Glide
                .with(requireContext())
                .load(requireContext().getDrawable(R.drawable.ic_add_to_fav))
                .into(binding.imageViewRecipeViewFavourites)
            Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun toggleForks(forked: Boolean) {
        recipeForked = if (forked) {
            Glide
                .with(requireContext())
                .load(requireContext().getDrawable(R.drawable.ic_fork_unchecked))
                .into(binding.imageViewForkViewRecipe)
            Toast.makeText(context, "Recipe not forked :(", Toast.LENGTH_SHORT).show()
            false
        } else {
            Glide
                .with(requireContext())
                .load(requireContext().getDrawable(R.drawable.ic_fork_checked))
                .into(binding.imageViewForkViewRecipe)
            Toast.makeText(context, "Recipe forked!", Toast.LENGTH_SHORT).show()
            true
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
        binding.loader.progressOverlay.visibility = View.GONE
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

