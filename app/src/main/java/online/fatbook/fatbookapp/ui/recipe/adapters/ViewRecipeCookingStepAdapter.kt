package online.fatbook.fatbookapp.ui.recipe.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.rv_cooking_step_preview.view.*
import kotlinx.android.synthetic.main.rv_cooking_step_recipe_view.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.CookingStep
import online.fatbook.fatbookapp.ui.recipe.listeners.OnRecipeStepImageClickListener
import online.fatbook.fatbookapp.ui.recipe.listeners.OnCookingStepClickListener
import online.fatbook.fatbookapp.util.BindableAdapter
import online.fatbook.fatbookapp.util.Utils

class ViewRecipeCookingStepAdapter(private val context: Context) :
    RecyclerView.Adapter<ViewRecipeCookingStepAdapter.ViewHolder>(), BindableAdapter<CookingStep> {

    private var data: ArrayList<CookingStep> = ArrayList()

    var listener: OnCookingStepClickListener? = null

    private lateinit var imageViewListener: OnRecipeStepImageClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_cooking_step_recipe_view, parent, false)
        )
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

    fun setImageListener(imageViewListener: OnRecipeStepImageClickListener) {
        this.imageViewListener = imageViewListener
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: CookingStep?) {
            itemView.textview_description_rv_cooking_step_recipe_view.text = value?.description

            itemView.rv_cooking_step_number_recipe_view.setBackgroundResource(
                Utils.getCookingStepNumeralIcon(
                    value?.stepNumber!!
                )
            )

            if (!value.image.isNullOrEmpty()) {
                Glide.with(context)
                    .load(value.image)
                    .into(itemView.imageview_photo_rv_cooking_step_recipe_view)
            } else {
                itemView.imageview_photo_rv_cooking_step_recipe_view.visibility = View.GONE
//                itemView.imageview_photo_rv_cooking_step_recipe_view.setImageResource(R.drawable.default_cooking_step_image)
            }


            itemView.imageview_photo_rv_cooking_step_recipe_view.setOnClickListener {
                val img = value.image
                if (img != null) {
                    imageViewListener.onStepImageClick(img)
                }
            }
        }
    }
}
