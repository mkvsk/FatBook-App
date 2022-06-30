package online.fatbook.fatbookapp.ui.adapters

import androidx.lifecycle.ViewModelProvider.get
import androidx.navigation.NavController.navigate
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.NavController.popBackStack
import androidx.appcompat.app.AppCompatActivity
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.ui.viewmodel.IngredientViewModel
import androidx.navigation.NavController
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import online.fatbook.fatbookapp.util.UserUtils
import online.fatbook.fatbookapp.R
import com.google.android.material.navigation.NavigationBarView
import online.fatbook.fatbookapp.ui.viewmodel.SignInViewModel
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.content.ContextCompat
import online.fatbook.fatbookapp.ui.activity.PasswordActivity
import android.text.TextWatcher
import android.text.Editable
import androidx.appcompat.content.res.AppCompatResources
import online.fatbook.fatbookapp.ui.activity.LoginActivity
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import online.fatbook.fatbookapp.ui.activity.MainActivity
import online.fatbook.fatbookapp.ui.activity.SignInActivity
import online.fatbook.fatbookapp.ui.activity.WelcomeActivity
import online.fatbook.fatbookapp.ui.activity.SplashActivity
import androidx.activity.result.ActivityResultLauncher
import online.fatbook.fatbookapp.ui.activity.fill_additional_info.FillAdditionalInfoViewModel
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.ActivityResultCallback
import online.fatbook.fatbookapp.ui.activity.fill_additional_info.FillAdditionalInfoActivity
import android.app.Activity
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import online.fatbook.fatbookapp.ui.activity.SkipAdditionalInfoActivity
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import online.fatbook.fatbookapp.util.RecipeUtils
import online.fatbook.fatbookapp.ui.listeners.OnRecipeViewDeleteIngredient
import online.fatbook.fatbookapp.ui.listeners.OnAddIngredientItemClickListener
import android.annotation.SuppressLint
import android.content.*
import androidx.cardview.widget.CardView
import online.fatbook.fatbookapp.ui.listeners.OnRecipeRevertDeleteListener
import online.fatbook.fatbookapp.ui.adapters.RecipeAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import androidx.recyclerview.widget.SimpleItemAnimator
import online.fatbook.fatbookapp.ui.fragment.FeedFragment
import androidx.navigation.fragment.NavHostFragment
import online.fatbook.fatbookapp.util.KeyboardActionUtil
import online.fatbook.fatbookapp.ui.fragment.BookmarksFragment
import online.fatbook.fatbookapp.ui.adapters.ViewRecipeIngredientAdapter
import online.fatbook.fatbookapp.ui.fragment.RecipeViewFragment
import okhttp3.RequestBody
import okhttp3.MultipartBody
import online.fatbook.fatbookapp.ui.adapters.IngredientsAdapter
import online.fatbook.fatbookapp.ui.fragment.IngredientsFragment
import android.content.DialogInterface.OnShowListener
import com.google.android.material.appbar.AppBarLayout
import android.graphics.PorterDuff
import androidx.fragment.app.FragmentActivity
import online.fatbook.fatbookapp.ui.fragment.UserProfileFragment
import online.fatbook.fatbookapp.ui.fragment.RecipeCreateFragment
import online.fatbook.fatbookapp.ui.adapters.AddIngredientToRecipeAdapter
import online.fatbook.fatbookapp.ui.fragment.RecipeAddIngredientFragment
import androidx.lifecycle.ViewModel
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.os.Environment
import android.text.TextUtils
import android.provider.OpenableColumns
import android.view.*
import android.widget.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import lombok.extern.java.Log
import online.fatbook.fatbookapp.core.*
import online.fatbook.fatbookapp.retrofit.NetworkInfoService
import org.apache.commons.lang3.StringUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Multipart

@Log
class RecipeAdapter(
    context: Context?,
    list: List<Recipe?>?,
    user: User?,
    listener: OnRecipeClickListener
) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {
    private val inflater: LayoutInflater
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
        holder.tvTitle.text = recipe.getName()
        holder.tvAuthor.text = recipe.getAuthor()
        val forksAmount = Integer.toString(recipe.getForks())
        holder.tvForks.text = forksAmount
        toggleForks(holder.fork, user.getRecipesForked().contains(recipe.getIdentifier()))
        if (recipe.getAuthor() == user.getLogin()) {
            holder.bookmarks.visibility = View.INVISIBLE
        } else {
            holder.bookmarks.visibility = View.VISIBLE
            toggleBookmarks(
                holder.bookmarks,
                user.getRecipesBookmarked().contains(recipe.getIdentifier())
            )
        }
        if (StringUtils.isNotEmpty(recipe.getImage())) {
            Glide
                .with(inflater.context)
                .load(recipe.getImage())
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
        inflater = LayoutInflater.from(context)
        this.list = list
        this.user = user
        this.listener = listener
    }
}