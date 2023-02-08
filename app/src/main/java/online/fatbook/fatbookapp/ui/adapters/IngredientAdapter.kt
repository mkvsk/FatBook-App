package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.databinding.RvIngredientBinding
import online.fatbook.fatbookapp.ui.listeners.OnIngredientItemClickListener

class IngredientAdapter :
    RecyclerView.Adapter<IngredientAdapter.ViewHolder>(), BindableAdapter<Ingredient> {
    private var _binding: RvIngredientBinding? = null
    private val binding get() = _binding!!

    private var data: List<Ingredient> = ArrayList()
    var listener: OnIngredientItemClickListener? = null
    var selectedIngredient: Ingredient? = Ingredient()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding =
            RvIngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
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

    fun setContext(context: Context) {
        this.context = context
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

            binding.textviewIngredientTitleRvIngredient.text = value.title

            binding.textviewIngredientKcalsTitleRvIngredient.text =
                "${value.unitRatio!!.kcal} kcal/${value.unitRatio.amount} ${value.unitRatio.unit!!.title}"

            if (binding.cardviewRvIngredient.isClickable) {
                binding.cardviewRvIngredient.setOnClickListener {
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
