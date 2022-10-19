package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_cooking_step_preview.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.CookingStep
import online.fatbook.fatbookapp.ui.listeners.OnCookingStepClickListener

class CookingStepAdapter :
    RecyclerView.Adapter<CookingStepAdapter.ViewHolder>(), BindableAdapter<CookingStep> {

    private var data: ArrayList<CookingStep> = ArrayList()
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

    override fun getItemCount(): Int {
        return data.size
    }

    fun setClickListener(listener: OnCookingStepClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: CookingStep?) {
            itemView.textview_description_rv_cooking_step.text = value!!.description.toString()

//            if (selectedItems!!.contains(bindingAdapterPosition)) {
//                selectItem(itemView.cardview_rv_recipe_methods_categories_items)
//            } else {
//                unselectItem(itemView.cardview_rv_recipe_methods_categories_items)
//            }
//
//            itemView.textview_rv_recipe_methods_categories_items.text = value!!.title
//
//            itemView.cardview_rv_recipe_methods_categories_items.setOnClickListener {
//                if (value is CookingMethod) {
//                    listener?.onItemClick(data[bindingAdapterPosition])
//                } else {
//                    if (!itemView.textview_rv_recipe_methods_categories_items.isSelected) {
//                        selectItem(itemView.cardview_rv_recipe_methods_categories_items)
//                        itemView.textview_rv_recipe_methods_categories_items.isSelected = true
//                    } else {
//                        unselectItem(itemView.cardview_rv_recipe_methods_categories_items)
//                        itemView.textview_rv_recipe_methods_categories_items.isSelected = false
//                    }
//                    listener?.onItemClickChoose(data[bindingAdapterPosition])
//                }
//            }
        }
    }
}
