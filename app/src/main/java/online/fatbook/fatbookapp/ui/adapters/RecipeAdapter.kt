package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.rv_feed_recipe_card_preview.view.*
import lombok.extern.java.Log
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.RecipeSimpleObject
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener
import online.fatbook.fatbookapp.util.FormatUtils
import online.fatbook.fatbookapp.util.RecipeUtils
import online.fatbook.fatbookapp.util.RecipeUtils.TAG_CLICK_FALSE
import online.fatbook.fatbookapp.util.RecipeUtils.TAG_CLICK_TRUE
import online.fatbook.fatbookapp.util.RecipeUtils.TAG_FORK_CHECKED
import online.fatbook.fatbookapp.util.RecipeUtils.TAG_FORK_UNCHECKED
import org.apache.commons.lang3.StringUtils
import java.time.LocalTime

@Log
class RecipeAdapter :
    RecyclerView.Adapter<RecipeAdapter.ViewHolder>(), BindableAdapter<RecipeSimpleObject> {

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
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_feed_recipe_card_preview, parent, false)
        )
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
            recipeForked = false
            recipeInFav = false
            itemView.view_click_fork.tag = TAG_CLICK_FALSE
            if (StringUtils.isNotEmpty(recipe.image)) {
                Glide
                    .with(itemView.context)
                    .load(recipe.image)
                    .into(itemView.imageView_rv_card_recipe_photo)
            } else {
                itemView.imageView_rv_card_recipe_photo.setImageResource(R.drawable.default_recipe_image_rv_feed)
            }

            itemView.textView_rv_card_recipe_title.text = recipe.title
            if (recipe.ingredientsLocalizedMap.isNotEmpty()) {
                itemView.rv_ingredients_preview.text = String.format(
                    context!!.getString(R.string.title_ingredients_rv_recipe),
                    recipe.ingredientQty,
                    recipe.ingredientsStr
                )
            }
            if (recipe.kcalPerPortion == 0.0) {
                itemView.rv_recipe_kcal_img.visibility = View.GONE
                itemView.rv_recipe_kcal.visibility = View.GONE
            } else {
                itemView.rv_recipe_kcal_img.visibility = View.VISIBLE
                itemView.rv_recipe_kcal.visibility = View.VISIBLE
            }
            itemView.rv_recipe_kcal.text = String.format(
                context!!.getString(R.string.format_kcal),
                recipe.kcalPerPortion.toString()
            )
            itemView.rv_recipe_cooking_time.text =
                getCookingTime(LocalTime.parse(recipe.cookingTime))

            itemView.rv_recipe_difficulty.text = recipe.difficulty?.title

            itemView.rv_recipe_create_date.text = FormatUtils.getCreateDate(recipe.createDate!!)

            itemView.rv_recipe_author.text = recipe.user!!.username
            Glide
                .with(itemView.context)
                .load(recipe.user!!.profileImage)
                .placeholder(itemView.context.getDrawable(R.drawable.ic_default_userphoto))
                .into(itemView.imageview_author_photo_rv_recipe_preview)
            itemView.rv_recipe_comments_qty.text = recipe.commentQty.toString()
            itemView.textView_rv_card_recipe_forks_avg.text =
                FormatUtils.prettyCount(recipe.forks!!)

            itemView.ll_author_link_rv_recipe_preview.setOnClickListener {
                listener.onUsernameClick(data[bindingAdapterPosition].user!!.username!!)
            }

            user.recipesForked?.forEach {
                recipeForked = (it.identifier?.equals(recipe.identifier) == true)
            }
            toggleForks(itemView.imageView_rv_card_recipe_fork, recipeForked)

            user.recipesFavourites?.forEach {
                recipeInFav = (it.identifier?.equals(recipe.identifier) == true)
            }
            if (recipe.user?.username == user.username) {
                itemView.imageView_rv_card_recipe_favourites.visibility = View.INVISIBLE
            } else {
                itemView.imageView_rv_card_recipe_favourites.visibility = View.VISIBLE
                toggleFavourites(itemView.imageView_rv_card_recipe_favourites, recipeInFav)
            }

            itemView.rv_card_recipe_preview.setOnClickListener {
                listener.onRecipeClick(recipe.identifier!!)
            }

            itemView.imageView_rv_card_recipe_favourites.setOnClickListener {
                when (itemView.imageView_rv_card_recipe_favourites.tag as String) {
                    RecipeUtils.TAG_FAVOURITES_UNCHECKED -> {
                        toggleFavourites(itemView.imageView_rv_card_recipe_favourites, true)
                        listener.onBookmarksClick(
                            data[bindingAdapterPosition],
                            true,
                            bindingAdapterPosition
                        )
                    }
                    RecipeUtils.TAG_FAVOURITES_CHECKED -> {
                        toggleFavourites(itemView.imageView_rv_card_recipe_favourites, false)
                        listener.onBookmarksClick(
                            data[bindingAdapterPosition],
                            false,
                            bindingAdapterPosition
                        )
                    }
                }
            }
            itemView.view_click_fork.setOnClickListener {
                if (itemView.view_click_fork.tag as String == TAG_CLICK_FALSE) {
                    itemView.view_click_fork.tag = TAG_CLICK_TRUE
                    if (itemView.imageView_rv_card_recipe_fork.tag as String == TAG_FORK_CHECKED) {
                        listener.onForkClicked(
                            data[bindingAdapterPosition],
                            false,
                            bindingAdapterPosition, this
                        )
                    } else {
                        listener.onForkClicked(
                            data[bindingAdapterPosition],
                            true,
                            bindingAdapterPosition, this
                        )
                    }
                }
            }
            itemView.view_click_comments.setOnClickListener {
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