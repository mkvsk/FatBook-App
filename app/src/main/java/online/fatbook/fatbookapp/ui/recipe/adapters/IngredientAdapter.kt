package online.fatbook.fatbookapp.ui.recipe.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.databinding.RvIngredientBinding
import online.fatbook.fatbookapp.ui.recipe.listeners.OnIngredientItemClickListener
import online.fatbook.fatbookapp.util.BindableAdapter

class IngredientAdapter(private val context: Context) :
    RecyclerView.Adapter<IngredientAdapter.IngredientItemViewHolder>(),
    BindableAdapter<Ingredient> {

    private var data: List<Ingredient> = ArrayList()
    var listener: OnIngredientItemClickListener? = null
    var selectedIngredient: Ingredient? = Ingredient()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientItemViewHolder {
        val binding = RvIngredientBinding.inflate(LayoutInflater.from(context), parent, false)
        return IngredientItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientItemViewHolder, position: Int) {
        val ingredientItem = data[position]
        holder.bind(ingredientItem)
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

    inner class IngredientItemViewHolder(rvIngredientBinding: RvIngredientBinding) :
        RecyclerView.ViewHolder(rvIngredientBinding.root) {
        private val binding = rvIngredientBinding
        fun bind(ingredientItem: Ingredient?) {
            if (selectedIngredient!!.title == ingredientItem!!.title) {
                selectItem(
                    binding.cardviewRvIngredient,
                    binding.textviewIngredientTitleRvIngredient,
                    binding.textviewIngredientKcalsTitleRvIngredient
                )
            } else {
                unselectItem(
                    binding.cardviewRvIngredient,
                    binding.textviewIngredientTitleRvIngredient,
                    binding.textviewIngredientKcalsTitleRvIngredient
                )
            }

            binding.textviewIngredientTitleRvIngredient.text = ingredientItem.title

            binding.textviewIngredientKcalsTitleRvIngredient.text =
                "${ingredientItem.unitRatio!!.kcal} kcal/${ingredientItem.unitRatio.amount} ${ingredientItem.unitRatio.unit!!.title}"

            if (binding.cardviewRvIngredient.isClickable) {
                binding.cardviewRvIngredient.setOnClickListener {
                    listener!!.onIngredientClick(
                        data.indexOf(selectedIngredient),
                        bindingAdapterPosition,
                        ingredientItem
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
