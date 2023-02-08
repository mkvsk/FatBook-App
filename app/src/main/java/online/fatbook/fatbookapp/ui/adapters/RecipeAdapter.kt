package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import lombok.extern.java.Log
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.RvFeedRecipeCardPreviewBinding
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener
import online.fatbook.fatbookapp.util.FormatUtils
import online.fatbook.fatbookapp.util.RecipeUtils
import org.apache.commons.lang3.StringUtils
import java.time.LocalTime

@Log
class RecipeAdapter :
    RecyclerView.Adapter<RecipeAdapter.ViewHolder>(), BindableAdapter<RecipeSimpleObject> {
    private var _binding: RvFeedRecipeCardPreviewBinding? = null
    private val binding get() = _binding!!

    private var data: List<RecipeSimpleObject> = ArrayList()
    private var user: User = User()
    private lateinit var listener: OnRecipeClickListener
    private var context: Context? = null

    private var recipeForked = false
    private var recipeInFav = false

    fun setUser(user: User) {
        this.user = user
    }

    fun setContext(context: Context) {
        this.context = context
    }

    fun setClickListener(listener: OnRecipeClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding =
            RvFeedRecipeCardPreviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<RecipeSimpleObject>?) {
        data?.let {
            this.data = it
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<RecipeSimpleObject>?, user: User?) {
        data?.let {
            this.data = it
        }
        user?.let {
            this.user = it
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(recipe: RecipeSimpleObject) {
            if (StringUtils.isNotEmpty(recipe.image)) {
                Glide
                    .with(itemView.context)
                    .load(recipe.image)
                    .into(binding.imageViewRvCardRecipePhoto)
            } else {
                binding.imageViewRvCardRecipePhoto.setImageResource(R.drawable.default_recipe_image_rv_feed)
            }

            binding.textViewRvCardRecipeTitle.text = recipe.title
            if (!recipe.ingredientsLocalizedMap.isNullOrEmpty()) {
                binding.rvIngredientsPreview.text = String.format(
                    context!!.getString(R.string.title_ingredients_rv_recipe),
                    recipe.ingredientQty,
                    recipe.ingredientsStr
                )
            }
            if (recipe.kcalPerPortion == 0.0) {
                binding.rvRecipeKcalImg.visibility = View.GONE
                binding.rvRecipeKcal.visibility = View.GONE
            } else {
                binding.rvRecipeKcalImg.visibility = View.VISIBLE
                binding.rvRecipeKcal.visibility = View.VISIBLE
            }
            binding.rvRecipeKcal.text = String.format(
                context!!.getString(R.string.format_kcal),
                recipe.kcalPerPortion.toString()
            )
            binding.rvRecipeCookingTime.text =
                getCookingTime(LocalTime.parse(recipe.cookingTime))

            binding.rvRecipeDifficulty.text = recipe.difficulty?.title

            binding.rvRecipeCreateDate.text = FormatUtils.getCreateDate(recipe.createDate!!)

            binding.rvRecipeAuthor.text = recipe.user!!.username
            Glide
                .with(itemView.context)
                .load(recipe.user!!.profileImage)
                .placeholder(itemView.context.getDrawable(R.drawable.ic_default_userphoto))
                .into(binding.imageviewAuthorPhotoRvRecipePreview)
            binding.rvRecipeCommentsQty.text = recipe.commentQty.toString()
            binding.textViewRvCardRecipeForksAvg.text =
                FormatUtils.prettyCount(recipe.forks!!)
            binding.llAuthorLinkRvRecipePreview.setOnClickListener {
                listener.onUsernameClick(data[bindingAdapterPosition].user!!.username!!)
            }

            user.recipesForked?.forEach {
                recipeForked = (it.identifier?.equals(recipe.identifier) == true)
            }
            toggleForks(binding.imageViewRvCardRecipeFork, recipeForked)

            user.recipesFavourites?.forEach {
                recipeInFav = (it.identifier?.equals(recipe.identifier) == true)
            }
            if (recipe.user?.username == user.username) {
                binding.imageViewRvCardRecipeFavourites.visibility = View.INVISIBLE
            } else {
                binding.imageViewRvCardRecipeFavourites.visibility = View.VISIBLE
                toggleFavourites(binding.imageViewRvCardRecipeFavourites, recipeInFav)
            }

            binding.rvCardRecipePreview.setOnClickListener {
                listener.onRecipeClick(recipe.identifier!!)
            }

            binding.imageViewRvCardRecipeFavourites.setOnClickListener {
                when (binding.imageViewRvCardRecipeFavourites.tag as String) {
                    RecipeUtils.TAG_FAVOURITES_UNCHECKED -> {
                        toggleFavourites(binding.imageViewRvCardRecipeFavourites, true)
                        listener.onBookmarksClick(
                            data[bindingAdapterPosition],
                            true,
                            bindingAdapterPosition
                        )
                    }
                    RecipeUtils.TAG_FAVOURITES_CHECKED -> {
                        toggleFavourites(binding.imageViewRvCardRecipeFavourites, false)
                        listener.onBookmarksClick(
                            data[bindingAdapterPosition],
                            false,
                            bindingAdapterPosition
                        )
                    }
                }
            }

            binding.imageViewRvCardRecipeFork.setOnClickListener {
                when (binding.imageViewRvCardRecipeFork.tag as String) {
                    RecipeUtils.TAG_FORK_UNCHECKED -> {
                        toggleForks(binding.imageViewRvCardRecipeFork, true)
                        listener.onForkClicked(
                            data[bindingAdapterPosition],
                            true,
                            bindingAdapterPosition
                        )
                    }
                    RecipeUtils.TAG_FORK_CHECKED -> {
                        toggleForks(binding.imageViewRvCardRecipeFork, false)
                        listener.onForkClicked(
                            data[bindingAdapterPosition],
                            false,
                            bindingAdapterPosition
                        )
                    }
                }
            }
        }
    }

    private fun getCookingTime(time: LocalTime): String {
        return if (time.hour != 0) {
            String.format(
                context!!.getString(R.string.title_cooking_time_rv_recipe_h_m),
                time.hour,
                time.minute
            )
        } else {
            String.format(context!!.getString(R.string.title_cooking_time_rv_recipe_m), time.minute)
        }
    }

    private fun toggleForks(fork: ImageView, selected: Boolean) {
        if (selected) {
            fork.setImageResource(R.drawable.ic_fork_checked)
            fork.tag = RecipeUtils.TAG_FORK_CHECKED
        } else {
            fork.setImageResource(R.drawable.ic_fork_unchecked)
            fork.tag = RecipeUtils.TAG_FORK_UNCHECKED
        }
    }

    private fun toggleFavourites(favourite: ImageView, selected: Boolean) {
        if (selected) {
            favourite.setImageResource(R.drawable.ic_add_to_fav)
            favourite.tag = RecipeUtils.TAG_FAVOURITES_CHECKED
        } else {
            favourite.setImageResource(R.drawable.ic_not_fav)
            favourite.tag = RecipeUtils.TAG_FAVOURITES_UNCHECKED
        }
    }

}
