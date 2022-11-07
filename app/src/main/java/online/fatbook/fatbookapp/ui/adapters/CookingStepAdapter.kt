package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.rv_cooking_step_preview.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.CookingStep
import online.fatbook.fatbookapp.ui.listeners.OnCookingStepClickListener
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import java.io.File

class CookingStepAdapter(private val context: Context) :
    RecyclerView.Adapter<CookingStepAdapter.ViewHolder>(), BindableAdapter<CookingStep> {

    private var data: ArrayList<CookingStep> = ArrayList()
    private var images: HashMap<Int, File> = HashMap()
    var listener: OnCookingStepClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_cooking_step_preview, parent, false)
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

    fun setImages(map: HashMap<Int, File>?) {
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: CookingStep?) {

            itemView.textview_description_rv_cooking_step.text = value!!.description
            images[value.stepNumber]?.let {
                Glide.with(context).load(it).into(itemView.imageview_photo_rv_cooking_step)
            }

            itemView.button_remove_rv_cooking_step.setOnClickListener {
                listener!!.onRecipeCookingStepDelete(bindingAdapterPosition)

                Log.d("REMOVE STEP:", "remover step number= ${value.stepNumber}")
            }

            //TODO add edit step
            itemView.cardview_cooking_step.isClickable = false
//            itemView.cardview_cooking_step.setOnClickListener {
//                listener!!.onCookingStepClick(value, bindingAdapterPosition)
//            }
        }
    }
}
