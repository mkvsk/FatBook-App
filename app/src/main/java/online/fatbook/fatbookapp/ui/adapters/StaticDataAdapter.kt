package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.rv_difficulty.view.*
import kotlinx.android.synthetic.main.rv_recipe_methods_categories_items.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.core.recipe.StaticDataObject
import online.fatbook.fatbookapp.ui.listeners.OnStaticDataClickListener

class StaticDataAdapter :
    RecyclerView.Adapter<StaticDataAdapter.ViewHolder>(), BindableAdapter<StaticDataObject> {

    private var data: List<StaticDataObject> = ArrayList()
    var listener: OnStaticDataClickListener? = null
    var selectedItems: List<Int>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_recipe_methods_categories_items, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<StaticDataObject>?) {
        data?.let {
            this.data = it as ArrayList<StaticDataObject>
            notifyDataSetChanged()
        }
    }

    fun setClickListener(listener: OnStaticDataClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setSelected(arrayList: List<Int>) {
        selectedItems = arrayList
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: StaticDataObject?) {
            if (selectedItems!!.contains(bindingAdapterPosition)) {
                selectItem(itemView.cardview_rv_recipe_methods_categories_items)
            } else {
                unselectItem(itemView.cardview_rv_recipe_methods_categories_items)
            }

            itemView.textview_rv_recipe_methods_categories_items.text = value!!.title

            itemView.cardview_rv_recipe_methods_categories_items.setOnClickListener {
                if (value is CookingMethod) {
                    listener?.onItemClick(data[bindingAdapterPosition])
                } else {
                    if (!itemView.textview_rv_recipe_methods_categories_items.isSelected) {
                        selectItem(itemView.cardview_rv_recipe_methods_categories_items)
                        itemView.textview_rv_recipe_methods_categories_items.isSelected = true
                    } else {
                        unselectItem(itemView.cardview_rv_recipe_methods_categories_items)
                        itemView.textview_rv_recipe_methods_categories_items.isSelected = false
                    }
                    listener?.onItemClickChoose(data[bindingAdapterPosition])
                }
            }
        }
    }

    private fun selectItem(cardView: MaterialCardView) {
        cardView.isChecked = true
        cardView.isSelected = true
        cardView.strokeWidth = 5
    }

    private fun unselectItem(cardView: MaterialCardView) {
        cardView.isChecked = false
        cardView.isSelected = false
        cardView.strokeWidth = 0
    }
}
