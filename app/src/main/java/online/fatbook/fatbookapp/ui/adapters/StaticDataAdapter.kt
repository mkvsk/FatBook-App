package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_recipe_methods_categories_items.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.core.recipe.StaticDataObject
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener
import online.fatbook.fatbookapp.ui.listeners.OnStaticDataClickListener

class StaticDataAdapter :
    RecyclerView.Adapter<StaticDataAdapter.ViewHolder>(), BindableAdapter<StaticDataObject> {

    private var data: List<StaticDataObject> = ArrayList()
    var listener: OnStaticDataClickListener? = null

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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: StaticDataObject?) {
            itemView.textview_rv_recipe_methods_categories_items.text = value!!.title

            itemView.textview_rv_recipe_methods_categories_items.setOnClickListener {
                if (value is CookingMethod) {
                    listener?.onItemClick(data[bindingAdapterPosition])
                } else {
                    listener?.onItemClickChoose(data[bindingAdapterPosition])
                }
            }
        }
    }
}
