package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.CookingStep
import online.fatbook.fatbookapp.databinding.RvCookingStepPreviewBinding
import online.fatbook.fatbookapp.ui.listeners.OnCookingStepClickListener

class CookingStepAdapter(private val context: Context) :
    RecyclerView.Adapter<CookingStepAdapter.ViewHolder>(), BindableAdapter<CookingStep> {
    private var _binding: RvCookingStepPreviewBinding? = null
    private val binding get() = _binding!!

    private var data: ArrayList<CookingStep> = ArrayList()
    var listener: OnCookingStepClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding =
            RvCookingStepPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

            binding.textviewDescriptionRvCookingStep.text = value!!.description
            Glide
                .with(context)
                .load(value.imageFile ?: value.image)
                .placeholder(context.getDrawable(R.drawable.default_recipe_image_recipe_create_second_stage))
                .into(binding.imageviewPhotoRvCookingStep)

            binding.buttonRemoveRvCookingStep.setOnClickListener {
                listener!!.onRecipeCookingStepDelete(bindingAdapterPosition)
            }

            //TODO add edit step
            binding.cardviewCookingStep.isClickable = false
//            binding.cardviewCookingStep.setOnClickListener {
//                listener!!.onCookingStepClick(value, bindingAdapterPosition)
//            }
        }
    }
}
