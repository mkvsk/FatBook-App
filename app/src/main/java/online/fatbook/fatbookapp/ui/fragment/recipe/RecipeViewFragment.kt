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
import online.fatbook.fatbookapp.databinding.FragmentRecipeViewBinding
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel

class RecipeViewFragment : Fragment() {

    private var _binding: FragmentRecipeViewBinding? = null
    private val binding get() = _binding!!

    private val recipeViewModel by lazy { obtainViewModel(RecipeViewModel::class.java) }
    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }

    private var recipeForked = false
    private var recipeInFav = false

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
        loadData(123123L)
        drawData()
        binding.loader.progressOverlay.visibility = View.GONE

        binding.textviewAuthorUsernameRecipeView.setOnClickListener {
            //val v = textview_author_username_recipe_view.text.toString()
            userViewModel.selectedUsername.value = "hewix"
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


        var qtt = 5
        binding.buttonRemovePortionRecipeView.setOnClickListener {
            if (qtt > 1) {
                binding.buttonRemovePortionRecipeView.isEnabled = true
                qtt--
                binding.textviewPortionsQttRecipeView.text = qtt.toString()
            }
            if (qtt == 1) {
                binding.buttonRemovePortionRecipeView.isEnabled = false
            }
        }

        binding.buttonAddPortionRecipeView.setOnClickListener {
            qtt++
            binding.buttonRemovePortionRecipeView.isEnabled = true
            binding.textviewPortionsQttRecipeView.text = qtt.toString()
        }

        binding.textViewCommentsAvgViewRecipe.text = binding.rvCommentsRecipeView.size.toString()

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
//        val recipe: Recipe = Recipe(
//            pid = 1235L,
//            title = "Kartoshechka",
//            author = "Neshik",
//            ingredients = ArrayList(),
//            image = "https://fatbook.b-cdn.net/root/upal.jpg",
//            forks = 123456789,
//            createDate = "10.09.2022", identifier = id
//        )

//        recipeViewModel.selectedRecipe.value = recipe

    }

    private fun drawData() {
        val recipe = recipeViewModel.selectedRecipe.value
        Glide
            .with(requireContext())
            .load(recipe!!.image)
            .into(binding.imageViewRecipePhoto)

        binding.textViewForksAvgViewRecipe.text = convertNumeric(recipe.forks!!)
    }

    private fun convertNumeric(value: Int): String {
        return "%,d".format(value)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

