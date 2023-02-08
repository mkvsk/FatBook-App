package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import online.fatbook.fatbookapp.core.recipe.CookingStep
import online.fatbook.fatbookapp.databinding.RvCookingStepRecipeViewBinding
import online.fatbook.fatbookapp.ui.listeners.OnCookingStepClickListener
import online.fatbook.fatbookapp.util.Utils

class ViewRecipeCookingStepAdapter(private val context: Context) :
    RecyclerView.Adapter<ViewRecipeCookingStepAdapter.ViewHolder>(), BindableAdapter<CookingStep> {
    private var _binding: RvCookingStepRecipeViewBinding? = null
    private val binding get() = _binding!!


    private var data: ArrayList<CookingStep> = ArrayList()

    var listener: OnCookingStepClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding =
            RvCookingStepRecipeViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<CookingStep>?) {
        data?.let {
            this.data = it as ArrayList<CookingStep>
            notifyDataSetChanged()
        }
    }

    fun setClickListener(listener: OnCookingStepClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: CookingStep?) {
            binding.textviewDescriptionRvCookingStepRecipeView.text = value?.description

            binding.rvCookingStepNumberRecipeView.setBackgroundResource(
                Utils.getCookingStepNumeralIcon(
                    value?.stepNumber!!
                )
            )

            if (!value.image.isNullOrEmpty()) {
                Glide.with(context)
                    .load(value.image)
                    .into(binding.imageviewPhotoRvCookingStepRecipeView)
            } else {
                binding.imageviewPhotoRvCookingStepRecipeView.visibility = View.GONE
//                binding.imageviewPhotoRvCookingStepRecipeView.setImageResource(R.drawable.default_cooking_step_image)
            }
        }
    }
}
