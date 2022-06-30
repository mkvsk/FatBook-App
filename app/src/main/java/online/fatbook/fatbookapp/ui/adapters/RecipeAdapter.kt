package online.fatbook.fatbookapp.ui.adapters

import android.content.*
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import lombok.extern.java.Log
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.*
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener
import online.fatbook.fatbookapp.util.RecipeUtils
import org.apache.commons.lang3.StringUtils

@Log
class RecipeAdapter(
    context: Context?,
    list: List<Recipe?>?,
    user: User?,
    listener: OnRecipeClickListener
) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var list: List<Recipe?>?
    private var user: User?
    private val listener: OnRecipeClickListener
    fun setData(list: List<Recipe?>?, user: User?) {
        this.list = list
        this.user = user
    }

    fun setUser(user: User?) {
        this.user = user
    }

    fun updateList(list: List<Recipe?>?) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.rv_feed_recipe_card_preview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = list!![position]
        holder.tvTitle.text = recipe!!.name
        holder.tvAuthor.text = recipe.author
        val forksAmount = recipe.forks.toString()
        holder.tvForks.text = forksAmount
        toggleForks(holder.fork, user!!.recipesForked!!.contains(recipe.identifier))
        if (recipe.author == user!!.login) {
            holder.bookmarks.visibility = View.INVISIBLE
        } else {
            holder.bookmarks.visibility = View.VISIBLE
            toggleBookmarks(
                holder.bookmarks,
                user!!.recipesBookmarked!!.contains(recipe.identifier)
            )
        }
        if (StringUtils.isNotEmpty(recipe.image)) {
            Glide
                .with(inflater.context)
                .load(recipe.image)
                .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.image_recipe_default)
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    private fun toggleForks(fork: ImageView, selected: Boolean) {
        if (selected) {
            fork.setImageResource(R.drawable.icon_fork_checked)
            fork.tag = RecipeUtils.TAG_FORK_CHECKED
        } else {
            fork.setImageResource(R.drawable.icon_fork_unchecked)
            fork.tag = RecipeUtils.TAG_FORK_UNCHECKED
        }
    }

    private fun toggleBookmarks(bookmark: ImageView, selected: Boolean) {
        if (selected) {
            bookmark.setImageResource(R.drawable.icon_bookmarks_checked)
            bookmark.tag = RecipeUtils.TAG_BOOKMARKS_CHECKED
        } else {
            bookmark.setImageResource(R.drawable.icon_bookmarks_unchecked)
            bookmark.tag = RecipeUtils.TAG_BOOKMARKS_UNCHECKED
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView
        val tvAuthor: TextView
        val tvForks: TextView
        val image: ImageView
        val bookmarks: ImageView
        val fork: ImageView
        val btnRecipe: ImageButton

        init {
            tvTitle = itemView.findViewById(R.id.textView_rv_card_recipe_title)
            tvAuthor = itemView.findViewById(R.id.textView_rv_card_recipe_author)
            tvForks = itemView.findViewById(R.id.textView_rv_card_recipe_forks_avg)
            image = itemView.findViewById(R.id.imageView_rv_card_recipe_photo)
            bookmarks = itemView.findViewById(R.id.imageView_rv_card_recipe_bookmarks)
            fork = itemView.findViewById(R.id.imageView_rv_card_recipe_fork)
            btnRecipe = itemView.findViewById(R.id.rv_card_recipe_bgr)
            btnRecipe.setOnClickListener { view: View? ->
                listener.onRecipeClick(
                    adapterPosition
                )
            }
            bookmarks.setOnClickListener { _view: View? ->
                val tag = bookmarks.tag as String
                when (tag) {
                    RecipeUtils.TAG_BOOKMARKS_UNCHECKED -> {
                        toggleBookmarks(bookmarks, true)
                        listener.onBookmarksClick(list!![adapterPosition], true, adapterPosition)
                    }
                    RecipeUtils.TAG_BOOKMARKS_CHECKED -> {
                        toggleBookmarks(bookmarks, false)
                        listener.onBookmarksClick(list!![adapterPosition], false, adapterPosition)
                    }
                }
            }
            fork.setOnClickListener { _view: View? ->
                val tag = fork.tag as String
                when (tag) {
                    RecipeUtils.TAG_FORK_UNCHECKED -> {
                        toggleForks(fork, true)
                        listener.onForkClicked(list!![adapterPosition], true, adapterPosition)
                    }
                    RecipeUtils.TAG_FORK_CHECKED -> {
                        toggleForks(fork, false)
                        listener.onForkClicked(list!![adapterPosition], false, adapterPosition)
                    }
                }
            }
        }
    }

    init {
        this.list = list
        this.user = user
        this.listener = listener
    }
}