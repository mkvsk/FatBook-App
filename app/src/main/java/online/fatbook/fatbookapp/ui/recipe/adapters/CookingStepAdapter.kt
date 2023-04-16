package online.fatbook.fatbookapp.ui.recipe.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.CookingStep
import online.fatbook.fatbookapp.databinding.RvCookingStepPreviewBinding
import online.fatbook.fatbookapp.ui.recipe.listeners.OnCookingStepClickListener
import online.fatbook.fatbookapp.util.BindableAdapter
import java.io.File

class CookingStepAdapter(private val context: Context) :
    RecyclerView.Adapter<CookingStepAdapter.CookingStepItemViewHolder>(),
    BindableAdapter<CookingStep> {

    private var data: ArrayList<CookingStep> = ArrayList()
    private var images: HashMap<Int, File?> = HashMap()
    var listener: OnCookingStepClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CookingStepItemViewHolder {
        val binding =
            RvCookingStepPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    @SuppressLint("NotifyDataSetChanged")
    fun setImages(map: HashMap<Int, File?>?) {
        map?.let {
            images = map
            notifyDataSetChanged()
        }
    }

    fun setClickListener(listener: OnCookingStepClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class CookingStepItemViewHolder(rvCookingStepPreviewBinding: RvCookingStepPreviewBinding) :
        RecyclerView.ViewHolder(rvCookingStepPreviewBinding.root) {
        private val binding = rvCookingStepPreviewBinding
        fun bind(cookingStepItem: CookingStep?) {

            binding.textviewDescriptionRvCookingStep.text = cookingStepItem!!.description
            Glide
                .with(context)
                .load(cookingStepItem.imageFile ?: cookingStepItem.image)
                .placeholder(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.default_recipe_image_recipe_create_second_stage
                    )
                )
                .into(binding.imageviewPhotoRvCookingStep)

            binding.buttonRemoveRvCookingStep.setOnClickListener {
                listener!!.onRecipeCookingStepDelete(bindingAdapterPosition)
            }

            //TODO add edit step
            binding.cardviewCookingStep.isClickable = false
//            itemView.cardview_cooking_step.setOnClickListener {
//                listener!!.onCookingStepClick(value, bindingAdapterPosition)
//            }
        }
    }
}
