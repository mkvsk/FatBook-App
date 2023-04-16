package online.fatbook.fatbookapp.ui.recipe.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.RecipeComment
import online.fatbook.fatbookapp.databinding.RvCommentRecipeViewBinding
import online.fatbook.fatbookapp.util.BindableAdapter

class ViewRecipeCommentAdapter(
    private val context: Context
) : RecyclerView.Adapter<ViewRecipeCommentAdapter.CommentItemViewHolder>(),
    BindableAdapter<RecipeComment> {

    private var commentItemList: List<RecipeComment> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentItemViewHolder {
        val binding =
            RvCommentRecipeViewBinding.inflate(LayoutInflater.from(context), parent, false)
        return CommentItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentItemViewHolder, position: Int) {
        val comment = commentItemList[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int {
        return commentItemList.size
    }

    inner class CommentItemViewHolder(rvCommentBinding: RvCommentRecipeViewBinding) :
        RecyclerView.ViewHolder(rvCommentBinding.root) {
        private val binding = rvCommentBinding
        fun bind(comment: RecipeComment?) {
            Glide
                .with(context)
                .load(comment?.user?.profileImage)
                .placeholder(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_default_userphoto
                    )
                )
                .into(binding.imageviewUserphotoRvComment)

            binding.textviewUsernameRvComment.text = comment?.user?.username.toString()

            //    TODO need fix
//            binding.textviewDateRvComment.text =
//                FormatUtils.getCreateDate(comment?.timestamp.toString())
            binding.textviewDateRvComment.text = comment?.timestamp.toString()
            binding.textviewUserCommentRvComment.text = comment?.comment.toString()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<RecipeComment>?) {
        commentItemList = data!!
        notifyDataSetChanged()
    }
}