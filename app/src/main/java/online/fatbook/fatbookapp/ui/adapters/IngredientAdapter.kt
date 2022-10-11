package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.rv_ingredient.view.*
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
            if (selectedItemPosition!! == bindingAdapterPosition) {
                selectItem(
                    itemView.cardview_rv_recipe_ingredient,
                    itemView.textview_ingredient_title_rv_ingredient
                )
            } else {
                unselectItem(
                    itemView.cardview_rv_recipe_ingredient,
                    itemView.textview_ingredient_title_rv_ingredient
                )
            }

            itemView.textview_ingredient_title_rv_ingredient.text = value!!.title

            itemView.textview_ingredient_title_rv_ingredient.setOnClickListener {

                if (itemView.cardview_rv_recipe_ingredient.isSelected
                    || itemView.textview_ingredient_title_rv_ingredient.isSelected
                    || itemView.textview_ingredient_kcals_title_rv_ingredient.isSelected
                ) {
                    selectItem(
                        itemView.cardview_rv_recipe_ingredient,
                        itemView.textview_ingredient_title_rv_ingredient
                    )
                } else {
                    unselectItem(
                        itemView.cardview_rv_recipe_ingredient,
                        itemView.textview_ingredient_title_rv_ingredient
                    )
                }
            }

        }
    }

    private fun selectItem(
        cardView: MaterialCardView,
        textView: TextView
    ) {
        cardView.isChecked = true
        cardView.isSelected = true
        textView.isSelected = true
    }

    private fun unselectItem(
        cardView: MaterialCardView,
        textView: TextView
    ) {
        cardView.isChecked = false
        cardView.isSelected = false
        textView.isSelected = false
    }
}
