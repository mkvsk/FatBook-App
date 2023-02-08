package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.RecipeComment
import online.fatbook.fatbookapp.databinding.RvCommentRecipeViewBinding
import online.fatbook.fatbookapp.util.FormatUtils

class ViewRecipeCommentAdapter(private val context: Context) :
    RecyclerView.Adapter<ViewRecipeCommentAdapter.ViewHolder>(), BindableAdapter<RecipeComment> {
    private var _binding: RvCommentRecipeViewBinding? = null
    private val binding get() = _binding!!

    private var data: ArrayList<RecipeComment> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding =
            RvCommentRecipeViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<RecipeComment>?) {
        data?.let {
            this.data = it as ArrayList<RecipeComment>
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(comment: RecipeComment?) {
            Glide
                .with(context)
                .load(comment?.user?.profileImage)
                .placeholder(context.getDrawable(R.drawable.ic_default_userphoto))
                .into(binding.imageviewUserphotoRvComment)

            binding.textviewUsernameRvComment.text = comment?.user?.username.toString()

            //    TODO need fix
//            binding.textviewDateRvComment.text =
//                FormatUtils.getCreateDate(comment?.timestamp.toString())
            binding.textviewDateRvComment.text = comment?.timestamp.toString()
            binding.textviewUserCommentRvComment.text = comment?.comment.toString()
        }
    }
}