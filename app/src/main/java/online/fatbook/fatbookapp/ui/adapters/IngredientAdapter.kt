package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.rv_ingredient.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.ui.listeners.OnIngredientItemClickListener

class IngredientAdapter :
    RecyclerView.Adapter<IngredientAdapter.ViewHolder>(), BindableAdapter<Ingredient> {

    private var data: List<Ingredient> = ArrayList()
    var listener: OnIngredientItemClickListener? = null
    var selectedIngredient: Ingredient? = Ingredient()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_ingredient, parent, false)
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

    fun setClickListener(listener: OnIngredientItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: Ingredient?) {
            if (selectedIngredient!!.title == value!!.title) {
                selectItem(
                    itemView.cardview_rv_ingredient,
                    itemView.textview_ingredient_title_rv_ingredient,
                    itemView.textview_ingredient_kcals_title_rv_ingredient
                )
            } else {
                unselectItem(
                    itemView.cardview_rv_ingredient,
                    itemView.textview_ingredient_title_rv_ingredient,
                    itemView.textview_ingredient_kcals_title_rv_ingredient
                )
            }

            itemView.textview_ingredient_title_rv_ingredient.text = value!!.title

            val tmp = value.units?.get(0)?.kcal.toString()
            itemView.textview_ingredient_kcals_title_rv_ingredient.text =
                String.format("%s kcal/100 gram", tmp)

            if (itemView.cardview_rv_ingredient.isClickable) {
                itemView.cardview_rv_ingredient.setOnClickListener {
                    listener!!.onIngredientClick(
                        data.indexOf(selectedIngredient),
                        bindingAdapterPosition,
                        value
                    )
                }
            }
        }
    }

    private fun selectItem(
        cardView: MaterialCardView,
        textView: TextView,
        textViewKcalGram: TextView
    ) {
        cardView.isClickable = false
        cardView.setBackgroundResource(R.drawable.select_ingredient_round_corner)
        textView.isSelected = true
        textViewKcalGram.isSelected = true
    }

    private fun unselectItem(
        cardView: MaterialCardView,
        textView: TextView,
        textViewKcalGram: TextView
    ) {
        cardView.isClickable = true
        cardView.setBackgroundResource(R.drawable.unselect_ingredient_round_corner)
        textView.isSelected = false
        textViewKcalGram.isSelected = false
    }
}
