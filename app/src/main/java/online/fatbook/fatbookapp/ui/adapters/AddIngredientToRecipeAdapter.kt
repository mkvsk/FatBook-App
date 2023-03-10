package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.databinding.RvIngredientToRecipeOldBinding
import online.fatbook.fatbookapp.ui.listeners.OnIngredientItemClickListener

class AddIngredientToRecipeAdapter :
    RecyclerView.Adapter<AddIngredientToRecipeAdapter.ViewHolder>(), BindableAdapter<Ingredient> {
    private var _binding: RvIngredientToRecipeOldBinding? = null
    private val binding get() = _binding!!

    private var selectedItem = -1
    private var listener: OnIngredientItemClickListener? = null
    private var data: List<Ingredient> = ArrayList()

    fun setClickListener(listener: OnIngredientItemClickListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = RvIngredientToRecipeOldBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(
            binding.root
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(ingredient: Ingredient, position: Int) {
            binding.textViewIngredientToRecipe.text = ingredient.title
            binding.rvItemCard.setCardBackgroundColor(itemView.context.resources.getColor(R.color.white))
            if (selectedItem == position) {
                binding.rvItemCard.setCardBackgroundColor(itemView.context.resources.getColor(R.color.color_lime_500))
            }
            binding.rvItemCard.setOnClickListener {
                val previousItem = selectedItem
                selectedItem = adapterPosition
                listener!!.onIngredientClick(previousItem, selectedItem, data[adapterPosition])
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<Ingredient>?) {
        data?.let {
            this.data = it
            notifyDataSetChanged()
        }
    }

}