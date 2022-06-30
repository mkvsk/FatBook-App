package online.fatbook.fatbookapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.Ingredient
import online.fatbook.fatbookapp.ui.listeners.OnAddIngredientItemClickListener

class AddIngredientToRecipeAdapter(
    context: Context?,
    private var list: List<Ingredient?>?
) :
    RecyclerView.Adapter<AddIngredientToRecipeAdapter.ViewHolder>(), BindableAdapter<Ingredient> {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var selectedItem = -1
    private var listener: OnAddIngredientItemClickListener? = null
    fun setClickListener(listener: OnAddIngredientItemClickListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.rv_ingredient_to_recipe, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = list!![position]
        holder.tvIngredient.text = ingredient!!.name
        holder.cardView.setCardBackgroundColor(inflater.context.resources.getColor(R.color.white))
        if (selectedItem == position) {
            holder.cardView.setCardBackgroundColor(inflater.context.resources.getColor(R.color.color_lime_500))
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    fun updateList(list: List<Ingredient?>?) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvIngredient: TextView = itemView.findViewById(R.id.textView_ingredient_to_recipe)
        val cardView: CardView = itemView.findViewById(R.id.rv_item_card)

        init {
            itemView.setOnClickListener {
                val previousItem = selectedItem
                selectedItem = adapterPosition
                listener!!.onIngredientClick(previousItem, selectedItem, list!![adapterPosition])
            }
        }
    }

    override fun setData(data: List<Ingredient>?) {
        data.let {
            list = it as ArrayList<Ingredient>
            notifyDataSetChanged()
        }
    }

}