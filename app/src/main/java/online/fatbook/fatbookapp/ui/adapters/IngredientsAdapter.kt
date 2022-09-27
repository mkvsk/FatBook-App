package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_ingredient.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient

class IngredientsAdapter :
    RecyclerView.Adapter<IngredientsAdapter.ViewHolder>(), BindableAdapter<Ingredient> {

    private var data: List<Ingredient> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_ingredient, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<Ingredient>?) {
        data?.let {
            this.data = it as ArrayList<Ingredient>
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(ingredient: Ingredient?) {
            itemView.textview_ingredient_title_rv_ingredient.text = ingredient!!.title
        }
    }

}
