package online.fatbook.fatbookapp.ui.feed.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import lombok.extern.java.Log
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.util.BindableAdapter
import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.RvFeedRecipeCardPreviewBinding
import online.fatbook.fatbookapp.ui.base.OnRecipeClickListener
import online.fatbook.fatbookapp.util.FormatUtils
import online.fatbook.fatbookapp.util.RecipeUtils
import online.fatbook.fatbookapp.util.RecipeUtils.TAG_CLICK_FALSE
import online.fatbook.fatbookapp.util.RecipeUtils.TAG_FORK_CHECKED
import online.fatbook.fatbookapp.util.RecipeUtils.TAG_FORK_UNCHECKED
import org.apache.commons.lang3.StringUtils
import java.time.LocalTime

@Log
class RecipeAdapter(private val context: Context) :
    RecyclerView.Adapter<RecipeAdapter.RecipePreviewItemViewHolder>(),
    BindableAdapter<RecipeSimpleObject> {

    private var data: List<RecipeSimpleObject> = ArrayList()
    private var user: User = User()
    private lateinit var listener: OnRecipeClickListener

    private var recipeForked = false
    private var recipeInFav = false

    fun setUser(user: User) {
        this.user = user
    }

    fun setClickListener(listener: OnRecipeClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipePreviewItemViewHolder {
        val binding =
            RvFeedRecipeCardPreviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return RecipePreviewItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipePreviewItemViewHolder, position: Int) {
        val previewItem = data[position]
        holder.bind(previewItem)
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

    inner class RecipePreviewItemViewHolder(rvFeedRecipeCardPreviewBinding: RvFeedRecipeCardPreviewBinding) :
        RecyclerView.ViewHolder(rvFeedRecipeCardPreviewBinding.root) {
        private val binding = rvFeedRecipeCardPreviewBinding
        fun bind(recipe: RecipeSimpleObject) {
            if (recipe.isPrivate == true) {
                binding.imageViewRvRecipePrivate.visibility = View.VISIBLE
                binding.viewClickComments.visibility = View.GONE
                binding.viewClickFork.visibility = View.GONE
                binding.textViewRvCardRecipeForksAvg.visibility = View.GONE
                binding.imageViewRvCardRecipeFork.visibility = View.GONE
                binding.imageViewRvCardRecipeComments.visibility = View.GONE
                binding.rvRecipeCommentsQty.visibility = View.GONE
            } else {
                binding.imageViewRvRecipePrivate.visibility = View.GONE
                binding.viewClickComments.visibility = View.VISIBLE
                binding.viewClickFork.visibility = View.VISIBLE
                binding.textViewRvCardRecipeForksAvg.visibility = View.VISIBLE
                binding.imageViewRvCardRecipeFork.visibility = View.VISIBLE
                binding.imageViewRvCardRecipeComments.visibility = View.VISIBLE
                binding.rvRecipeCommentsQty.visibility = View.VISIBLE
            }

            binding.viewClickFork.tag = TAG_CLICK_FALSE
            if (StringUtils.isNotEmpty(recipe.image)) {
                Glide.with(itemView.context).load(recipe.image)
                    .into(binding.imageViewRvCardRecipePhoto)
            } else {
                binding.imageViewRvCardRecipePhoto.setImageResource(R.drawable.default_recipe_image_rv_feed)
            }

            binding.textViewRvCardRecipeTitle.text = recipe.title
            if (recipe.ingredientsLocalizedMap.isNotEmpty()) {
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
                context!!.getString(R.string.format_kcal), recipe.kcalPerPortion.toString()
            )
            binding.rvRecipeCookingTime.text =
                getCookingTime(LocalTime.parse(recipe.cookingTime))

            binding.rvRecipeDifficulty.text = recipe.difficulty?.title

            binding.rvRecipeCreateDate.text = FormatUtils.getCreateDate(recipe.createDate!!)

            binding.rvRecipeAuthor.text = recipe.user!!.username
            Glide.with(itemView.context).load(recipe.user!!.profileImage)
                .placeholder(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_default_userphoto
                    )
                )
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

//            TODO fav
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
                            data[bindingAdapterPosition], true, bindingAdapterPosition
                        )
                    }
                    RecipeUtils.TAG_FAVOURITES_CHECKED -> {
                        toggleFavourites(binding.imageViewRvCardRecipeFavourites, false)
                        listener.onBookmarksClick(
                            data[bindingAdapterPosition], false, bindingAdapterPosition
                        )
                    }
                }
            }
            binding.viewClickFork.setOnClickListener {
                if (binding.viewClickFork.tag as String == TAG_CLICK_FALSE) {
//                    binding.viewClickFork.tag = TAG_CLICK_TRUE
                    if (binding.imageViewRvCardRecipeFork.tag as String == TAG_FORK_CHECKED) {
                        listener.onForkClicked(
                            data[bindingAdapterPosition], false, bindingAdapterPosition, this
                        )
                        toggleForks(binding.imageViewRvCardRecipeFork, false)
                        recipeForked = false
                    } else {
                        listener.onForkClicked(
                            data[bindingAdapterPosition], true, bindingAdapterPosition, this
                        )
                        toggleForks(binding.imageViewRvCardRecipeFork, true)
                        recipeForked = true
                    }
                }
            }
            binding.viewClickComments.setOnClickListener {}
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

    public fun toggleForks(fork: ImageView, selected: Boolean) {
        if (selected) {
            fork.setImageResource(R.drawable.ic_fork_checked)
            fork.tag = TAG_FORK_CHECKED
        } else {
            fork.setImageResource(R.drawable.ic_fork_unchecked)
            fork.tag = TAG_FORK_UNCHECKED
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