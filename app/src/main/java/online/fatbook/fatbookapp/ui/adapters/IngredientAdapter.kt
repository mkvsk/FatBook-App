package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.ui.listeners.OnAddIngredientItemClickListener

class IngredientAdapter :
    RecyclerView.Adapter<IngredientAdapter.ViewHolder>(), BindableAdapter<Ingredient> {

    private var data: List<Ingredient> = ArrayList()
    var listener: OnAddIngredientItemClickListener? = null
    var selectedItemPosition: Int? = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_recipe_create_add_ingredients, parent, false)
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

    fun setClickListener(listener: OnAddIngredientItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setSelected(int: Int) {
        selectedItemPosition = int
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: Ingredient?) {



        }
    }

    private fun selectItem() {
//        cardView.isChecked = true
//        cardView.isSelected = true
//        cardView.strokeWidth = 5
    }

    private fun unselectItem() {
//        cardView.isChecked = false
//        cardView.isSelected = false
//        cardView.strokeWidth = 0
    }
}
