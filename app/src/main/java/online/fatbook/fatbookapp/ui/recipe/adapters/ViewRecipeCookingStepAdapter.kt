package online.fatbook.fatbookapp.ui.recipe.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import online.fatbook.fatbookapp.core.recipe.CookingStep
import online.fatbook.fatbookapp.databinding.RvCookingStepRecipeViewBinding
import online.fatbook.fatbookapp.ui.recipe.listeners.OnRecipeStepImageClickListener
import online.fatbook.fatbookapp.ui.recipe.listeners.OnCookingStepClickListener
import online.fatbook.fatbookapp.util.BindableAdapter
import online.fatbook.fatbookapp.util.Utils

class ViewRecipeCookingStepAdapter(private val context: Context) :
    RecyclerView.Adapter<ViewRecipeCookingStepAdapter.CookingStepItemViewHolder>(),
    BindableAdapter<CookingStep> {

    private var data: ArrayList<CookingStep> = ArrayList()
    var listener: OnCookingStepClickListener? = null

    private lateinit var imageViewListener: OnRecipeStepImageClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CookingStepItemViewHolder {
        val binding =
            RvCookingStepRecipeViewBinding.inflate(LayoutInflater.from(context), parent, false)
        return CookingStepItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CookingStepItemViewHolder, position: Int) {
        val cookingStepItem = data[position]
        holder.bind(cookingStepItem)
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

    fun setImageListener(imageViewListener: OnRecipeStepImageClickListener) {
        this.imageViewListener = imageViewListener
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class CookingStepItemViewHolder(rvCookingStepRecipeViewBinding: RvCookingStepRecipeViewBinding) :
        RecyclerView.ViewHolder(rvCookingStepRecipeViewBinding.root) {
        private val binding = rvCookingStepRecipeViewBinding
        fun bind(cookingStepItem: CookingStep?) {
            binding.textviewDescriptionRvCookingStepRecipeView.text = cookingStepItem?.description

            binding.rvCookingStepNumberRecipeView.setBackgroundResource(
                Utils.getCookingStepNumeralIcon(
                    cookingStepItem?.stepNumber!!
                )
            )

            if (!cookingStepItem.image.isNullOrEmpty()) {
                Glide.with(context)
                    .load(cookingStepItem.image)
                    .into(binding.imageviewPhotoRvCookingStepRecipeView)
            } else {
                binding.imageviewPhotoRvCookingStepRecipeView.visibility = View.GONE
//                binding.imageviewPhotoRvCookingStepRecipeView.setImageResource(R.drawable.default_cooking_step_image)
            }


            binding.imageviewPhotoRvCookingStepRecipeView.setOnClickListener {
                val img = cookingStepItem.image
                if (img != null) {
                    imageViewListener.onStepImageClick(img)
                }
            }
        }
    }
}
