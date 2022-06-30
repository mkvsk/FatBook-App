package online.fatbook.fatbookapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.Ingredient

class IngredientsAdapter(context: Context?, private var list: List<Ingredient?>?) :
    RecyclerView.Adapter<IngredientsAdapter.ViewHolder>(), BindableAdapter<Ingredient> {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.rv_ingredient, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = list!![position]
        holder.tvName.text = ingredient!!.name
    }

    override fun setData(data: List<Ingredient>?) {
        data.let {
            list = it as ArrayList<Ingredient>
            notifyDataSetChanged()
        }
    }

    fun updateList(list: List<Ingredient?>?) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.textView_ingredients_name)

    }

}