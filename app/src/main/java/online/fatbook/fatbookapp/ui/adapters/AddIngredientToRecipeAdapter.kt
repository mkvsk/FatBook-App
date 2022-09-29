package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_ingredient_to_recipe_old.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredients
import online.fatbook.fatbookapp.ui.listeners.OnAddIngredientItemClickListener

class AddIngredientToRecipeAdapter :
    RecyclerView.Adapter<AddIngredientToRecipeAdapter.ViewHolder>(), BindableAdapter<Ingredients> {

    private var selectedItem = -1
    private var listener: OnAddIngredientItemClickListener? = null
    private var data: List<Ingredients> = ArrayList()

    fun setClickListener(listener: OnAddIngredientItemClickListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_ingredient_to_recipe_old, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(ingredient: Ingredients, position: Int) {
            itemView.textView_ingredient_to_recipe.text = ingredient.title
            itemView.rv_item_card.setCardBackgroundColor(itemView.context.resources.getColor(R.color.white))
            if (selectedItem == position) {
                itemView.rv_item_card.setCardBackgroundColor(itemView.context.resources.getColor(R.color.color_lime_500))
            }
            itemView.setOnClickListener {
                val previousItem = selectedItem
                selectedItem = adapterPosition
                listener!!.onIngredientClick(previousItem, selectedItem, data[adapterPosition])
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<Ingredients>?) {
        data?.let {
            this.data = it
            notifyDataSetChanged()
        }
    }

}