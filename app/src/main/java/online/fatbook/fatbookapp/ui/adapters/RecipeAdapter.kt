package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
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
import org.apache.commons.lang3.StringUtils

@Log
class RecipeAdapter :
    RecyclerView.Adapter<RecipeAdapter.ViewHolder>(), BindableAdapter<RecipeSimpleObject> {

    private var data: List<RecipeSimpleObject> = ArrayList()
    private var user: User = User()
    private var listener: OnRecipeClickListener? = null


    fun setUser(user: User) {
        this.user = user
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

            itemView.textView_rv_card_recipe_title.text = recipe.title
            itemView.textView_rv_card_recipe_author.text = recipe.author
            val forksAmount = FormatUtils.prettyCount(recipe.forks!!)
            itemView.textView_rv_card_recipe_forks_avg.text = forksAmount
            toggleForks(
                itemView.imageView_rv_card_recipe_fork,
                user.recipesForked!!.contains(recipe.identifier)
            )
            if (recipe.author == user.username) {
                itemView.imageView_rv_card_recipe_favourites.visibility = View.INVISIBLE
            } else {
                itemView.imageView_rv_card_recipe_favourites.visibility = View.VISIBLE
                toggleFavourites(
                    itemView.imageView_rv_card_recipe_favourites,
                    user.recipesFavourites!!.contains(recipe.identifier)
                )
            }
            if (StringUtils.isNotEmpty(recipe.image)) {
                Glide
                    .with(itemView.context)
                    .load(recipe.image)
                    .into(itemView.imageView_rv_card_recipe_photo)
            } else {
                itemView.imageView_rv_card_recipe_photo.setImageResource(R.drawable.default_recipe_image_rv_feed)
            }

            itemView.rv_card_recipe_preview.setOnClickListener {
                listener!!.onRecipeClick(adapterPosition)
            }
            itemView.imageView_rv_card_recipe_favourites.setOnClickListener {
                val tag = itemView.imageView_rv_card_recipe_favourites.tag as String
                when (tag) {
                    RecipeUtils.TAG_BOOKMARKS_UNCHECKED -> {
                        toggleFavourites(itemView.imageView_rv_card_recipe_favourites, true)
                        listener!!.onBookmarksClick(data[adapterPosition], true, adapterPosition)
                    }
                    RecipeUtils.TAG_BOOKMARKS_CHECKED -> {
                        toggleFavourites(itemView.imageView_rv_card_recipe_favourites, false)
                        listener!!.onBookmarksClick(data[adapterPosition], false, adapterPosition)
                    }
                }
            }
            itemView.imageView_rv_card_recipe_fork.setOnClickListener {
                when (itemView.imageView_rv_card_recipe_fork.tag as String) {
                    RecipeUtils.TAG_FORK_UNCHECKED -> {
                        toggleForks(itemView.imageView_rv_card_recipe_fork, true)
                        listener!!.onForkClicked(data[adapterPosition], true, adapterPosition)
                    }
                    RecipeUtils.TAG_FORK_CHECKED -> {
                        toggleForks(itemView.imageView_rv_card_recipe_fork, false)
                        listener!!.onForkClicked(data[adapterPosition], false, adapterPosition)
                    }
                }
            }
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
            favourite.tag = RecipeUtils.TAG_BOOKMARKS_CHECKED
        } else {
            favourite.setImageResource(R.drawable.ic_not_fav)
            favourite.tag = RecipeUtils.TAG_BOOKMARKS_UNCHECKED
        }
    }

}
