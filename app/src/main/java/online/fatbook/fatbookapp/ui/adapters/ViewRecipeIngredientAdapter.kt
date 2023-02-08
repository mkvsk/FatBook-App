package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.databinding.RvAddNewRecipeIngredientsOldBinding
import online.fatbook.fatbookapp.ui.listeners.OnRecipeViewDeleteIngredientListener

class ViewRecipeIngredientAdapter :
    RecyclerView.Adapter<ViewRecipeIngredientAdapter.ViewHolder>(),
    BindableAdapter<RecipeIngredient> {
    private var _binding: RvAddNewRecipeIngredientsOldBinding? = null
    private val binding get() = _binding!!

    private var data: List<RecipeIngredient> = ArrayList()
    private var isEditAvailable = false
    private var listener: OnRecipeViewDeleteIngredientListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding =
            RvAddNewRecipeIngredientsOldBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setEditMode(allow: Boolean) {
        isEditAvailable = allow
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<RecipeIngredient>?) {
        data?.let {
            this.data = it
            notifyDataSetChanged()
        }
    }

    fun setClickListener(listener: OnRecipeViewDeleteIngredientListener) {
        this.listener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(recipeIngredient: RecipeIngredient) {
            binding.textViewIngredientInRecipe.text = recipeIngredient.ingredient!!.title
            val quantityName =
                recipeIngredient.quantity.toString() + " " + recipeIngredient.unit!!.title
            binding.textViewRvAddRecipeIngredientQuantity.text = quantityName
            if (isEditAvailable) {
                binding.btnRvAddRecipeIngredientRemove.visibility = View.VISIBLE
            } else {
                binding.btnRvAddRecipeIngredientRemove.visibility = View.INVISIBLE
            }
            binding.btnRvAddRecipeIngredientRemove.setOnClickListener {
                listener!!.onDeleteIngredientClick(data[adapterPosition], adapterPosition)
            }
        }
    }
}